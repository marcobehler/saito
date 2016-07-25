package com.marcobehler.saito.core.markdown;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.pegdown.PegDownProcessor;

import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.freemarker.FreemarkerRenderer;
import com.marcobehler.saito.core.rendering.Renderer;
import com.marcobehler.saito.core.rendering.RenderingModel;

import lombok.extern.slf4j.Slf4j;

/**
 * A Markdown renderer on steroids
 * <p>
 * See https://github.com/sirthias/pegdown
 */
@Singleton
@Slf4j
public class PegdownRenderer implements Renderer {

    private final FreemarkerRenderer freemarkerRenderer;

    @Inject
    public PegdownRenderer(FreemarkerRenderer freemarkerRenderer) {
        this.freemarkerRenderer = freemarkerRenderer;
    }

    @Override
    public boolean canRender(final Template template) {
        final String fileName = template.getRelativePath().toString().toLowerCase();
        // github markdown extensions https://github.com/github/markup/blob/b865add2e053f8cea3d7f4d9dcba001bdfd78994/lib/github/markups.rb#L1
        return fileName.endsWith(".markdown") || fileName.endsWith(".mdown") || fileName.endsWith(".mkdn") || fileName
                .endsWith(".mkd") || fileName.endsWith(".md");
    }

    @Override
    public String render(final Template template, final RenderingModel renderingModel) {
        final PegDownProcessor pegDownProcessor = new PegDownProcessor();
        final String markdown = template.getContent().getText();
        final String html = pegDownProcessor.markdownToHtml(markdown);

        return freemarkerRenderer.renderLayout(template.getLayout(), html, renderingModel);
    }
}
