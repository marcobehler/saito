package com.marcobehler.saito.core.files;


import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.domain.FrontMatter;
import com.marcobehler.saito.core.domain.TemplateContent;
import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.rendering.RenderingEngine;
import com.marcobehler.saito.core.rendering.RenderingModel;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Template extends SaitoFile {

    static final String TEMPLATE_FILE_EXTENSION = ".ftl";

    @Getter
    private FrontMatter frontmatter;

    @Getter
    private TemplateContent content; // can be  HTML, asciidoc, md

    @Setter
    @Getter
    private Layout layout;

    public Template(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
        this.frontmatter = FrontMatter.of(getDataAsString());
        this.content = TemplateContent.of(getDataAsString());
    }

    private Template() {}


    public Template clone(String content) {
        Template clone = new Template();
        clone.relativePath = getRelativePath();
        clone.sourceDirectory = getSourceDirectory();
        clone.frontmatter = frontmatter;
        clone.layout = layout;
        clone.content = new TemplateContent(content);
        try {
            clone.data = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {}
        return clone;
    }

    public void process(RenderingModel renderingModel, Path targetDir, RenderingEngine engine) {
        if (layout == null) {
            throw new IllegalStateException("Layout must not be null");
        }

        if (!shouldProcess()) {
            return;
        }

        String outputPath = PathUtils.stripExtension(getOutputPath(), TEMPLATE_FILE_EXTENSION);
        Path targetFile = getTargetFile(renderingModel, targetDir, outputPath);

        ThreadLocal<Path> tl = (ThreadLocal<Path>) renderingModel.getParameters().get(RenderingModel.TEMPLATE_OUTPUT_PATH);
        tl.set(targetFile);

        try {
            engine.render(this, targetFile, renderingModel);
        } catch (PaginationException e) {
            paginate(renderingModel, targetDir, engine, e);
        }
    }

    private void paginate(RenderingModel renderingModel, Path targetDir, RenderingEngine engine, PaginationException e) {
        log.info("Starting to paginate ", e);

        int pages = e.getPages();
        final List<List<Object>> partitions = e.getPartitions();


        Path targetFile;

        for (int i = 0; i < pages; i++ ) {
            String outputPath = PathUtils.stripExtension(getOutputPath(), TEMPLATE_FILE_EXTENSION);
            RenderingModel clonedModel = renderingModel.clone();
            clonedModel.getParameters().put("_saito_pagination_content_", partitions.get(i));

            if (isDirectoryIndexEnabled(clonedModel.getSaitoConfig(), outputPath)) {
                if (i > 0) {

                }
                targetFile = getDirectoryIndexTargetFile(targetDir, outputPath); // resolve( i == 1 ? "" : "/pages/" + i + "/")
            } else {
                if (i > 0) {
                    outputPath = outputPath.replaceAll("(.*)(\\.html.*)", "$1-page" + (i+1) + "$2");
                }
                targetFile = getTargetFile(targetDir,outputPath );
            }

            String paginatedTemplate = content.getText().replaceFirst("(\\[@saito\\.paginate\\s+)(.+\\s?)(;.+\\])", "$1_saito_pagination_content_$3");
            Template clonedTemplate = this.clone(paginatedTemplate);
            engine.render(clonedTemplate, targetFile, clonedModel);
        }
    }



    protected boolean shouldProcess() {
        return true;
    }

    protected Path getTargetFile(final RenderingModel renderingModel, final Path targetDir, final String outputPath) {
        return isDirectoryIndexEnabled(renderingModel.getSaitoConfig(), outputPath)
                    ? getDirectoryIndexTargetFile(targetDir, outputPath)
                    : getTargetFile(targetDir, outputPath);
    }




    @SneakyThrows
    private Path getDirectoryIndexTargetFile(Path targetDir, String relativePath) {
        final FileSystem fs = targetDir.getFileSystem();
        relativePath = PathUtils.stripExtension(fs.getPath(relativePath), ".html");

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


    private boolean isDirectoryIndexEnabled(SaitoConfig config, String relativePath) {
        return config.isDirectoryIndexes() && !relativePath.endsWith("index.html"); // if the file is already called index.html, skip it
    }



    public String getLayoutName() {
        Map<String, Object> frontMatter = getFrontmatter().getCurrentPage();
        return (String) frontMatter.getOrDefault("layout", "layout");
    }

}
