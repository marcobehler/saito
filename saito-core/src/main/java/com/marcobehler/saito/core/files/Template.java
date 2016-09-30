package com.marcobehler.saito.core.files;


import com.marcobehler.saito.core.domain.FrontMatter;
import com.marcobehler.saito.core.domain.TemplateContent;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
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


    public Template replaceAndClone(String regex, String replacement) {
        String replacedContent = getContent().getText().replaceFirst(regex, replacement);

        Template clone = new Template();
        clone.relativePath = getRelativePath();
        clone.sourceDirectory = getSourceDirectory();
        clone.frontmatter = frontmatter;
        clone.layout = layout;
        clone.content = new TemplateContent(replacedContent);
        try {
            clone.data = replacedContent.getBytes("UTF-8");
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

    public boolean isProxyPage() {
        return frontmatter.containsKey("current_page") && frontmatter.get("current_page").containsKey("proxy");
    }

    public String getProxyDataKey() {
        return (String) ((Map<String,Object>)frontmatter.get("current_page").get("proxy")).get("data");
    }

    public String getProxyPattern() {
        return (String) ((Map<String,Object>)frontmatter.get("current_page").get("proxy")).get("pattern");
    }

    public String getProxyAlias() {
        return (String) ((Map<String,Object>)frontmatter.get("current_page").get("proxy")).get("alias");
    }

}
