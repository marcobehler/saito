package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Template;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
public class FreemarkerRenderer implements Renderer {

    private static final String LIVE_RELOAD_TAG = "<script>document.write('<script src=\"http://' + (location.host || 'localhost').split(':')[0] + ':35729/livereload.js?snipver=1\"></' + 'script>')</script>";

    private final SaitoConfig config; // TODO remove from here

    @Inject
    public FreemarkerRenderer(SaitoConfig config) {
        this.config = config;
    }

    public void render(Template template, Path targetFile) {
        try {
            String renderedTemplate = renderTemplate(template);
            String renderedLayout = renderLayout(template.getLayout(), renderedTemplate);
            String postProcessed = postProcess(renderedLayout);

            Files.write(targetFile, postProcessed.getBytes("UTF-8"));
            log.info("created {}", targetFile);
        } catch (IOException e) {
            log.error("Error processing file", e);
        }
    }

    @SneakyThrows
    private String renderLayout(Layout layout, String renderedTemplate) {
        StringWriter w = new StringWriter();
        freemarker.template.Template template = config.getFreemarkerConfig().get().getFreemarkerTemplate(layout, null);
        template.process(Collections.singletonMap("_saito_content_", renderedTemplate), w);
        return w.toString();
    }

    @SneakyThrows
    private String renderTemplate(Template template) {
        StringWriter w = new StringWriter();
        String templatName = template.getRelativePath().getFileName().toString();
        new freemarker.template.Template(templatName, template.getContent().getText(), config.getFreemarkerConfig().get().getCfg())
                .process(Collections.emptyMap(), w);
        return w.toString();
    }

    private String postProcess(String renderedLayout) {
        if (config.isLiveReloadEnabled()) {
            return renderedLayout.replace("</head>", LIVE_RELOAD_TAG + "</head>");
        } else {
            return renderedLayout;
        }
    }
}
