package com.marcobehler.saito.core.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.rendering.Renderer;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
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
    public List<String> getSupportedExtensions() {
        return Arrays.asList("ftl");
    }

    @Override
    public String render(Template template, final Model model) {
        String renderedTemplate = renderTemplate(template, model);
        return renderLayout(template, renderedTemplate, model);
    }

    @SneakyThrows
    public String renderLayout(Template template, String renderedTemplate, Model model) {
        StringWriter w = new StringWriter();

        freemarker.template.Template freemarkerTemplate = templateLoader.get(template.getLayout());

        Map<String,Object> dataModel = new HashMap<>();
        dataModel.putAll(model);
        dataModel.putAll(template.getFrontmatter().replace(model));


        dataModel.put("_saito_content_", renderedTemplate);
        freemarkerTemplate.process(dataModel, w);

        return w.toString();
    }



    @SneakyThrows
    private String renderTemplate(Template t, Model model) {
        StringWriter w = new StringWriter();

        freemarker.template.Template template = templateLoader.get(t);

        try {
            Map<String,Object> dataModel = new HashMap<>();
            dataModel.putAll(model);
            dataModel.putAll(t.getFrontmatter().replace(model));

            template.process(dataModel, w);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof PaginationException) { // PaginationException gets wrapped, we have to unwrap it ehre
               throw cause;
            } else {
                throw e;
            }
        }
        return w.toString();
    }
}
