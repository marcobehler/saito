package com.marcobehler.saito.core.configuration;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.freemarker.FreemarkerConfig;
import dagger.Lazy;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
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
    private final Lazy<FreemarkerConfig> freemarkerConfig;

    // configuration properties

    private boolean directoryIndexes = false;
    private boolean relativeLinks = false;
    private boolean liveReloadEnabled = true;
    private Integer port = 8820;

    @Inject
    public SaitoConfig(@Named("configFile") Path configFile, Lazy<FreemarkerConfig> freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
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
        port = (Integer) map.getOrDefault("port", port);
    }
}
