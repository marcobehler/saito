package com.marcobehler.saito.core.configuration;

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
import java.nio.file.Files;
import java.nio.file.Path;

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

    @Inject
    public SaitoConfig(@Named("configFile") Path configFile, Lazy<FreemarkerConfig> freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
        this.configFile = configFile;
        initializeFromYaml(configFile);
    }

    @SneakyThrows
    private void initializeFromYaml(@Named(PathsModule.CONFIG_FILE) Path configFile) {
        if (!Files.exists(configFile)) {
            log.info("No config file {} found - using defaults", configFile);
            return;
        }
        Representer r = new Representer();
        r.represent(this);
        Yaml yaml = new Yaml(r);
        yaml.load(new String(Files.readAllBytes(configFile), "UTF-8"));
    }
}
