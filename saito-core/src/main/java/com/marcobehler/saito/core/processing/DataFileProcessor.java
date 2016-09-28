package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.files.DataFile;
import com.marcobehler.saito.core.rendering.Model;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marco on 17.09.2016.
 */
@Singleton
public class DataFileProcessor implements Processor<DataFile> {

    /**
     * Parses the .json file this class represents and makes its data available in Freemarker, as a shared variable.
     */
    public void process(DataFile dataFile, Model model) {
        Map<String, Object> parsedData = dataFile.parse();

        ConcurrentHashMap<String, Object> params = model.getParameters();
        params.putIfAbsent("data", new HashMap<>());
        ((Map<String, Object>) params.get("data")).putAll(parsedData);
    }
}
