package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.files.DataFile;
import com.marcobehler.saito.core.rendering.Model;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marco on 17.09.2016.
 */
public class DataFileProcessor implements Processor<DataFile> {

    private final Model model;

    @Inject
    public DataFileProcessor(Model model) {
        this.model = model;
    }

    /**
     * Parses the .json file this class represents and makes its data available in Freemarker, as a shared variable.
     */
    public void process(DataFile dataFile) {
        Map<String, Object> parsedData = dataFile.parse();

        ConcurrentHashMap<String, Object> params = model.getParameters();
        params.putIfAbsent("data", new HashMap<>());
        ((Map<String, Object>) params.get("data")).putAll(parsedData);
    }
}
