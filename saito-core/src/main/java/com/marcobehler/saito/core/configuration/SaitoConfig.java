package com.marcobehler.saito.core.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Getter
@Setter
@Slf4j
public class SaitoConfig {

    private boolean directoryIndexes = false;

    public static SaitoConfig getOrDefault(Path path) {
        SaitoConfig config = new SaitoConfig();
        if (!Files.exists(path)) {
            return config;
        }

        try {
            Yaml yaml = new Yaml(new Constructor(SaitoConfig.class));
            config = (SaitoConfig) yaml.load(new String(Files.readAllBytes(path), "UTF-8"));
        } catch (Exception e) {
            log.error("Error reading config file", e);
        }

        return config;
    }

}
