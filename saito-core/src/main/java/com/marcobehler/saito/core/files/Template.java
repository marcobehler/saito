package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.configuration.ModelSpace;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.domain.FrontMatter;
import com.marcobehler.saito.core.domain.TemplateContent;
import com.marcobehler.saito.core.rendering.RenderingEngine;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Template extends SaitoFile {

    private static final String TEMPLATE_FILE_EXTENSION = ".ftl";

    @Getter
    private final FrontMatter frontmatter;

    @Getter
    private final TemplateContent content; // can be  HTML, asciidoc, md

    @Setter
    @Getter
    private Layout layout;

    public Template(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
        this.frontmatter = FrontMatter.of(getDataAsString());
        this.content = TemplateContent.of(getDataAsString());
    }

    public void process(ModelSpace config, Path targetDir, RenderingEngine engine) {
        if (layout == null) {
            throw new IllegalStateException("Layout must not be null");
        }

        String relativePath = PathUtils.stripExtension(getRelativePath(), TEMPLATE_FILE_EXTENSION);

        Path targetFile = isDirectoryIndexEnabled(config.getSaitoConfig(), relativePath)
                ? getDirectoryIndexTargetFile(targetDir, relativePath)
                : getTargetFile(targetDir, relativePath);

        engine.render(this, targetFile);
    }

    private boolean isDirectoryIndexEnabled(SaitoConfig config, String relativePath) {
        return config.isDirectoryIndexes() && !relativePath.endsWith("index.html"); // if the file is already called index.html, skip it
    }


    @SneakyThrows
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

    public String getLayoutName() {
        Map<String, Object> frontMatter = getFrontmatter();
        return (String) frontMatter.getOrDefault("layout", "layout");
    }

}
