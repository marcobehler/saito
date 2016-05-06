package com.marcobehler.saito.core.freemarker;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.marcobehler.saito.core.rendering.RenderingModel;
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
    public String render(Template template, final RenderingModel renderingModel) {
        String renderedTemplate = renderTemplate(template, renderingModel);
        return renderLayout(template.getLayout(), renderedTemplate, renderingModel);
    }

    @SneakyThrows
    private String renderLayout(Layout layout, String renderedTemplate, RenderingModel renderingModel) {
        StringWriter w = new StringWriter();

        freemarker.template.Template template = templateLoader.get(layout);

        Map<String,Object> dataModel = new HashMap<>();
        dataModel.putAll(renderingModel.getParameters());
        dataModel.put("_saito_content_", renderedTemplate);
        template.process(dataModel, w);

        return w.toString();
    }

    @SneakyThrows
    private String renderTemplate(Template t, RenderingModel renderingModel) {
        StringWriter w = new StringWriter();

        freemarker.template.Template template = templateLoader.get(t);

        Map<String,Object> dataModel = new HashMap<>();
        dataModel.putAll(renderingModel.getParameters());
        template.process(dataModel, w);

        return w.toString();
    }


}
