package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.watcher.SourceWatcher;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class FileWatcherPlugin implements Plugin {

    @Override
    public void start(Saito saito) {
        Path sourceDir = saito.getWorkingDir().resolve("source");
        new Thread(() -> {
            try {
                new SourceWatcher(sourceDir, true).setLiveReload(null).processEvents();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public Integer getOrder() {
        return 20;
    }
}