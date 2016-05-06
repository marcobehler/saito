package com.marcobehler.saito.core.files;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcobehler.saito.core.configuration.ModelSpace;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class DataFile extends SaitoFile {

    private static final String DATA_FILE_EXTENSION = ".json";

    public DataFile(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
    }

    /**
     * Parses the .json file this class represents and makes its data available in Freemarker, as a shared variable.
     */
    public void process(ModelSpace modelSpace) {
        Map<String, Object> parsedData = parse();
        modelSpace.getFreemarkerConfig().mergeSharedVariableMap("data", parsedData);
    }

    /**
     * Parses the .json file and return its content as a map.
     * <p>
     * If the data file has the following path /data/my/dear/friends.json , the resulting map will look like
     * { my :{ dear: { friends: [content]}}}
     *
     * @return
     */
    Map<String, Object> parse() {
        Map<String, Object> result = new HashMap<>();
        try {
            Iterator<String> it = stripAndSplit(getRelativePath());
            Map<String, Object> currentMap = result;
            while (it.hasNext()) {
                String key = it.next();
                if (it.hasNext()) {
                    currentMap.put(key, currentMap = new HashMap<>());
                } else {
                    Map<? extends String, ?> dataAsMap = doParse();
                    currentMap.put(key, dataAsMap);
                }
            }
        } catch (IOException e) {
            log.error("Error parsing data file", e);
        }
        return result;
    }

    private Map<? extends String, ?> doParse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(getData(), new TypeReference<HashMap<String, Object>>() {
        });
    }

    private Iterator<String> stripAndSplit(Path path) {
        String strippedPath = PathUtils.stripExtension(path, DATA_FILE_EXTENSION);
        return PathUtils.splitByFileSeparator(strippedPath).iterator();
    }


}
