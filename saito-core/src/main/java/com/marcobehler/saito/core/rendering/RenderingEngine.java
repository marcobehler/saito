package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.configuration.ModelSpace;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.Template;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
public class RenderingEngine {

    private static final String LIVE_RELOAD_TAG = "<script>document.write('<script src=\"http://' + (location.host || 'localhost').split(':')[0] + ':35729/livereload.js?snipver=1\"></' + 'script>')</script>";

    private final SaitoConfig config;

    private final Set<Renderer> renderers;


    @Inject
    public RenderingEngine(SaitoConfig config, Set<Renderer> renderers) {
        this.config = config;
        this.renderers = renderers;
    }

    public void render(Template template, Path targetFile, ModelSpace modelSpace) {
        Renderer renderer = renderers.stream()
                .filter(r -> r.canRender(template))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));

        doRender(template, targetFile, renderer, modelSpace);
    }

    private void doRender(Template template, Path targetFile, Renderer renderer,  ModelSpace modelSpace) {
        try {
            String rendered = renderer.render(template, modelSpace);
            String postProcess = postProcess(rendered);
            Files.write(targetFile, postProcess.getBytes("UTF-8"));
            log.info("created {}", targetFile);
        } catch (Exception e) {
            log.error("error creating file {}", targetFile, e);
        }
    }


    private String postProcess(String renderedLayout) {
        if (config.isLiveReloadEnabled()) {
            return renderedLayout.replace("</head>", LIVE_RELOAD_TAG + "</head>");
        } else {
            return renderedLayout;
        }
    }
}
