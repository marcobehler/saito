package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class OtherTest extends BaseInMemoryFSTest {

    @Test
    public void saitoFile_getDataAsString() throws IOException {
        Path image = Files.write(fs.getPath("/test.jpeg"), "myContent".getBytes());

        Other other = new Other(image.getParent(), image.getFileName());
        Path targetDirectory = fs.getPath("/dest");
        Files.createDirectories(targetDirectory);
        other.process(null, targetDirectory);

        assertThat(Files.exists(fs.getPath("/dest/test.jpeg")));
    }
}