package com.marcobehler.saito.core;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.marcobehler.saito.core.configuration.SaitoConfig;
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

    public void init(Path workingDirectory, String subDirectory) {
        Path workingDir = subDirectory != null ? workingDirectory.resolve(subDirectory) : workingDirectory;
        try {
            createDirectories(workingDir);
            createFiles(workingDir);

            log.info("Init complete!");
            log.info("Use 'saito process' to process your new site");
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
        log.info("create {}", Files.write(targetDir.resolve(classPathResource), content.getBytes()));
    }

    public void build(Path workingDirectory) {
        try {
            Path configFile = workingDirectory.resolve("config.yaml");
            SaitoConfig config = SaitoConfig.getOrDefault(configFile);

            // 1. scan-in ALL source files
            SaitoModel saitoModel = new SourceScanner().scan(workingDirectory);

            // 2. process them (e.g. merge templates with layouts, minify assets etc, save them to target dir)
            Path buildDir = getOrCreateDirectory(workingDirectory, "build");
            saitoModel.process(config, buildDir);

        } catch (IOException e) {
            log.warn("Error building site", e);
        }
    }

    private Path getOrCreateDirectory(Path parent, String subdir) throws IOException {
        Path directory = parent.resolve(subdir);
        if (!Files.exists(directory)) {
            log.info("create {}", Files.createDirectories(directory));
        }
        return directory;
    }

    public void clean(Path currentWorkingDir) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
