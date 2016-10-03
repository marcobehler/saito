package com.marcobehler.saito.core.domain;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.files.Template;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class TemplateTest extends BaseInMemoryFSTest{

    @Test
    public void getExtensionTest() throws IOException {
        Path path = fs.getPath("/").resolve("hello.xml");
        Files.write(path, "<xml></xml>".getBytes());

        Template template = new Template(path.getParent(), path.getFileName());
        assertThat(template.getExtension()).isEqualTo(".xml");
    }

    @Test
    public void getExtensionsTest() throws IOException {
        Path path = fs.getPath("/").resolve("hello.xml.ftl");
        Files.write(path, "<xml></xml>".getBytes());

        Template template = new Template(path.getParent(), path.getFileName());
        assertThat(template.getExtension()).isEqualTo(".xml.ftl");
    }

    @Test
    public void getFileNameWithoutExtension() throws IOException {
        Path path = fs.getPath("/").resolve("hello.xml.ftl");
        Files.write(path, "<xml></xml>".getBytes());

        Template template = new Template(path.getParent(), path.getFileName());
        assertThat(template.getFileNameWithoutExtension()).isEqualTo("hello");
    }
}
