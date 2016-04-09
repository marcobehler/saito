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
public class LayoutTest extends BaseInMemoryFSTest {

    @Test
    public void getLayoutName() throws IOException {
        Path image = Files.write(fs.getPath("/myLayout.ftl"), "myContent".getBytes());
        Layout layout = new Layout(image.getParent(), image.getFileName());
        assertThat(layout.getName()).isEqualTo("myLayout");
    }
}
