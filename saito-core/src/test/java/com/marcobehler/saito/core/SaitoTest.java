package com.marcobehler.saito.core;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SaitoTest extends AbstractInMemoryFileSystemTest{

    @Test
    public void init_should_create_directories() {
        Path workingDirectory = fs.getPath("/");
        new Saito().init(workingDirectory, null);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(fs.getPath("/source/images")).exists();
        softly.assertThat(fs.getPath("/source/javascripts")).exists();
        softly.assertThat(fs.getPath("/source/stylesheets")).exists();
        softly.assertThat(fs.getPath("/source/layouts")).exists();
        softly.assertThat(fs.getPath("/data")).exists();
        softly.assertAll();
    }

    @Test
    public void init_should_create_directories_in_sub_folder() {
        Path workingDirectory = fs.getPath("/");
        new Saito().init(workingDirectory, "nested");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(fs.getPath("/nested/source/images")).exists();
        softly.assertThat(fs.getPath("/nested/source/javascripts")).exists();
        softly.assertThat(fs.getPath("/nested/source/stylesheets")).exists();
        softly.assertThat(fs.getPath("/nested/source/layouts")).exists();
        softly.assertThat(fs.getPath("/nested/data")).exists();
        softly.assertAll();
    }

    @Test
    public void init_should_create_layouts() {
        Path workingDirectory = fs.getPath("/");
        new Saito().init(workingDirectory, null);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(fs.getPath("/source/layouts/layout.ftl")).exists();
        softly.assertAll();
    }


    @Test
    public void build_should_create_directory() throws IOException {
        Path workingDirectory = Paths.get("./a");
        Files.createDirectories(workingDirectory);
        Saito saito = new Saito();
        saito.init(workingDirectory, "nested");
        saito.build(workingDirectory.resolve("nested"));
    }
}
