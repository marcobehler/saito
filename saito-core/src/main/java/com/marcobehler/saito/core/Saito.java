package com.marcobehler.saito.core;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.marcobehler.saito.core.rendering.RenderingModel;
import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.files.Sources;
import com.marcobehler.saito.core.plugins.Plugin;
import com.marcobehler.saito.core.processing.SourceScanner;
import com.marcobehler.saito.core.rendering.RenderingEngine;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
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
    private final RenderingModel renderingModel;

    @Getter
    private final RenderingEngine engine;

    @Inject
    public Saito(final RenderingModel renderingModel, final @Named(PathsModule.WORKING_DIR) Path workDirectory, final @Named(PathsModule.SOURCES_DIR) Path sourcesDir, final RenderingEngine engine) {
        this.workingDir = workDirectory;
        this.sourcesDir = sourcesDir;
        this.renderingModel = renderingModel;
        this.engine = engine;
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
            Sources sources = new SourceScanner().scan(workingDir);

            // 2. process them (e.g. merge templates with layouts, minify assets etc, save them to target dir)
            Path buildDir = workingDir.resolve("build");
            if (!Files.exists(buildDir)) {
                log.info("create {}", Files.createDirectories(buildDir));
            }

            sources.process(renderingModel, buildDir, engine);

            if (plugins != null) {
                plugins.stream()
                        .forEach(plugin -> plugin.start(this));
            }

        } catch (IOException e) {
            log.warn("Error building site", e);
        }
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
