package com.marcobehler.saito.core.freemarker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.files.Layout;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultMapAdapter;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@Singleton
public class FreemarkerTemplateLoader {

    private Configuration freemarkerConfig;

    // wow, what a mess ;(
    private LoadingCache<com.marcobehler.saito.core.files.Template, Template> templatesCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<com.marcobehler.saito.core.files.Template, Template>() {
                        public Template load(com.marcobehler.saito.core.files.Template template) throws IOException {
                            String templatName = template.getRelativePath().getFileName().toString();
                            return new Template(templatName, template.getContent().getText(), freemarkerConfig);
                        }
                    });


    private LoadingCache<Layout, Template> layoutsCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<Layout, Template>() {
                        public Template load(Layout layout) throws IOException {
                            return new Template(layout.getName(), layout.getDataAsString(), freemarkerConfig);
                        }
                    });


    @Inject
    public FreemarkerTemplateLoader(@Named(PathsModule.WORKING_DIR) Path workingDir, Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
        initClassLoaders(workingDir);
    }

    protected void initClassLoaders(Path workingDirectory) {
        try {
            ClassTemplateLoader tl1 = new ClassTemplateLoader(Saito.class.getClassLoader(), "/");
            FileTemplateLoader tl2 = new FileTemplateLoader(workingDirectory.resolve("source").toFile());
            MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[]{tl1, tl2});
            freemarkerConfig.setTemplateLoader(mtl);
        } catch (IOException e) {
            log.error("Error setting Freemarker template loader", e);
        }
    }

    @SneakyThrows
    public Template getTemplate(Layout layout) {
        return layoutsCache.get(layout);
    }

    @SneakyThrows
    public Template getTemplate(com.marcobehler.saito.core.files.Template template) {
        return templatesCache.get(template);
    }

    @SuppressWarnings("unchecked")
    public synchronized void mergeSharedVariableMap(String key, Map<String, Object> parsedData) {
        TemplateModel data = freemarkerConfig.getSharedVariable(key);

        if (data == null) {
            try {
                // when freemarker adds a HashMap sharedvariable, it will wrap it inside a DefaultMapAdapter
                freemarkerConfig.setSharedVariable(key, new HashMap<>());
            } catch (TemplateModelException e) {
                log.error("Problem setting shared variable", e);
            }
        }
        data = freemarkerConfig.getSharedVariable(key);

        if (data instanceof DefaultMapAdapter) {
            Map<String, Object> underlyingMap = (Map<String, Object>) ((DefaultMapAdapter) data).getWrappedObject();
            underlyingMap.putAll(parsedData);
        } else {
            throw new IllegalStateException("Data variable : " + key + " is not a Map");
        }
    }
}
