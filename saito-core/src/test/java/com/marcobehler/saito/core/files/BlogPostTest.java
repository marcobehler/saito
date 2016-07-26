package com.marcobehler.saito.core.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.marcobehler.saito.core.BaseInMemoryFSTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class BlogPostTest extends BaseInMemoryFSTest  {

    @Test
    public void blog_post_should_parse_filename_correctly() throws IOException {
        final Path f = workingDirectory.resolve("2015-03-05-this-is-it.html.ftl");
        Files.write(f, "".getBytes());
        BlogPost post = new BlogPost(workingDirectory, f);

        assertThat(post.getYear()).isEqualTo("2015");
        assertThat(post.getMonth()).isEqualTo("03");
        assertThat(post.getDay()).isEqualTo("05");
        assertThat(post.getTitle()).isEqualTo("this-is-it");
    }

    @Test
    public void blog_post_should_parse_filename_correctly_independent_from_extension() throws IOException {
        final Path f = workingDirectory.resolve("1999-01-04-letmedoitagain.md.adoc");
        Files.write(f, "".getBytes());
        BlogPost post = new BlogPost(workingDirectory, f);

        assertThat(post.getYear()).isEqualTo("1999");
        assertThat(post.getMonth()).isEqualTo("01");
        assertThat(post.getDay()).isEqualTo("04");
        assertThat(post.getTitle()).isEqualTo("letmedoitagain");
    }
}
