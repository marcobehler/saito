package com.marcobehler.saito.core.freemarker;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.marcobehler.saito.core.files.Layout;

import dagger.Lazy;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * Keeps Saito layouts and templates in a guava cache, so that they do not get re-loaded on every render.
 *
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@Singleton
public class FreemarkerTemplateLoader {

    private Lazy<Configuration> freemarkerConfig;

    // wow, what a mess ;(
    private LoadingCache<com.marcobehler.saito.core.files.Template, Template> templatesCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<com.marcobehler.saito.core.files.Template, Template>() {
                        public Template load(com.marcobehler.saito.core.files.Template template) throws IOException {
                            String templatName = template.getRelativePath().getFileName().toString();
                            return new Template(templatName, template.getContent().getText(), freemarkerConfig.get());
                        }
                    });


    private LoadingCache<Layout, Template> layoutsCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<Layout, Template>() {
                        public Template load(Layout layout) throws IOException {
                            return new Template(layout.getName(), layout.getDataAsString(), freemarkerConfig.get());
                        }
                    });


    @Inject
    public FreemarkerTemplateLoader(Lazy<Configuration> freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    @SneakyThrows
    public Template get(Layout layout) {
        return layoutsCache.get(layout);
    }

    @SneakyThrows
    public Template get(com.marcobehler.saito.core.files.Template template) {
        return templatesCache.get(template);
    }
}
