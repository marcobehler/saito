package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.freemarker.FreemarkerTemplateLoader;
import dagger.Lazy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.StringWriter;
import java.util.Collections;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
public class FreemarkerRenderer implements Renderer {

    private final Lazy<FreemarkerTemplateLoader> config;

    @Inject
    public FreemarkerRenderer(Lazy<FreemarkerTemplateLoader> freemarkerConfig) {
        this.config = freemarkerConfig;
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
        freemarker.template.Template template = config.get().getTemplate(layout);
        template.process(Collections.singletonMap("_saito_content_", renderedTemplate), w);
        return w.toString();
    }

    @SneakyThrows
    private String renderTemplate(Template t) {
        StringWriter w = new StringWriter();
        freemarker.template.Template template = config.get().getTemplate(t);
        template.process(Collections.emptyMap(), w);
        return w.toString();
    }
}
