package com.marcobehler.saito.core.freemarker;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.files.Layout;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class FreemarkerConfig {

    @Getter
    private final Configuration cfg;

    private static final Object lock = new Object();
    private static volatile FreemarkerConfig instance;

    private FreemarkerConfig(Path workingDirectory) {
        this.cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
        cfg.setTagSyntax(freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        cfg.addAutoImport("saito", "saito.ftl");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLogTemplateExceptions(false);

        try {
            ClassTemplateLoader tl1 = new ClassTemplateLoader(Saito.class.getClassLoader(), "/");
            FileTemplateLoader tl2 = new FileTemplateLoader(workingDirectory.resolve("source").toFile());
            MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[]{tl1, tl2});
            cfg.setTemplateLoader(mtl);
        } catch (IOException e) {
            log.error("Error setting Freemarker template loader", e);
        }
    }

    public static FreemarkerConfig getInstance(Path workingDirectory) {
        FreemarkerConfig freemarkerConfig = instance;
        if (freemarkerConfig == null) {
            synchronized (lock) {
                freemarkerConfig = instance;
                if (freemarkerConfig == null) {
                    freemarkerConfig = new FreemarkerConfig(workingDirectory);
                    instance = freemarkerConfig;
                }
            }
        }
        return freemarkerConfig;
    }

    @SneakyThrows
    public Template getFreemarkerTemplate(Layout layout) {
        return new Template(layout.getName(), layout.getContent(), cfg);
    }
}
