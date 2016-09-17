package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.events.FileEvent;
import com.marcobehler.saito.core.events.FileEventSubscriber;
import com.marcobehler.saito.core.files.Sources;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.livereload.LiveReloadServer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@Singleton
public class LiveReloadPlugin implements Plugin, FileEventSubscriber, TemplatePostProcessor {

    private static final String LIVE_RELOAD_TAG = "<script>document.write('<script src=\"http://' + (location.host || 'localhost').split(':')[0] + ':35729/livereload.js?snipver=1\"></' + 'script>')</script>";

    @Getter
    private LiveReloadServer liveReloadServer;

    private Boolean isEnabled = false;

    @Inject
    public LiveReloadPlugin() {}

    @Override
    public void start(Saito saito, Sources sources) {
        log.info("Starting Livereload");
        SaitoConfig config = saito.getRenderingModel().getSaitoConfig();
        isEnabled = config.isLiveReloadEnabled();

        if (isEnabled) {
            try {
                liveReloadServer = new LiveReloadServer();
                liveReloadServer.start();
            } catch (IOException e) {
                log.error("Problem starting LiveReload", e);
            }
        }
    }

    @Override
    public String postProcess(String rendered) {
        if (isEnabled) {
            return rendered.replaceFirst("</head>", LIVE_RELOAD_TAG + "</head>");
        }
        return rendered;
    }

    @Override
    public void onFileEvent(FileEvent event) {
        if (isEnabled) {
            liveReloadServer.triggerReload();
        }
    }

    @Override
    public Integer getOrder() {
        return 10;
    }
}
