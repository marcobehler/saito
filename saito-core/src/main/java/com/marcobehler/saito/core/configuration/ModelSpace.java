package com.marcobehler.saito.core.configuration;

import com.marcobehler.saito.core.freemarker.FreemarkerConfig;
import dagger.Lazy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
@Getter
public class ModelSpace {

    public static final String BUILD_TIME_PARAMETER = "saito_build_time";

    private final SaitoConfig saitoConfig;
    private final Lazy<FreemarkerConfig> freemarkerConfig;
    private final Map<String,Object> parameters = new HashMap<>();

    @Inject
    public ModelSpace(SaitoConfig saitoConfig, Lazy<FreemarkerConfig> freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
        this.saitoConfig = saitoConfig;

        this.parameters.put(BUILD_TIME_PARAMETER, new Date());
    }

}