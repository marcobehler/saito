package com.marcobehler.saito.core.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import javax.inject.Named;
import javax.inject.Singleton;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.util.LinkHelper;

import dagger.Module;
import dagger.Provides;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Module
@Slf4j
public class FreemarkerModule {

    @Singleton
    @Provides
    public static Configuration configuration(LinkHelper linkHelper, MultiTemplateLoader templateLoader) {
        try {
            freemarker.log.Logger.selectLoggerLibrary(Logger.LIBRARY_SLF4J);
            Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
            cfg.setTagSyntax(freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX);
            cfg.addAutoImport("saito", "saito.ftl");
            cfg.setSharedVariable("saitoLinkHelper", linkHelper);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLogTemplateExceptions(false);
            cfg.setTemplateLoader(templateLoader);
            return cfg;
        } catch (TemplateModelException | ClassNotFoundException e) {
            log.error("Error creating config", e);
            throw new IllegalStateException(e);
        }
    }

    @Singleton
    @Provides
    public MultiTemplateLoader classLoaders(@Named(PathsModule.SOURCES_DIR) Path sourcesDir) {
        try {
            ClassTemplateLoader tl1 = new ClassTemplateLoader(Saito.class.getClassLoader(), "/");
            Java7PathTemplateLoader tl2 = new Java7PathTemplateLoader(sourcesDir);
            return new MultiTemplateLoader(new TemplateLoader[] { tl1, tl2 });
        } catch (IOException e) {
            log.error("Error setting Freemarker template loader", e);
            throw new IllegalStateException(e);
        }
    }
}
