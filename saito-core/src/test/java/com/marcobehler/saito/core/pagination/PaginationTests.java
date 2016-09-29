package com.marcobehler.saito.core.pagination;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.DaggerTestSaito$$;
import com.marcobehler.saito.core.dagger.TestSaito$$;
import com.marcobehler.saito.core.files.BlogPost;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

/**
 * Created by BEHLEMA on 29.09.2016.
 */
public class PaginationTests extends BaseInMemoryFSTest {


    private Saito saito;

    @Before
    public void setup() throws IOException {
        final TestSaito$$ saito$$ = DaggerTestSaito$$.builder().build();
        saito = saito$$.saito();

        Path workingDir = saito.getWorkingDir();
        Path dataDir = workingDir.resolve("data");
        Files.createDirectories(dataDir);

        Files.write(dataDir.resolve("dummy.json"), ("{\n" +
                "  \"friends\": [\n" +
                "    \"Tom\",\n" +
                "    \"Dick\",\n" +
                "    \"Harry\"\n" +
                "  ]\n" +
                "}").getBytes());


        final Path sourceDirectory = saito.getSourcesDir();

        String templateFileName = "friends.html.ftl";
        Files.write(sourceDirectory.resolve(templateFileName), ("---\n" +
                "layout: layout\n" +
                "pagination:\n" +
                "  per_page: 1\n" +
                "---\n" +
                "\n" +
                "\n" +
                "Torsten maul hat einen freund:\n" +
                "\n" +
                "\n" +
                "[@saito.paginate data.dummy.friends; f]<p>${f}</p>[/@saito.paginate]").getBytes());


        Path layoutDir = sourceDirectory.resolve("layouts");
        Files.createDirectories(layoutDir);

        Files.write(layoutDir.resolve("layout.ftl"), ("<p>[@saito.yield/]</p>").getBytes());
    }


    @Test
    public void pagination_creates_correct_dirs_with_indexing_on() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(true);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");
        assertThat(buildDir.resolve("friends/index.html")).exists();
        assertThat(buildDir.resolve("friends/pages/2/index.html")).exists();
        assertThat(buildDir.resolve("friends/pages/3/index.html")).exists();
        assertThat(buildDir.resolve("friends/pages/4/index.html")).doesNotExist();
    }

    @Test
    public void pagination_does_not_create_files_with_indexing_on() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(true);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");
        assertThat(buildDir.resolve("friends.html")).doesNotExist();
        assertThat(buildDir.resolve("friends-page2.html")).doesNotExist();
        assertThat(buildDir.resolve("friends-page3.html")).doesNotExist();
        assertThat(buildDir.resolve("friends-page4.html")).doesNotExist();
    }

    @Test
    public void pagination_creates_correct_files_with_indexing_off() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(false);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");
        assertThat(buildDir.resolve("friends.html")).exists();
        assertThat(buildDir.resolve("friends-page2.html")).exists();
        assertThat(buildDir.resolve("friends-page3.html")).exists();
        assertThat(buildDir.resolve("friends-page4.html")).doesNotExist();
    }

    @Test
    public void pagination_does_not_create_dirs_with_indexing_off() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(false);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");
        assertThat(buildDir.resolve("friends/index.html")).doesNotExist();
        assertThat(buildDir.resolve("friends/pages/2/index.html")).doesNotExist();
        assertThat(buildDir.resolve("friends/pages/3/index.html")).doesNotExist();
        assertThat(buildDir.resolve("friends/pages/4/index.html")).doesNotExist();
    }

    @Test
    public void pagination_creates_wrong_dirs_with_directoryIndexes_on() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(true);
        saito.build();

        boolean existsBuggyDir = Files.exists( saito.getWorkingDir().resolve("friends"));
        assertFalse(existsBuggyDir);
    }

}
