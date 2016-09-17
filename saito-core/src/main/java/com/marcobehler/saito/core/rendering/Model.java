package com.marcobehler.saito.core.rendering;

import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.marcobehler.saito.core.configuration.SaitoConfig;

import com.marcobehler.saito.core.pagination.Paginator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
@Getter
public class Model {

    public static final String BUILD_TIME_PARAMETER = "saito_build_time";
    public static final String TEMPLATE_OUTPUT_PATH = "saito_output_path";

    private final SaitoConfig saitoConfig;
    private final ConcurrentHashMap<String,Object> parameters = new ConcurrentHashMap<>();

    @Inject
    public Model(SaitoConfig saitoConfig) {
        this.saitoConfig = saitoConfig;

        this.parameters.put(TEMPLATE_OUTPUT_PATH, new ThreadLocal<Path>());
        this.parameters.put(BUILD_TIME_PARAMETER, new Date());
        this.parameters.put("paginator", new Paginator());
    }


    public Model clone() {
        Model clone = new Model(saitoConfig);
        clone.parameters.putAll(this.parameters);
        return clone;
    }
}
