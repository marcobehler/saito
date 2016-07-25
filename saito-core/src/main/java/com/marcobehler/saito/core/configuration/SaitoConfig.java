package com.marcobehler.saito.core.configuration;

import com.marcobehler.saito.core.dagger.PathsModule;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Getter
@Setter
@Singleton
@Slf4j
public class SaitoConfig {

    private final Path configFile;


    // configuration properties

    private boolean directoryIndexes = false;
    private boolean relativeLinks = false;
    private boolean liveReloadEnabled = true;
    private boolean compressCss = false;
    private boolean compressJs = false;
    private String blogSourceDir = "source";

    private Integer port = 8820;

    @Inject
    public SaitoConfig(@Named("configFile") Path configFile) {
        this.configFile = configFile;
        initializeFromYaml(configFile);
    }


    @SneakyThrows
    private void initializeFromYaml(@Named(PathsModule.CONFIG_FILE) Path configFile) {
        if (configFile == null || !Files.exists(configFile)) {
            log.info("No config file {} found - using defaults", configFile);
            return;
        }
        Yaml yaml = new Yaml();

        Map<String, Object> map = (Map<String, Object>) yaml.load(new String(Files.readAllBytes(configFile), "UTF-8"));
        directoryIndexes = (boolean) map.getOrDefault("directoryIndexes", directoryIndexes);
        relativeLinks = (boolean) map.getOrDefault("relativeLinks", relativeLinks);
        liveReloadEnabled = (boolean) map.getOrDefault("liveReloadEnabled", liveReloadEnabled);
        blogSourceDir = (String) map.getOrDefault("blogSourceDir", blogSourceDir);
        compressCss = (boolean) map.getOrDefault("compressCss", compressCss);
        compressJs = (boolean) map.getOrDefault("compressJs", compressJs);
        port = (Integer) map.getOrDefault("port", port);
        }
        }
