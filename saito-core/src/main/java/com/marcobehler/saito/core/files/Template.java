package com.marcobehler.saito.core.files;


import com.marcobehler.saito.core.domain.FrontMatter;
import com.marcobehler.saito.core.domain.TemplateContent;
import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.rendering.Processors;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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


    public boolean shouldProcess() {
        return true;
    }

    public String getLayoutName() {
        Map<String, Object> frontMatter = getFrontmatter().getCurrentPage();
        return (String) frontMatter.getOrDefault("layout", "layout");
    }

}
