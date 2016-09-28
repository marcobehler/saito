package com.marcobehler.saito.core.watcher;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class SourceWatcher {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    @EqualsAndHashCode
    @Getter
    @RequiredArgsConstructor
    private static class FileChange {
        private final Path dir;
        private final Path relativePath;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public SourceWatcher(Path dir, boolean recursive) throws IOException {
        this.watcher = dir.getFileSystem().newWatchService();
        this.keys = new HashMap<>();
        this.recursive = recursive;

        if (recursive) {
            log.info("Watching for changes in {} ...\n", dir);
            registerAll(dir);
        } else {
            register(dir);
        }
    }


    private Map<String, Long> lastModified = new HashMap<>();

    /**
     * Process all events for keys queued to the watcher
     */
    public void start() {
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                if (kind == ENTRY_MODIFY && isNotTemporaryJetbrainsFile(child) && isNotDirectory(child)) {
                    Long previouslyModified = lastModified.getOrDefault(child.toString(), 0L);
                    long nowLastModified = child.toFile().lastModified();

                    if (nowLastModified - previouslyModified > 750) {
                        log.trace("{} {} {}", event.kind().name(), child, nowLastModified);
                        lastModified.put(child.toString(), nowLastModified);
                        onFileModified(dir.resolve(child));
                    }
                }


                if (kind == ENTRY_CREATE) {
                    onEntryCreate(dir.resolve(child));
                }

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        log.error("Error registering new sub-dir", x);
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    protected void onEntryCreate(Path createdFile) {
        log.info("File created {}", createdFile);
    }

    /**
     * Override in subclasses for custom behaviour
     *
     * @param modifiedFile
     */
    protected void onFileModified(Path modifiedFile) {
       log.info("Modified file {}", modifiedFile);
    }



    private boolean isNotDirectory(Path child) {
        return !Files.isDirectory(child);
    }

    private boolean isNotTemporaryJetbrainsFile(Path path) {
        return !path.toString().contains("___jb_tmp___") && !path.toString().contains("___jb_old___");
    }
}
