package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.plugins.TemplatePostProcessor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Set;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
public class Processors {

    private final Set<Renderer> renderers;
    private final Set<TemplatePostProcessor> templatePostProcessors;

    @Inject
    public Processors(Set<Renderer> renderers, Set<TemplatePostProcessor> templatePostProcessors) {
        this.templatePostProcessors = templatePostProcessors;
        this.renderers = renderers;
    }

    public void process(Path buildDir, SaitoFile source, RenderingModel renderingModel) {

    }

    public String render(Template template, RenderingModel renderingModel) {
        Renderer renderer = renderers.stream()
                .filter(r -> r.canRender(template))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));
        try {
            String rendered = renderer.render(template, renderingModel);
            return postProcess(rendered);
        } catch (PaginationException e) {
            throw e;
        }
    }

    private String postProcess(String renderedLayout) {
        String rendered = renderedLayout;
        for (TemplatePostProcessor each : templatePostProcessors) {
            rendered = each.postProcess(rendered);
        }
        return rendered;
    }


}
