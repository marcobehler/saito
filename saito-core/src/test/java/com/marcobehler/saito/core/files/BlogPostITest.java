package com.marcobehler.saito.core.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.DaggerTestSaito$$;
import com.marcobehler.saito.core.dagger.TestSaito$$;

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

        final BlogPost bp = new BlogPost(sourceDirectory, sourceDirectory.resolve(templateFileName));
        bp.setLayout(new Layout(sourceDirectory, sourceDirectory.resolve(layoutFileName)));

        final Path tmpDir = Files.createTempDirectory("tmpdir");
        bp.process(saito.getRenderingModel(), tmpDir, saito.getEngine());

        tmpDir.resolve("2015");



    }

}
