package com.marcobehler.saito.core.files;


import com.marcobehler.saito.core.domain.FrontMatter;
import com.marcobehler.saito.core.domain.TemplateContent;
import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.rendering.RenderingEngine;
import com.marcobehler.saito.core.rendering.RenderingModel;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Template extends SaitoFile {


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

    @Override
    public Path getOutputPath() {
        final String blogPath = PathUtils.stripExtension(getRelativePath(), ".ftl");
        return relativePath.getFileSystem().getPath(blogPath);
    }


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

        Path targetFile = getTargetFile(targetDir, renderingModel);

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
            RenderingModel clonedModel = renderingModel.clone();
            clonedModel.getParameters().put("_saito_pagination_content_", partitions.get(i));
            e.setCurrentPage(i + 1);

            Path targetFile = getTargetFile(targetDir, renderingModel);

            String paginatedTemplate = content.getText().replaceFirst("(\\[@saito\\.paginate\\s+)(.+\\s?)(;.+\\])", "$1_saito_pagination_content_$3");
            Template clonedTemplate = this.clone(paginatedTemplate);
            engine.render(clonedTemplate, targetFile, clonedModel);
        }
    }

    protected boolean shouldProcess() {
        return true;
    }



    public String getLayoutName() {
        Map<String, Object> frontMatter = getFrontmatter().getCurrentPage();
        return (String) frontMatter.getOrDefault("layout", "layout");
    }

}
