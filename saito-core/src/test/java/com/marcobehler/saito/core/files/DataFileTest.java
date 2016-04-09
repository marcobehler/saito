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

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class DataFileTest extends BaseInMemoryFSTest {

    private String json = "{\n" +
            "  \"friends\": [\n" +
            "    \"Tom\",\n" +
            "    \"Hänsel\",\n" +
            "    \"Harry\"\n" +
            "  ]\n" +
            "}";

    @Test
    public void dataFile_can_return_json_data_as_map() throws IOException {
        Path peopleJson = fs.getPath("/dummy.json");
        Files.write(peopleJson, json.getBytes("UTF-8"));

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
        Files.write(peopleJson, json.getBytes("UTF-8"));

        DataFile dataFile = new DataFile(fs.getPath("/"), fs.getPath("usa/texas/people.json"));

        Map<String, Object> expected = new HashMap<>();
        expected.put("usa", Collections.singletonMap("texas", Collections.singletonMap("people", Collections.singletonMap("friends", Arrays.asList("Tom", "Hänsel", "Harry")))));

        Map<String,Object> data = dataFile.parse();
        assertThat(data).isEqualTo(expected);
    }
}
