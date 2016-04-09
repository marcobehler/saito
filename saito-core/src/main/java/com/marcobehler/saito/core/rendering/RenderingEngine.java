package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.files.Template;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Set;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
public class RenderingEngine {

    private final Set<Renderer> renderers;

    @Inject
    public RenderingEngine(Set<Renderer> renderers) {
        this.renderers = renderers;
    }

    public void render(Template template, Path targetFile) {
        // TODO only right as long as there is only a reemarker renderer :D
        // will be replaced with finding the right renderer for a specific template file
        renderers.stream().forEach(r -> render(template, targetFile));
    }
}
