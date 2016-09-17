package com.marcobehler.saito.core.markdown;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.pegdown.PegDownProcessor;

import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.freemarker.FreemarkerRenderer;
import com.marcobehler.saito.core.rendering.Renderer;
import com.marcobehler.saito.core.rendering.Model;

import lombok.extern.slf4j.Slf4j;

/**
 * A Markdown renderer on steroids
 * <p>
 * See https://github.com/sirthias/pegdown
 */
@Singleton
@Slf4j
public class MarkdownRenderer implements Renderer {

    private final FreemarkerRenderer freemarkerRenderer;

    @Inject
    public MarkdownRenderer(FreemarkerRenderer freemarkerRenderer) {
        this.freemarkerRenderer = freemarkerRenderer;
    }

    @Override
    public List<String> getSupportedExtensions() {
        return Arrays.asList("markdown", "mdown", "mkdn", "mkd", "md");
    }

    @Override
    public String render(final Template template, final Model model) {
        final PegDownProcessor pegDownProcessor = new PegDownProcessor();
        final String markdown = template.getContent().getText();
        final String html = pegDownProcessor.markdownToHtml(markdown);

        return freemarkerRenderer.renderLayout(template, html, model);
    }
}
