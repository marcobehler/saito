package com.marcobehler.saito.core;

import org.assertj.core.api.SoftAssertions;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SaitoTest extends BaseInMemoryFSTest {


    @Test
    public void init_should_create_directories() {
        new Saito(null, null, workingDirectory, null, null).init(null);

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
        new Saito(null, null, workingDirectory,  null, null).init("nested");

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
        new Saito(null, null, workingDirectory, null,  null).init(null);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(fs.getPath("/source/layouts/layout.ftl")).exists();
        softly.assertAll();
    }

}
