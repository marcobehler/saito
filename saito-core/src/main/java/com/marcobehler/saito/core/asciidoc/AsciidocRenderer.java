package com.marcobehler.saito.core.asciidoc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.asciidoctor.Asciidoctor;

import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.freemarker.FreemarkerRenderer;
import com.marcobehler.saito.core.rendering.Renderer;
import com.marcobehler.saito.core.rendering.RenderingModel;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Singleton
@Slf4j
public class AsciidocRenderer implements Renderer {

    private final FreemarkerRenderer freemarkerRenderer;

    @Inject
    public AsciidocRenderer(FreemarkerRenderer freemarkerRenderer) {
        this.freemarkerRenderer = freemarkerRenderer;
    }

    @Override
    public List<String> getSupportedExtensions() {
        return Arrays.asList("asciidoc", "adoc", "asc");
    }

    @Override
    public String render(final Template template, final RenderingModel renderingModel) {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        final String asciidoc = template.getContent().getText();
        final String html = asciidoctor.convert(asciidoc, new HashMap<>());
        return freemarkerRenderer.renderLayout(template.getLayout(), html, renderingModel);
    }
}
