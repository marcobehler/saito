package com.marcobehler.saito.core;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SaitoTest {

    FileSystem fs;

    @Before
    public void before() {
        fs = Jimfs.newFileSystem(Configuration.unix());
    }

    @After
    public void after() throws IOException {
        if (fs != null) {
            fs.close();
        }
    }

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
    public void build_should_create_directory() {
        Path workingDirectory = Paths.get(".");
        Saito saito = new Saito();
        saito.init(workingDirectory, "nested");
        saito.build(workingDirectory.resolve("nested"));
    }
}
