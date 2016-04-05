package com.marcobehler.saito.core.configuration;

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
@Slf4j
@Singleton
public class SaitoConfig {

    private boolean directoryIndexes = false;
    private boolean relativeLinks = false;
    private boolean liveReloadEnabled = true;

    private final Path configFile;

    @Inject
    @SneakyThrows
    public SaitoConfig(@Named("configFile") Path configFile) {
        this.configFile = configFile;

        Representer r = new Representer();
        r.represent(this);
        Yaml yaml = new Yaml(r);
        yaml.load(new String(Files.readAllBytes(configFile), "UTF-8"));
    }
}
