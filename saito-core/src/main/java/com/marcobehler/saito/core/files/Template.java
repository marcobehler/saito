package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.domain.FrontMatter;
import com.marcobehler.saito.core.freemarker.FreemarkerConfig;
import com.marcobehler.saito.core.util.PathUtils;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class Template extends SaitoFile  {

    private static final String TEMPLATE_FILE_EXTENSION = ".ftl";

    private static final Pattern pattern = Pattern.compile("---(.*)---(.*)", Pattern.DOTALL);

    @Getter
    private final FrontMatter frontmatter;

    @Getter
    private String template; // can be  HTML, asciidoc, md

    @Setter
    private Layout layout;

    public Template(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
        this.frontmatter = FrontMatter.parse(getDataAsString());
        this.template = parseTemplate(getDataAsString());
    }

    private String parseTemplate(final String content) {
        if (content == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }


    public void process(SaitoConfig config, Path targetDir) {
        if (layout == null) {
            throw new IllegalStateException("Layout must not be null");
        }

        String relativePath = PathUtils.stripExtension(getRelativePath(), TEMPLATE_FILE_EXTENSION);
        Path targetFile;

        if (config.isDirectoryIndexes() && !relativePath.endsWith("index.html")) {
            targetFile = getDirectoryIndexFile(targetDir, relativePath);
        } else {
            targetFile = getTargetFile(targetDir, relativePath);
        }

        // TODO refactor
        try (BufferedWriter writer = Files.newBufferedWriter(targetFile, Charset.forName("UTF-8"))) {
            Map<String, Object> data = new HashMap<>();

            FreemarkerConfig i = FreemarkerConfig.getInstance();
            StringWriter w = new StringWriter();
            new freemarker.template.Template(getRelativePath().getFileName().toString(), template, i.getCfg()).process(Collections.emptyMap(), w);

            data.put("_saito_content_", w.toString());
            FreemarkerConfig.getInstance()
                    .getFreemarkerTemplate(layout)
                    .process(data, writer);
            log.info("created {}", targetFile);
        } catch (IOException | TemplateException e) {
            log.error("Error processing file", e);
        }
    }

    @SneakyThrows
    private Path getDirectoryIndexFile(Path targetDir, String relativePath) {
        relativePath = PathUtils.stripExtension(relativePath, ".html");
        Path dir = targetDir.resolve(relativePath);
        Path targetSubDir = Files.createDirectories(dir);
        return targetSubDir.resolve("index.html");
    }

    private Path getTargetFile(Path targetDir, String relativePath) {
        Path targetFile = targetDir.resolve(relativePath);
        if (!Files.exists(targetFile.getParent())) {
            try {
                Files.createDirectories(targetFile.getParent());
            } catch (IOException e) {
                log.error("Error creating directory", e);
            }
        }
        return targetFile;
    }

    public String getLayout() {
        Map<String, Object> frontMatter = getFrontmatter();
        return (String) frontMatter.getOrDefault("layout", "layout");
    }

}
