package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.files.Template;
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

    @Inject
    public TemplateProcessor(TargetPathFinder targetPathFinder, Set<Renderer> rendererers) {
        this.targetPathFinder = targetPathFinder;
        this.renderers = rendererers;
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
            Files.write(targetFile, rendered.getBytes("UTF-8"));
        } catch (IOException e) {
            log.error("Error writing file", e);
        }
    }



  /*  private void paginate(Model model, Path targetDir, Processors engine, PaginationException e) {
        log.info("Starting to paginate ", e);

        int pages = e.getPages();
        final List<List<Object>> partitions = e.getPartitions();

        for (int i = 0; i < pages; i++ ) {
            // TODO fix
            Model clonedModel = model.clone();
            clonedModel.getParameters().put("_saito_pagination_content_", partitions.get(i));
            e.setCurrentPage(i + 1);

            Path targetFile = getTargetFile(targetDir, model);

            String paginatedTemplate = content.getText().replaceFirst("(\\[@saito\\.paginate\\s+)(.+\\s?)(;.+\\])", "$1_saito_pagination_content_$3");
            Template clonedTemplate = this.clone(paginatedTemplate);
            String renderedString = engine.render(clonedTemplate, clonedModel);
            try {
                Files.write(targetFile, renderedString.getBytes("UTF-8"));
            } catch (IOException e1) {
                log.error("Error writing file", e1);
            }
        }
    }*/

}
