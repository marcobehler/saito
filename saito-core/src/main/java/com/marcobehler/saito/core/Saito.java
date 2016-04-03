package com.marcobehler.saito.core;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.freemarker.FreemarkerConfig;
import com.marcobehler.saito.core.processing.SourceScanner;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class Saito {

    /**
     * Creates the full Site Structure for a Saito project, including example files.
     *
     * @param targetDirectory the target directory
     * @param subDirectory optional subdirectory
     */
    public void init(Path targetDirectory, String subDirectory) {
        Path projectDirectory = subDirectory != null ? targetDirectory.resolve(subDirectory) : targetDirectory;
        try {
            createDirectories(projectDirectory);
            createFiles(projectDirectory);

            log.info("Init complete!");
            log.info("Use 'saito build' to build your new site");
        } catch (IOException e) {
            log.warn("Error creating directory", e);
        }
    }

    private void createDirectories(Path workingDir) throws IOException {
        log.info("create {}", Files.createDirectories(workingDir.resolve("source/images")));
        log.info("create {}", Files.createDirectories(workingDir.resolve("source/javascripts")));
        log.info("create {}", Files.createDirectories(workingDir.resolve("source/stylesheets")));
        log.info("create {}", Files.createDirectories(workingDir.resolve("source/layouts")));
        log.info("create {}", Files.createDirectories(workingDir.resolve("data")));
    }

    private void createFiles(Path workingDir) throws IOException {
        copyClasspathResourceToFile("index.html.ftl", workingDir.resolve("source"));
        copyClasspathResourceToFile("layout.ftl", workingDir.resolve("source/layouts"));
        copyClasspathResourceToFile("dummy.json", workingDir.resolve("data"));

        log.info("create {}", Files.write(workingDir.resolve("source/stylesheets/all.css"), "".getBytes()));
        log.info("create {}", Files.write(workingDir.resolve("source/javascripts/all.js"), "".getBytes()));
    }


    private void copyClasspathResourceToFile(String classPathResource, Path targetDir) throws IOException {
        URL url = Resources.getResource(classPathResource);
        String content = Resources.toString(url, Charsets.UTF_8);
        log.info("create {}", Files.write(targetDir.resolve(classPathResource), content.getBytes("UTF-8")));
    }

    /**
     * Builds a Saito project, i.e. taking in all the source files, templates, images etc. , processing them and putting them in the "build" directory.
     *
     * @param projectDir the target directory
     */
    public void build(Path projectDir) {
        try {
            log.info("Working dir {} ", projectDir);
            Path configFile = projectDir.resolve("config.yaml");
            SaitoConfig config = SaitoConfig.getOrDefault(configFile);

            FreemarkerConfig.getInstance().initClassLoaders(projectDir);

            // 1. scan-in ALL source files
            SaitoModel saitoModel = new SourceScanner().scan(projectDir);

            // 2. process them (e.g. merge templates with layouts, minify assets etc, save them to target dir)
            Path buildDir = projectDir.resolve("build");
            if (!Files.exists(buildDir)) {
                log.info("create {}", Files.createDirectories(buildDir));
            }
            saitoModel.process(config, buildDir);
        } catch (IOException e) {
            log.warn("Error building site", e);
        }
    }

    public void clean(Path currentWorkingDir) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
