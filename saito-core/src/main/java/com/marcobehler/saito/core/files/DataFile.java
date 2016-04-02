package com.marcobehler.saito.core.files;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcobehler.saito.core.freemarker.FreemarkerConfig;
import freemarker.template.Configuration;
import freemarker.template.DefaultMapAdapter;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class DataFile extends SaitoFile {


    public DataFile(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
    }

    public Map<String, Object> parseData() {
        Map<String, Object> result = new HashMap<>();
        try {

            Map<? extends String, ?> dataAsMap = new ObjectMapper().readValue(getContent(), new TypeReference<HashMap<String, Object>>() {});

            List<String> keys = stripExtension();
            Iterator<String> it = keys.iterator();

            Map<String,Object> currentMap = result;
            while (it.hasNext()) {
                String key = it.next();
                if (it.hasNext()) {
                    currentMap.put(key, currentMap = new HashMap<>());
                } else {
                    currentMap.put(key, dataAsMap);
                }
            }
        } catch (IOException e) {
            log.error("Error parsing data file", e);
        }
        return result;
    }

    private List<String> stripExtension() {
        String fileName = getRelativePath().toString();
        String fileNameWithoutJsonExtension = fileName.substring(0, fileName.toLowerCase().indexOf(".json"));
        return Arrays.asList(fileNameWithoutJsonExtension.split("[\\\\|/]"));
    }

    public void process() {
        Configuration freemarkerConfig = FreemarkerConfig.getInstance(getSourceDirectory().getParent()).getCfg();
        TemplateModel data = freemarkerConfig.getSharedVariable("data");
        if (data == null) {
            try {
                freemarkerConfig.setSharedVariable("data", new HashMap<>());
            } catch (TemplateModelException e) {
                log.error("Problem setting shared variable", e);
            }
        }

        data = freemarkerConfig.getSharedVariable("data");
        if (data instanceof DefaultMapAdapter) {
            Map<String,Object> underlyingMap = (Map<String, Object>) ((DefaultMapAdapter) data).getWrappedObject();
            underlyingMap.putAll(parseData());
        }
    }
}
