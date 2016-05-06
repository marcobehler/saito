package com.marcobehler.saito.core.configuration;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.freemarker.FreemarkerTemplateLoader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
@Getter
public class ModelSpace {

    public static final String BUILD_TIME_PARAMETER = "saito_build_time";
    public static final String TEMPLATE_OUTPUT_PATH = "saito_output_path";


    private final Path workDirectory;
    private final SaitoConfig saitoConfig;
    private final FreemarkerTemplateLoader freemarkerConfig;
    private final Map<String,Object> parameters = new HashMap<>();


    @Inject
    public ModelSpace(SaitoConfig saitoConfig, FreemarkerTemplateLoader freemarkerConfig, @Named(PathsModule.WORKING_DIR) Path workDirectory) {
        this.freemarkerConfig = freemarkerConfig;
        this.saitoConfig = saitoConfig;
        this.workDirectory = workDirectory;

        this.parameters.put(TEMPLATE_OUTPUT_PATH, new ThreadLocal<Path>());
        this.parameters.put(BUILD_TIME_PARAMETER, new Date());
    }

}
