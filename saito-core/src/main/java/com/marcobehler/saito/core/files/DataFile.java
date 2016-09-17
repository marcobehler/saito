package com.marcobehler.saito.core.files;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.util.PathUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class DataFile extends SaitoFile {

    private static final String DATA_FILE_EXTENSION = ".json";

    public DataFile(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
    }

    @Override
    public Path getOutputPath() {
        return getRelativePath();
    }

    /**
     * Parses the .json file and return its content as a map.
     * <p>
     * If the data file has the following path /data/my/dear/friends.json , the resulting map will look like
     * { my :{ dear: { friends: [content]}}}
     *
     * @return
     */
    public Map<String, Object> parse() {
        Map<String, Object> result = new HashMap<>();
        try {
            Iterator<String> it = stripAndSplit(getRelativePath());
            Map<String, Object> currentMap = result;
            while (it.hasNext()) {
                String key = it.next();
                if (it.hasNext()) {
                    currentMap.put(key, currentMap = new HashMap<>());
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<? extends String, ?> dataAsMap = mapper.readValue(getData(), new TypeReference<HashMap<String, Object>>() {});
                    currentMap.put(key, dataAsMap);
                }
            }
        } catch (IOException e) {
            log.error("Error parsing data file", e);
        }
        return result;
    }


    private Iterator<String> stripAndSplit(Path path) {
        String strippedPath = PathUtils.stripExtension(path, DATA_FILE_EXTENSION);
        return PathUtils.splitByFileSeparator(strippedPath).iterator();
    }


}
