package com.marcobehler.saito.core.processing;

import com.github.slugify.Slugify;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.pagination.Page;
import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.plugins.TemplatePostProcessor;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.rendering.Renderer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by marco on 17.09.2016.
 */
@Slf4j
@Singleton
public class TemplateProcessor implements Processor<Template> {

    private final TargetPathFinder targetPathFinder;

    private final Set<Renderer> renderers;

    private final Set<TemplatePostProcessor> templatePostProcessor;

    @Inject
    public TemplateProcessor(TargetPathFinder targetPathFinder, Set<Renderer> rendererers, Set<TemplatePostProcessor> templatePostProcessors) {
        this.targetPathFinder = targetPathFinder;
        this.renderers = rendererers;
        this.templatePostProcessor = templatePostProcessors;
    }

    public void process(Template template, Model model) {
        if (template.getLayout() == null) {
            throw new IllegalStateException("Layout must not be null");
        }

        if (!template.shouldProcess()) {
            return;
        }

        if (template.isProxyPage()) {
            renderProxyPages(template, model);
        } else {
            renderNormalPage(template, model);
        }
    }

    private void renderNormalPage(Template template, Model model) {
        Path targetFile = targetPathFinder.find(template);

        Renderer renderer = renderers.stream()
                .filter(r -> r.canRender(template))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));

        doRender(renderer, template, model, targetFile);
    }


    private void renderProxyPages(Template template, Model model) {
        log.trace("Starting to render proxy pages for {}", template);
        String expression = template.getProxyDataKey();
        try {
            Collection<Object> data = (Collection<Object>) new PropertyUtilsBean().getProperty(model, expression);

            for (Object d : data) {
                Model clonedModel = model.clone();
                clonedModel.put(template.getProxyAlias(), d);

                String proxyPattern = template.getProxyPattern();
                String replacedProxyPattern = replaceProxyPattern(proxyPattern, d);

                Path targetFile = targetPathFinder.find(template, replacedProxyPattern);

                Renderer renderer = renderers.stream()
                        .filter(r -> r.canRender(template))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));
                doRender(renderer, template, clonedModel, targetFile);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace(); // TODO proper handling
        }
    }


    private void paginate(PaginationException paginationException, Model currentModel, Template template) {
        log.trace("Starting to paginate ", paginationException);

        for (int i = 1; i <= paginationException.getPages(); i++) {
            Page page = paginationException.toPage(i);

            Model clonedModel = currentModel.clone();
            clonedModel.setPaginationContent(page.getData());

            Path targetFile = targetPathFinder.find(template, page);

            Template clonedTemplate = template.replaceAndClone("(\\[@saito\\.paginate\\s+)(.+\\s?)(;.+\\])", "$1_saito_pagination_content_$3");

            Renderer renderer = renderers.stream()
                    .filter(r -> r.canRender(clonedTemplate))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));
            doRender(renderer, clonedTemplate, clonedModel, targetFile);
        }
    }


    private void doRender(Renderer renderer, Template template, Model clonedModel, Path targetFile) {
        try {
            String rendered = renderer.render(template, clonedModel);

            for (TemplatePostProcessor each : templatePostProcessor) {
                rendered = each.onBeforeWrite(targetFile, rendered);
            }

            Files.write(targetFile, rendered.getBytes("UTF-8"));
        } catch (PaginationException e) {
            paginate(e, clonedModel, template);
        } catch (IOException e) {
            log.error("Error writing file", e);
        }
    }


    private String replaceProxyPattern(String proxyPattern, Object data) {
        String result = proxyPattern;

        // 1. replace placeholders
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(proxyPattern);
        while (matcher.find()) {
            try {
                String variableName = matcher.group(1);
                Object dataValue = new PropertyUtilsBean().getProperty(data, variableName);
                log.warn("Could not find data value for proxy variable {}", variableName);
                if (dataValue != null) {
                    result = result.replaceAll("\\$\\{" + variableName + "\\}", dataValue.toString());
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }


        // 2. slugify
        Slugify slg = new Slugify();
        result = slg.slugify(result);

        return result;
    }

}
