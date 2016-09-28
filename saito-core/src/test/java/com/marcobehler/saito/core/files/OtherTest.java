package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.processing.OtherFileProcessor;
import com.marcobehler.saito.core.processing.TargetPathFinder;
import com.marcobehler.saito.core.rendering.Model;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class OtherTest extends BaseInMemoryFSTest {



    @Test
    public void saitoFile_getDataAsString() throws IOException {
        SaitoConfig config = new SaitoConfig(null);
        OtherFileProcessor processor = new OtherFileProcessor(config, new TargetPathFinder(config, fs.getPath("/build")));

        Path image = Files.write(fs.getPath("/test.jpeg"), "myContent".getBytes());

        Other other = new Other(image.getParent(), image.getFileName());
        Path targetDirectory = fs.getPath("/dest");
        Files.createDirectories(targetDirectory);
        processor.process(other, new Model());

        assertThat(Files.exists(fs.getPath("/dest/test.jpeg")));
    }
}
