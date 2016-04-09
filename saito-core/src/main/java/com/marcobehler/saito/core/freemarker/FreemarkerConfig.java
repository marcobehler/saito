package com.marcobehler.saito.core.freemarker;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.files.*;
import com.marcobehler.saito.core.util.LinkHelper;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.*;
import freemarker.template.Template;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@Singleton
public class FreemarkerConfig {

    @Getter
    private Configuration cfg;

    // wow, what a mess ;(
    private LoadingCache<com.marcobehler.saito.core.files.Template, Template> templateCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<com.marcobehler.saito.core.files.Template, Template>() {
                        public Template load(com.marcobehler.saito.core.files.Template template) throws IOException {
                            String templatName = template.getRelativePath().getFileName().toString();
                            return new Template(templatName, template.getContent().getText(), cfg);
                        }
                    });


    private LoadingCache<Layout, Template> layoutCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<Layout, Template>() {
                        public Template load(Layout layout) throws IOException {
                            return new Template(layout.getName(), layout.getDataAsString(), cfg);
                        }
                    });


    @Inject
    public FreemarkerConfig(@Named(PathsModule.WORKING_DIR) Path workingDir) {
        this.cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
        try {
            cfg.setTagSyntax(freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX);
            cfg.addAutoImport("saito", "saito.ftl");
            cfg.setSharedVariable("saitoLinkHelper", new LinkHelper());
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLogTemplateExceptions(false);
        } catch (TemplateModelException e) {
            log.error("Error creating config",  e);
        }
        initClassLoaders(workingDir);
    }

    public void initClassLoaders(Path workingDirectory) {
        try {
            ClassTemplateLoader tl1 = new ClassTemplateLoader(Saito.class.getClassLoader(), "/");
            FileTemplateLoader tl2 = new FileTemplateLoader(workingDirectory.resolve("source").toFile());
            MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[]{tl1, tl2});
            cfg.setTemplateLoader(mtl);
        } catch (IOException e) {
            log.error("Error setting Freemarker template loader", e);
        }
    }


    @SneakyThrows
    public Template getFreemarkerTemplate(Layout layout) {
        return layoutCache.get(layout);
    }

    @SneakyThrows
    public Template getFreemarkerTemplate(com.marcobehler.saito.core.files.Template template) {
        return templateCache.get(template);
    }


    @SuppressWarnings("unchecked")
    public synchronized void mergeSharedVariableMap(String key, Map<String, Object> parsedData) {
        TemplateModel data = cfg.getSharedVariable(key);

        if (data == null) {
            try {
                // when freemarker adds a HashMap sharedvariable, it will wrap it inside a DefaultMapAdapter
                cfg.setSharedVariable(key, new HashMap<>());
            } catch (TemplateModelException e) {
                log.error("Problem setting shared variable", e);
            }
        }
        data = cfg.getSharedVariable(key);

        if (data instanceof DefaultMapAdapter) {
            Map<String, Object> underlyingMap = (Map<String, Object>) ((DefaultMapAdapter) data).getWrappedObject();
            underlyingMap.putAll(parsedData);
        } else {
            throw new IllegalStateException("Data variable : " + key + " is not a Map");
        }
    }
}
