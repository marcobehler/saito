package com.marcobehler.saito.core.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
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
    private boolean relativeLinks = false;

    public static SaitoConfig getOrDefault(Path path) {
        SaitoConfig config = new SaitoConfig();
        return Files.exists(path) ? parseYaml(path) : config;
    }

    @SneakyThrows
    private static SaitoConfig parseYaml(Path path) {
        Yaml yaml = new Yaml(new Constructor(SaitoConfig.class));
        return (SaitoConfig) yaml.load(new String(Files.readAllBytes(path), "UTF-8"));
    }

}
