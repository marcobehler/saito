package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.FileEvent;
import com.marcobehler.saito.core.files.FileEventSubscriber;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.livereload.LiveReloadServer;

import javax.inject.Singleton;
import java.io.IOException;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@Singleton
public class LiveReloadPlugin implements Plugin, FileEventSubscriber {

    @Getter
    private LiveReloadServer liveReloadServer;

    @Override
    public void start(Saito saito) {
        SaitoConfig config = saito.getSaitoConfig();
        if (config.isLiveReloadEnabled()) {
            try {
                liveReloadServer = new LiveReloadServer();
                liveReloadServer.start();
            } catch (IOException e) {
                log.error("Problem starting LiveReload", e);
            }
        }
    }

    @Override
    public void onFileEvent(FileEvent event) {
        liveReloadServer.triggerReload();
    }

    @Override
    public Integer getOrder() {
        return 10;
    }
}
