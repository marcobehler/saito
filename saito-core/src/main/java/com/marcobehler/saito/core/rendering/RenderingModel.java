package com.marcobehler.saito.core.rendering;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
public class RenderingModel {

    public static final String BUILD_TIME_PARAMETER = "saito_build_time";
    public static final String TEMPLATE_OUTPUT_PATH = "saito_output_path";

    private final SaitoConfig saitoConfig;
    private final Map<String,Object> parameters = new HashMap<>();

    @Inject
    public RenderingModel(SaitoConfig saitoConfig) {
        this.saitoConfig = saitoConfig;

        this.parameters.put(TEMPLATE_OUTPUT_PATH, new ThreadLocal<Path>());
        this.parameters.put(BUILD_TIME_PARAMETER, new Date());
        this.parameters.put("paginator", new Paginator());
    }
}
