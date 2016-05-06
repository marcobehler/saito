package com.marcobehler.saito.core.freemarker;

import javax.inject.Singleton;

import com.marcobehler.saito.core.util.LinkHelper;

import dagger.Module;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Module
@Slf4j
public class FreemarkerModule {

    @Singleton
    static Configuration configuration(LinkHelper linkHelper) {
        try {
            Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
            cfg.setTagSyntax(freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX);
            cfg.addAutoImport("saito", "saito.ftl");
            cfg.setSharedVariable("saitoLinkHelper", linkHelper);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLogTemplateExceptions(false);
            return cfg;
        } catch (TemplateModelException e) {
            log.error("Error creating config", e);
            throw new IllegalStateException(e);
        }
    }
}
