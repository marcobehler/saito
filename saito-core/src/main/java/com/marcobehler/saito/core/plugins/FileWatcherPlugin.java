package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.events.FileEvent;
import com.marcobehler.saito.core.events.FileEventSubscriber;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.watcher.SourceWatcher;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */

@Slf4j
@Singleton
public class FileWatcherPlugin implements Plugin {

    private final Set<FileEventSubscriber> subscribers;

    @Inject
    public FileWatcherPlugin(Set<FileEventSubscriber> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public void start(Saito saito, List<SaitoFile> sources) {
        Path sourceDir = saito.getWorkingDir().resolve("source");
        new Thread(() -> {
            try {
                new SourceWatcher(sourceDir, true) {
                    @Override
                    protected void onFileModified(Path modifiedFile) {
                        saito.incrementalBuild(modifiedFile);
                        subscribers.stream().forEach(s -> s.onFileEvent(new FileEvent(modifiedFile)));
                    }
                }.start();

            } catch (IOException e) {
                log.error("Error watching files", e);
            }
        }).start();
    }

    @Override
    public Integer getOrder() {
        return 20;
    }
}
