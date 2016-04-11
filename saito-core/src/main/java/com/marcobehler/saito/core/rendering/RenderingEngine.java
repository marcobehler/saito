package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.files.Template;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
    private final Path workingDir;
    private final Path buildDir;

    @Inject
    public RenderingEngine(SaitoConfig config, Set<Renderer> renderers,
            @Named(PathsModule.WORKING_DIR) Path workingDir,
            @Named(PathsModule.BUILD_DIR) Path buildDir) {
        this.config = config;
        this.renderers = renderers;
        this.workingDir = workingDir;
        this.buildDir = buildDir;
    }

    public void render(Template template, Path targetFile) {
        Renderer renderer = renderers.stream().filter(r -> r.canRender(template)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find renderer for template " + template));

        doRender(template, targetFile, renderer);
    }

    private void doRender(Template template, Path targetFile, Renderer renderer) {
        try {
            Map<String, Object> renderContext = new HashMap<>();
            final HashMap<Object, Object> internal = new HashMap<>();
            renderContext.put("saito_internal", internal);
            internal.put("workingDir", workingDir);
            internal.put("buildDir", buildDir);
            internal.put("targetFile", targetFile);

            String rendered = renderer.render(template, renderContext);
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
