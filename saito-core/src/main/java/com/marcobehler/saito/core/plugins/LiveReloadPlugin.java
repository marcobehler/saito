package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
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
public class LiveReloadPlugin implements Plugin {

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
    public Integer getOrder() {
        return 30;
    }
}
