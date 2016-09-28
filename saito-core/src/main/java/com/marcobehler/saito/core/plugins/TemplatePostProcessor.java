package com.marcobehler.saito.core.plugins;

/**
 * Created by marco on 17.09.2016.
 */
public interface TemplatePostProcessor {

    default public String postProcess(String rendered) {
        return rendered;
    }

}
