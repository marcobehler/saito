package com.marcobehler.saito.core.rendering;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.marcobehler.saito.core.pagination.Paginator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
@Getter
public class Model extends ConcurrentHashMap<String, Object>{

    public static final String BUILD_TIME_PARAMETER = "saito_build_time";
    public static final String TEMPLATE_OUTPUT_PATH = "saito_output_path";

    @Inject
    public Model() {
        put(TEMPLATE_OUTPUT_PATH, new ThreadLocal<Path>());
        put(BUILD_TIME_PARAMETER, new Date());
        put("paginator", Paginator.INSTANCE);
    }

    public Model clone() {
        Model clone = new Model();
        clone.putAll(this);
        return clone;
    }


    public void setPaginationContent(List<Object> data) {
        put("_saito_pagination_content_", data);
    }
}
