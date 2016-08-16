package com.marcobehler.saito.core.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.DaggerTestSaito$$;
import com.marcobehler.saito.core.dagger.TestSaito$$;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 *
 */
public class BlogPostITest {

    @Test
    public void blog_post_should_be_processed_into_correct_directory() throws IOException, BlogPost.BlogPostFormattingException {
        final TestSaito$$ saito$$ = DaggerTestSaito$$.builder().build();
        Saito saito = saito$$.saito();

        final Path sourceDirectory = saito.getSourcesDir();

        String templateFileName = "2015-03-05-this-is-it.html.ftl";
        Files.write(sourceDirectory.resolve(templateFileName), ("---\n" + "layout: layout\n" + "---This is not a test").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(sourceDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        final BlogPost bp = new BlogPost(sourceDirectory, sourceDirectory.relativize(sourceDirectory.resolve(templateFileName)));
        bp.setLayout(new Layout(sourceDirectory, sourceDirectory.resolve(layoutFileName)));

        final Path buildDir = sourceDirectory.resolve("build");
        Files.createDirectories(buildDir);

        bp.process(saito.getRenderingModel(), buildDir, saito.getEngine());

        final Path yearDir = buildDir.resolve("2015");
        assertThat(Files.exists(yearDir)).isTrue();

        final Path monthDir = yearDir.resolve("03");
        assertThat(Files.exists(monthDir)).isTrue();

        final Path dayDir = monthDir.resolve("05");
        assertThat(Files.exists(dayDir)).isTrue();

        final Path indexFile = dayDir.resolve("this-is-it.html");
        assertThat(Files.exists(indexFile)).isTrue();
    }

}
