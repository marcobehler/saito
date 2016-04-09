package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.AbstractInMemoryFileSystemTest;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SaitoFileTest extends AbstractInMemoryFileSystemTest{

    @Test
    public void saitoFile_getDataAsString() throws IOException {
        Path file = Files.write(fs.getPath("/test.yaml"), "myContent".getBytes());

        SaitoFile saitoFile = new SaitoFile(file.getParent(), file.getFileName());
        String data = saitoFile.getDataAsString();
        assertThat(data).isEqualToIgnoringWhitespace("myContent");
    }
}
