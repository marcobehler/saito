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
import java.util.Optional;
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
        Path targetFile = getTargetFile(renderingModel, targetDir, outputPath, Optional.empty());

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

        for (int i = 0; i < pages; i++ ) {
            // TODO fix
            String outputPath = PathUtils.stripExtension(getOutputPath(), TEMPLATE_FILE_EXTENSION);
            RenderingModel clonedModel = renderingModel.clone();
            clonedModel.getParameters().put("_saito_pagination_content_", partitions.get(i));
            e.setCurrentPage(i + 1);
            Path targetFile = getTargetFile(renderingModel, targetDir, outputPath, Optional.of(e));
            String paginatedTemplate = content.getText().replaceFirst("(\\[@saito\\.paginate\\s+)(.+\\s?)(;.+\\])", "$1_saito_pagination_content_$3");
            Template clonedTemplate = this.clone(paginatedTemplate);
            engine.render(clonedTemplate, targetFile, clonedModel);
        }
    }



    protected boolean shouldProcess() {
        return true;
    }


    // TODO fix paginationException
    protected Path getTargetFile(final RenderingModel renderingModel, final Path targetDir, final String outputPath,  Optional<PaginationException> paginationException) {
        return isDirectoryIndexEnabled(renderingModel.getSaitoConfig(), outputPath, paginationException)
                    ? getDirectoryIndexTargetFile(targetDir, outputPath, paginationException)
                    : getTargetFile(targetDir, outputPath,paginationException);
    }



    // TODO fix paginationException
    @SneakyThrows
    private Path getDirectoryIndexTargetFile(Path targetDir, String relativePath, Optional<PaginationException> pagination) {
        final FileSystem fs = targetDir.getFileSystem();
        relativePath = PathUtils.stripExtension(fs.getPath(relativePath), ".html");

        Path dir = targetDir.resolve(relativePath);
        if (pagination.isPresent() && pagination.get().getCurrentPage() > 1) {
            dir = dir.resolve("pages/" + pagination.get().getCurrentPage());
        }
        Path targetSubDir = Files.createDirectories(dir);

        return targetSubDir.resolve("index.html");
    }



    // TODO fix paginationException
    private Path getTargetFile(Path targetDir, String relativePath,  Optional<PaginationException> pagination) {
        if (pagination.isPresent() && pagination.get().getCurrentPage() > 1) {
            relativePath = relativePath.replaceAll("(.*)(\\.html.*)", "$1-page" + pagination.get().getCurrentPage() + "$2");
        }

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


    private boolean isDirectoryIndexEnabled(SaitoConfig config, String relativePath, Optional<PaginationException> pagination) {
        if (!pagination.isPresent()) {
            return config.isDirectoryIndexes() && !relativePath.endsWith("index.html"); // if the file is already called index.html, skip it
        } else {
            return config.isDirectoryIndexes() || pagination.get().getCurrentPage() > 1;
        }
    }



    public String getLayoutName() {
        Map<String, Object> frontMatter = getFrontmatter().getCurrentPage();
        return (String) frontMatter.getOrDefault("layout", "layout");
    }

}
