package com.marcobehler.saito.core.configuration;

import com.marcobehler.saito.core.freemarker.FreemarkerConfig;
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
    private final FreemarkerConfig freemarkerConfig;

    @Inject
    @SneakyThrows
    public SaitoConfig(@Named("configFile") Path configFile, FreemarkerConfig freemarkerConfig) {

        this.freemarkerConfig = freemarkerConfig;

        this.configFile = configFile;

        parseYaml(configFile);
    }

    private void parseYaml(@Named("configFile") Path configFile) throws IOException {
        Representer r = new Representer();
        r.represent(this);
        Yaml yaml = new Yaml(r);
        yaml.load(new String(Files.readAllBytes(configFile), "UTF-8"));
    }
}
