package com.marcobehler.saito.core.processing;

import com.github.slugify.Slugify;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.pagination.Page;
import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.pagination.Paginator;
import com.marcobehler.saito.core.plugins.TemplatePostProcessor;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.rendering.Renderer;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


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

        ThreadLocal<Path> tl = (ThreadLocal<Path>) model.get(Model.TEMPLATE_OUTPUT_PATH);
        tl.set(targetFile);

        Renderer renderer = getRenderer(template);
        try {
            doRender(renderer, template, model, targetFile);
        } catch (PaginationException e) {
            paginate(e, model, template, Optional.empty());
        }
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
                String replacedProxyPattern = replace(proxyPattern, d);

                Path targetFile = targetPathFinder.find(template, Optional.empty(), Optional.of(replacedProxyPattern));
                ThreadLocal<Path> tl = (ThreadLocal<Path>) model.get(Model.TEMPLATE_OUTPUT_PATH);
                tl.set(targetFile);

                Renderer renderer = getRenderer(template);
                try {
                    doRender(renderer, template, clonedModel, targetFile);
                } catch (PaginationException e) {
                    paginate(e, clonedModel, template, Optional.of(replacedProxyPattern));
                }
            }


            if (template.hasLocalProxyData()) {
                Model clonedModel = model.clone();
                clonedModel.put(template.getProxyAlias(), template.getLocalProxyData());

                Path targetFile = targetPathFinder.find(template);
                Renderer renderer = getRenderer(template);
                try {
                    doRender(renderer, template, clonedModel, targetFile);
                } catch (PaginationException e) {
                    paginate(e, clonedModel, template, Optional.empty());
                }
            }

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error rendering proxy pages", e);
        }
    }


    private void paginate(PaginationException paginationException, Model currentModel, Template template, Optional<String> templatePattern) {
        log.trace("Starting to paginate ", paginationException);

        for (int i = 1; i <= paginationException.getPages(); i++) {
            Page page = paginationException.toPage(i);

            Model clonedModel = currentModel.clone();
            clonedModel.setPaginationContent(page.getData());

            Path targetFile = targetPathFinder.find(template, Optional.of(page), templatePattern);

            Template clonedTemplate = template.replaceAndClone("(\\[@saito\\.paginate\\s+)(.+\\s?)(;.+\\])", "$1_saito_pagination_content_$3");

            Renderer renderer = renderers.stream()
                    .filter(r -> r.canRender(clonedTemplate))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));

            try {
                doRender(renderer, clonedTemplate, clonedModel, targetFile);
            } catch (PaginationException e) {
                throw new IllegalStateException("Pagination exception thrown during pagination");
            }

            if (i == paginationException.getPages()) {
                Paginator.INSTANCE.reset();
            }
        }
    }


    private void doRender(Renderer renderer, Template template, Model model, Path targetFile) throws PaginationException {
        try {

            String rendered = renderer.render(template, model);

            // =====================


            for (TemplatePostProcessor each : templatePostProcessor) {
                rendered = each.onBeforeWrite(targetFile, rendered);
            }

            Files.write(targetFile, rendered.getBytes("UTF-8"));
        } catch (IOException e) {
            log.error("Error writing file", e);
        }
    }


    private String replace(String variableString, Object data) {
        String result;

        // 1. process proxy
        StringWriter writer = new StringWriter();
        try {
            freemarker.template.Template t = new freemarker.template.Template(variableString, variableString, new Configuration(Configuration.VERSION_2_3_25));
            t.process(data, writer);
        } catch (TemplateException | IOException e) {
            log.error("Could not replace proxy pattern");
        }
        result = writer.toString();

        // 2. slugify
        Slugify slg = new Slugify();
        result = slg.slugify(result);

        return result;
    }

    private Renderer getRenderer(Template template) {
        return renderers.stream()
                .filter(r -> r.canRender(template))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));
    }

}
