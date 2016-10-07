package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.BaseInMemoryFSTest;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class DataFileTest extends BaseInMemoryFSTest {

    private String jsonObject = "{\n" +
            "  \"friends\": [\n" +
            "    \"Tom\",\n" +
            "    \"Hänsel\",\n" +
            "    \"Harry\"\n" +
            "  ]\n" +
            "}";


    private String jsonArray = "[\"hans\", \"meier\", \"geht\"]";

    @Test
    public void dataFile_can_return_json_data_as_map() throws IOException {
        Path peopleJson = fs.getPath("/dummy.json");
        Files.write(peopleJson, jsonObject.getBytes("UTF-8"));

        DataFile dataFile = new DataFile(fs.getPath("/"), fs.getPath("dummy.json"));

        Map<String, Object> expected = new HashMap<>();
        expected.put("dummy", Collections.singletonMap("friends", Arrays.asList("Tom", "Hänsel", "Harry")));

        Map<String,Object> data = dataFile.parse();
        assertThat(data).isEqualTo(expected);
    }


    @Test
    public void dataFile_can_return_json_data_as_map_with_deeper_filename() throws IOException {
        Path peopleJson = fs.getPath("/usa/texas/people.json");
        Files.createDirectories(peopleJson.getParent());
        Files.write(peopleJson, jsonObject.getBytes("UTF-8"));

        DataFile dataFile = new DataFile(fs.getPath("/"), fs.getPath("usa/texas/people.json"));

        Map<String, Object> expected = new HashMap<>();
        expected.put("usa", Collections.singletonMap("texas", Collections.singletonMap("people", Collections.singletonMap("friends", Arrays.asList("Tom", "Hänsel", "Harry")))));

        Map<String,Object> data = dataFile.parse();
        assertThat(data).isEqualTo(expected);
    }

    @Test
    public void dataFile_can_return_array() throws IOException {
        Path peopleJson = fs.getPath("/array.json");
        Files.createDirectories(peopleJson.getParent());
        Files.write(peopleJson, jsonArray.getBytes("UTF-8"));

        DataFile dataFile = new DataFile(fs.getPath("/"), fs.getPath("array.json"));

        Map<String, Object> expected = new HashMap<>();
        expected.put("array", Arrays.asList("hans", "meier", "geht"));

        Map<String,Object> data = dataFile.parse();
        assertThat(data).isEqualTo(expected);
    }

    
}
