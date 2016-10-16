package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.dagger.DaggerTestSaito$$;
import com.marcobehler.saito.core.dagger.TestSaito$$;
import com.marcobehler.saito.core.processing.TargetPathFinder;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 *
 */
public class BlogPostITest {


    @Test
    public void blog_post_should_be_processed_into_correct_directory_with_filename() throws IOException, BlogPost.BlogPostFormattingException {
        final TestSaito$$ saito$$ = DaggerTestSaito$$.builder().build();
        TargetPathFinder targetPathFinder = saito$$.targetPathFinder();

        final Path sourceDirectory = saito$$.sourcesDir();

        String templateFileName = "2015-03-05-this-is-it.html.ftl";
        Files.write(sourceDirectory.resolve(templateFileName), ("---\n" + "layout: layout\n" + "---This is not a test").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(sourceDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        final BlogPost bp = new BlogPost(sourceDirectory, sourceDirectory.relativize(sourceDirectory.resolve(templateFileName)));
        bp.setLayout(new Layout(sourceDirectory, sourceDirectory.resolve(layoutFileName)));

        final Path buildDir = saito$$.buildDir();

        targetPathFinder.find(bp, model);

        final Path yearDir = buildDir.resolve("2015");
        assertThat(Files.exists(yearDir)).isTrue();

        final Path monthDir = yearDir.resolve("03");
        assertThat(Files.exists(monthDir)).isTrue();

        final Path dayDir = monthDir.resolve("05");
        assertThat(Files.exists(dayDir)).isTrue();
    }

    @Test
    public void blog_post_should_be_processed_into_correct_directory_with_directory_index() throws IOException, BlogPost.BlogPostFormattingException {
        final TestSaito$$ saito$$ = DaggerTestSaito$$.builder().build();
        TargetPathFinder targetPathFinder = saito$$.targetPathFinder();
        targetPathFinder.getSaitoConfig().setDirectoryIndexes(true);

        final Path sourceDirectory = saito$$.sourcesDir();

        String templateFileName = "2015-03-05-this-is-it.html.ftl";
        Files.write(sourceDirectory.resolve(templateFileName), ("---\n" + "layout: layout\n" + "---This is not a test").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(sourceDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        final BlogPost bp = new BlogPost(sourceDirectory, sourceDirectory.relativize(sourceDirectory.resolve(templateFileName)));
        bp.setLayout(new Layout(sourceDirectory, sourceDirectory.resolve(layoutFileName)));

        final Path buildDir = saito$$.buildDir();

        targetPathFinder.find(bp, model);

        final Path yearDir = buildDir.resolve("2015");
        assertThat(Files.exists(yearDir)).isTrue();

        final Path monthDir = yearDir.resolve("03");
        assertThat(Files.exists(monthDir)).isTrue();

        final Path dayDir = monthDir.resolve("05");
        assertThat(Files.exists(dayDir)).isTrue();

        final Path thisIsItDir = dayDir.resolve("this-is-it");
        assertThat(Files.exists(thisIsItDir)).isTrue();

    }


    @Test
    public void blog_draft_should_not_be_processed() throws IOException, BlogPost.BlogPostFormattingException {
        final TestSaito$$ saito$$ = DaggerTestSaito$$.builder().build();
        TargetPathFinder targetPathFinder = saito$$.targetPathFinder();
        targetPathFinder.getSaitoConfig().setDirectoryIndexes(true);

        final Path sourceDirectory = saito$$.sourcesDir();

        String templateFileName = "2015-03-05-this-is-it.html.ftl";
        Files.write(sourceDirectory.resolve(templateFileName), ("---\n" + "layout: layout\npublished: false\n" + "---This is not a test").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(sourceDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        final BlogPost bp = new BlogPost(sourceDirectory, sourceDirectory.relativize(sourceDirectory.resolve(templateFileName)));
        bp.setLayout(new Layout(sourceDirectory, sourceDirectory.resolve(layoutFileName)));


        final Path buildDir = saito$$.buildDir();

        targetPathFinder.find(bp, model);

        final Path yearDir = buildDir.resolve("2015");
        assertThat(Files.exists(yearDir)).isFalse();
    }

}
