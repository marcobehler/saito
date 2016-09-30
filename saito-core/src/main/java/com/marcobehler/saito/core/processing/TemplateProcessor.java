package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.pagination.Page;
import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.plugins.TemplatePostProcessor;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.rendering.Renderer;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        Path targetFile = targetPathFinder.find(template);

        Renderer renderer = renderers.stream()
                .filter(r -> r.canRender(template))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));
        try {
            String rendered = renderer.render(template, model);

            for (TemplatePostProcessor each: templatePostProcessor) {
                rendered = each.onBeforeTemplateWrite(targetFile, rendered);
            }

            Files.write(targetFile, rendered.getBytes("UTF-8"));
        } catch (PaginationException e) {
            paginate(e, model, template);
        } catch (IOException e) {
            log.error("Error writing file", e);
        }
    }


    private void paginate(PaginationException paginationException, Model currentModel, Template template) {
        log.info("Starting to paginate ", paginationException);

        for (int i = 1; i <= paginationException.getPages(); i++ ) {
            Page page = paginationException.toPage(i);

            Model clonedModel = currentModel.clone();
            clonedModel.setPaginationContent(page.getData());

            Path targetFile = targetPathFinder.find(template, page);

            Template clonedTemplate = template.replaceAndClone("(\\[@saito\\.paginate\\s+)(.+\\s?)(;.+\\])", "$1_saito_pagination_content_$3");

            Renderer renderer = renderers.stream()
                    .filter(r -> r.canRender(clonedTemplate))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));
            try {
                String rendered = renderer.render(clonedTemplate, clonedModel);

                for (TemplatePostProcessor each: templatePostProcessor) {
                    rendered = each.onBeforeTemplateWrite(targetFile, rendered);
                }

                Files.write(targetFile, rendered.getBytes("UTF-8"));
            } catch (IOException e) {
                log.error("Error writing file", e);
            }
        }
    }
}
