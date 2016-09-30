package com.marcobehler.saito.core.plugins;

import java.nio.file.Path;

/**
 * Created by marco on 17.09.2016.
 */
public interface TemplatePostProcessor {

    default String onBeforeWrite(Path targetFile, String rendered) {
        return rendered;
    }

}
