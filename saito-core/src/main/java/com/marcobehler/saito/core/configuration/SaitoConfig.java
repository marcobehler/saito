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

    private static final Object lock = new Object();
    private static volatile SaitoConfig INSTANCE;

    private boolean directoryIndexes = false;
    private boolean relativeLinks = false;

    public static SaitoConfig getOrDefault(Path path) {
        SaitoConfig result = INSTANCE;
        if (result == null) {
            synchronized (lock) {
                result = INSTANCE;
                if (result == null) {
                    SaitoConfig config = path != null && Files.exists(path) ? parseYaml(path) : new SaitoConfig();
                    INSTANCE = result = config;
                }
            }
        }
        return result;
    }

    static void reset() {
        INSTANCE = null;
    }

    @SneakyThrows
    private static SaitoConfig parseYaml(Path path) {
        Yaml yaml = new Yaml(new Constructor(SaitoConfig.class));
        return (SaitoConfig) yaml.load(new String(Files.readAllBytes(path), "UTF-8"));
    }

}
