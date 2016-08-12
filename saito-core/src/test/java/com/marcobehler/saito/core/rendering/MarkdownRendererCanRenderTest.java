package com.marcobehler.saito.core.rendering;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.markdown.MarkdownRenderer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
@RunWith(Parameterized.class)
public class MarkdownRendererCanRenderTest extends BaseInMemoryFSTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { ".markdown" }, { ".mdown" }, { ".mkdn" }, { ".mkd" }, { ".md" } });
    }

    private String extension;

    public MarkdownRendererCanRenderTest(String extension) {
        this.extension = extension;
    }

    @Test
    public void canRender_files_with_extension() throws IOException {
        String fileName = "ble" + extension;
        Files.write(workingDirectory.resolve(fileName), "test".getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath(fileName));
        boolean canRender = new MarkdownRenderer(null).canRender(saitoTemplate);
        assertThat(canRender).isTrue();
    }

}
