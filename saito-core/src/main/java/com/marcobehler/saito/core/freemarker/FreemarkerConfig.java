package com.marcobehler.saito.core.freemarker;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.files.Layout;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.*;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
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


    @Inject
    public FreemarkerConfig(@Named(PathsModule.WORKING_DIR) Path workingDir) {
        this.cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
        cfg.setTagSyntax(freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        cfg.addAutoImport("saito", "saito.ftl");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLogTemplateExceptions(false);

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
    public Template getFreemarkerTemplate(Layout layout, Function<String,String> modificationFunction) {
        String template = layout.getDataAsString();
        String modifiedTemplate = modificationFunction.apply(template);
        return new Template(layout.getName(), modifiedTemplate, cfg);
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
