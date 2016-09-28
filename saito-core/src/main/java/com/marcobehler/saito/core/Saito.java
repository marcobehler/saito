package com.marcobehler.saito.core;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.marcobehler.saito.core.files.DataFile;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.processing.Processor;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.plugins.Plugin;
import com.marcobehler.saito.core.processing.SourceScanner;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class Saito {

    @Getter
    private Path sourcesDir;

    @Getter
    private Path workingDir;

    @Getter
    private final Model model;

    @Getter
    private final Map<Class<? extends SaitoFile>, Processor<? extends SaitoFile>> processors;

    @Inject
    public Saito(final Model model, final @Named(PathsModule.WORKING_DIR) Path workDirectory, final @Named(PathsModule.SOURCES_DIR) Path sourcesDir, Map<Class<? extends SaitoFile>, Processor<? extends SaitoFile>> processors) {
        this.workingDir = workDirectory;
        this.sourcesDir = sourcesDir;
        this.model = model;
        this.processors = processors;
    }

    /**
     * Creates the full Site Structure for a Saito project, including example files.
     *
     * @param
     */
    public void init(String subDirectory) {
        if (subDirectory != null) {
            workingDir = workingDir.resolve(subDirectory);
        }

        try {
            createDirectories();
            createFiles();

            log.info("Init complete!");
            String subDir = subDirectory != null ? "'cd " + subDirectory + "' &&" : "";
            log.info("Use {} 'saito build' to build your new site", subDir);
        } catch (IOException e) {
            log.warn("Error creating directory", e);
        }
    }

    private void createDirectories() throws IOException {
        log.info("create {}", Files.createDirectories(workingDir.resolve("source/images")));
        log.info("create {}", Files.createDirectories(workingDir.resolve("source/javascripts")));
        log.info("create {}", Files.createDirectories(workingDir.resolve("source/stylesheets")));
        log.info("create {}", Files.createDirectories(workingDir.resolve("source/layouts")));
        log.info("create {}", Files.createDirectories(workingDir.resolve("data")));
    }

    private void createFiles() throws IOException {
        copyClasspathResourceToFile("index.html.ftl", workingDir.resolve("source"));
        copyClasspathResourceToFile("layout.ftl", workingDir.resolve("source/layouts"));
        copyClasspathResourceToFile("dummy.json", workingDir.resolve("data"));
        copyClasspathResourceToFile("cover.css", workingDir.resolve("source/stylesheets"));
        copyClasspathResourceToFile("config.yaml", workingDir);

        log.info("create {}", Files.write(workingDir.resolve("source/stylesheets/all.css"), "".getBytes()));
        log.info("create {}", Files.write(workingDir.resolve("source/javascripts/all.js"), "".getBytes()));
    }


    private void copyClasspathResourceToFile(String classPathResource, Path targetDir) throws IOException {
        URL url = Resources.getResource(classPathResource);
        String content = Resources.toString(url, Charsets.UTF_8);
        log.info("create {}", Files.write(targetDir.resolve(classPathResource), content.getBytes("UTF-8")));
    }


    public void build() {
        build(Collections.emptySet());
    }

    /**
     * Builds a Saito project, i.e. taking in all the source files, templates, images etc. , processing them and putting them in the "build" directory.
     */
    public void build(Set<Plugin> plugins) {
        try {
            log.info("Working dir {} ", workingDir);

            // 1. scan-in ALL source files
            List<? extends SaitoFile> files = new SourceScanner().scan(workingDir);

            // 2. sort files so that data files are always processed first
            files.sort((o1, o2) -> {
                boolean o1Data = o1 instanceof DataFile;
                boolean o2Data = o2 instanceof DataFile;
                if (o1Data) {
                    return -1;
                } else if (o2Data) {
                    return 1;
                } else {
                    return 0;
                }
            });

            Path buildDir = workingDir.resolve("build");
            if (!Files.exists(buildDir)) {
                log.info("create {}", Files.createDirectories(buildDir));
            }

            // 2. process them (e.g. merge templates with layouts, minify assets etc, save them to target dir)
            files.forEach(file -> {
                if (processors.containsKey(file.getClass())) {
                    Processor<? extends SaitoFile> processor = processors.get(file.getClass());
                    processor.process(rebox(file), model);
                }
            });

            if (plugins != null) {
                plugins.stream()
                        .forEach(plugin -> plugin.start(this, files));
            }

        } catch (IOException e) {
            log.warn("Error building site", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SaitoFile> T rebox(SaitoFile saitoFile) {
        return (T) saitoFile;
    }

    public void incrementalBuild(Path singleFile) {
        if (Files.isDirectory(singleFile)) {
            throw new IllegalArgumentException("You are trying to incrementally build a directory");
        }
        // TODO a real incremental build, at the moment i am cheating :D
        build();
    }

    public void clean() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
