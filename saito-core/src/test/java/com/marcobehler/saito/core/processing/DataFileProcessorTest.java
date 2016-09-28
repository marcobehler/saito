package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.DataFile;
import com.marcobehler.saito.core.rendering.Model;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by BEHLEMA on 28.09.2016.
 */
public class DataFileProcessorTest extends BaseInMemoryFSTest {

    private DataFileProcessor processor = new DataFileProcessor();

    @Test
    public void multipleDataFiles_dont_screwup_renderingModel() throws IOException {
        final Model model = new Model();

        Path dummyJson = fs.getPath("/dummy.json");
        Files.write(dummyJson, "{\"friends\" : [\"johnny\", \"b\"]}".getBytes("UTF-8"));

        Path peopleJson = fs.getPath("/people.json");
        Files.write(peopleJson, "{ \"yes\": \"mam\"}".getBytes("UTF-8"));

        DataFile dataFile = new DataFile(fs.getPath("/"), fs.getPath("dummy.json"));
        processor.process(dataFile, model);

        DataFile dataFile2 = new DataFile(fs.getPath("/"), fs.getPath("people.json"));
        processor.process(dataFile2, model);


        assertThat(model).containsKey("data");

        final Map<String,Object> data = (Map<String, Object>) model.get("data");
        assertThat(data).hasSize(2);
        assertThat(data).containsKey("dummy");
        assertThat(data).containsKey("people");
    }
}
