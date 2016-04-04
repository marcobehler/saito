package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.domain.FrontMatter;
import com.marcobehler.saito.core.domain.TemplateContent;
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
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class Template extends SaitoFile {

    private static final String TEMPLATE_FILE_EXTENSION = ".ftl";

    @Getter
    private final FrontMatter frontmatter;

    @Getter
    private final TemplateContent content; // can be  HTML, asciidoc, md

    @Setter
    private Layout layout;

    public Template(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
        this.frontmatter = FrontMatter.parse(getDataAsString());
        this.content = TemplateContent.parseTemplate(getDataAsString());
    }

    public void process(SaitoConfig config, Path targetDir) {
        if (layout == null) {
            throw new IllegalStateException("Layout must not be null");
        }

        String relativePath = PathUtils.stripExtension(getRelativePath(), TEMPLATE_FILE_EXTENSION);

        Path targetFile = isDirectoryIndexEnabled(config, relativePath)
                ? getDirectoryIndexTargetFile(targetDir, relativePath)
                : getTargetFile(targetDir, relativePath);

        writeTargetFile(config, targetFile);
    }

    private boolean isDirectoryIndexEnabled(SaitoConfig config, String relativePath) {
        return config.isDirectoryIndexes() && !relativePath.endsWith("index.html"); // if the file is already called index.html, skip it
    }

    private void writeTargetFile(SaitoConfig config, Path targetFile) {
        // TODO refactor
        try (BufferedWriter writer = Files.newBufferedWriter(targetFile, Charset.forName("UTF-8"))) {
            Map<String, Object> data = new HashMap<>();

            FreemarkerConfig i = FreemarkerConfig.getInstance();
            StringWriter w = new StringWriter();
            new freemarker.template.Template(getRelativePath().getFileName().toString(), content.getText(), i.getCfg()).process(Collections.emptyMap(), w);

            data.put("_saito_content_", w.toString());
            FreemarkerConfig.getInstance()
                    .getFreemarkerTemplate(layout, template -> {
                        if (config.isLiveReloadEnabled()) {
                            return template.replace("</head>", "<script>document.write('<script src=\"http://' + (location.host || 'localhost').split(':')[0] + ':35729/livereload.js?snipver=1\"></' + 'script>')</script></head>");
                        } else {
                            return template;
                        }
                    })
                    .process(data, writer);
            log.info("created {}", targetFile);
        } catch (IOException | TemplateException e) {
            log.error("Error processing file", e);
        }
    }

    @SneakyThrows
    // TODO refactor
    private Path getDirectoryIndexTargetFile(Path targetDir, String relativePath) {
        relativePath = PathUtils.stripExtension(Paths.get(relativePath), ".html");

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
