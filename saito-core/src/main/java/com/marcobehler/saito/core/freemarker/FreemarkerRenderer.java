package com.marcobehler.saito.core.freemarker;

import java.io.StringWriter;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.rendering.Renderer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
public class FreemarkerRenderer implements Renderer {

    private final FreemarkerTemplateLoader templateLoader;

    @Inject
    public FreemarkerRenderer(FreemarkerTemplateLoader freemarkerConfig) {
        this.templateLoader = freemarkerConfig;
    }

    @Override
    public boolean canRender(Template template) {
        return template.getRelativePath().toString().toLowerCase().endsWith(".ftl");
    }

    @Override
    public String render(Template template) {
        String renderedTemplate = renderTemplate(template);
        return renderLayout(template.getLayout(), renderedTemplate);
    }

    @SneakyThrows
    private String renderLayout(Layout layout, String renderedTemplate) {
        StringWriter w = new StringWriter();
        freemarker.template.Template template = templateLoader.get(layout);
        template.process(Collections.singletonMap("_saito_content_", renderedTemplate), w);
        return w.toString();
    }

    @SneakyThrows
    private String renderTemplate(Template t) {
        StringWriter w = new StringWriter();
        freemarker.template.Template template = templateLoader.get(t);
        template.process(Collections.emptyMap(), w);
        return w.toString();
    }
}
