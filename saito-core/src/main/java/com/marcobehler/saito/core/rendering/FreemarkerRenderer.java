package com.marcobehler.saito.core.rendering;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.freemarker.FreemarkerConfig;

import dagger.Lazy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
public class FreemarkerRenderer implements Renderer {

    private final Lazy<FreemarkerConfig> config;

    @Inject
    public FreemarkerRenderer(Lazy<FreemarkerConfig> freemarkerConfig) {
        this.config = freemarkerConfig;
    }

    @Override
    public boolean canRender(Template template) {
        return template.getRelativePath().toString().toLowerCase().endsWith(".ftl");
    }

    @Override
    public String render(Template template, final Map<String, Object> renderContext) {
        String renderedTemplate = renderTemplate(template, renderContext);
        return renderLayout(template.getLayout(), renderedTemplate, renderContext);
    }

    @SneakyThrows
    private String renderLayout(Layout layout, String renderedTemplate, final Map<String, Object> renderContext) {
        renderContext.putAll(Collections.singletonMap("_saito_content_", renderedTemplate));

        StringWriter w = new StringWriter();
        freemarker.template.Template template = config.get().getFreemarkerTemplate(layout);
        template.process(renderContext, w);
        return w.toString();
    }

    @SneakyThrows
    private String renderTemplate(Template t, final Map<String, Object> renderContext) {
        renderContext.putAll(getTemplateData(t));

        StringWriter w = new StringWriter();
        freemarker.template.Template template = config.get().getFreemarkerTemplate(t);
        template.process(getTemplateData(t), w);
        return w.toString();
    }

    private Map<String, Object> getTemplateData(Template t) {
        Map<String, Object> result = new HashMap<>();

        final HashMap<Object, Object> currentPage = new HashMap<>();
        result.put("current_page", currentPage);

        final HashMap<Object, Object> data = new HashMap<>();
        data.putAll(t.getFrontmatter());
        currentPage.put("data", data);

        return result;
    }
}
