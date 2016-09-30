package com.marcobehler.saito.core.proxy;

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
 * Created by BEHLEMA on 30.09.2016.
 */
public class ProxyTests extends BaseInMemoryFSTest {

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
                "   { \"name\" : \"Tom\", \"age\": 15}, \n" +
                "   { \"name\" : \"Dick\", \"age\": 20}, \n" +
                "   { \"name\":  \"Harry\", \"age\": 22},\n" +
                "   { \"name\":  \"Dörpär ist Ne Muddi\", \"age\": 25}\n" +
                "  ]\n" +
                "}").getBytes());


        final Path sourceDirectory = saito.getSourcesDir();

        String templateFileName = "friends.html.ftl";
        Files.write(sourceDirectory.resolve(templateFileName), ("---\n" +
                "layout: layout\n" +
                "proxy:\n" +
                "  data: data.dummy.friends\n" +
                "  pattern: ${name}\n" +
                "  alias: friend\n" +
                "---\n" +
                "\n" +
                "\n" +
                "My friend ${friend.name} is of age ${friend.age} \n"
                ).getBytes());


        Path layoutDir = sourceDirectory.resolve("layouts");
        Files.createDirectories(layoutDir);

        Files.write(layoutDir.resolve("layout.ftl"), ("<p>[@saito.yield/]</p>").getBytes());
    }


    @Test
    public void proxying_creates_correct_files_with_indexing_off() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(false);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");

        assertThat(buildDir.resolve("friends/tom.html")).exists();
        assertThat(buildDir.resolve("friends/dick.html")).exists();
        assertThat(buildDir.resolve("friends/harry.html")).exists();

        assertThat(buildDir.resolve("friends/tom/index.html")).doesNotExist();
        assertThat(buildDir.resolve("friends/dick/index.html")).doesNotExist();
        assertThat(buildDir.resolve("friends/harry/index.html")).doesNotExist();

    }

    @Test
    public void proxying_slugifys_data() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(false);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");

        assertThat(buildDir.resolve("friends/doerpaer-ist-ne-muddi.html")).exists();
    }
    

    @Test
    public void proxying_creates_correct_content_with_indexing_off() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(false);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");

        assertThat(buildDir.resolve("friends/tom.html")).hasContent("<p>My friend Tom is of age 15</p>");
        assertThat(buildDir.resolve("friends/dick.html")).hasContent("<p>My friend Dick is of age 20</p>");
        assertThat(buildDir.resolve("friends/harry.html")).hasContent("<p>My friend Harry is of age 22</p>");
    }



    @Test
    public void proxying_creates_correct_files_with_indexing_on() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(true);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");

        assertThat(buildDir.resolve("friends/tom/index.html")).exists();
        assertThat(buildDir.resolve("friends/dick/index.html")).exists();
        assertThat(buildDir.resolve("friends/harry/index.html")).exists();

        assertThat(buildDir.resolve("friends/tom.html")).doesNotExist();
        assertThat(buildDir.resolve("friends/dick.html")).doesNotExist();
        assertThat(buildDir.resolve("friends/harry.html")).doesNotExist();
    }



    @Test
    public void proxying_creates_correct_content_with_indexing_on() throws IOException, BlogPost.BlogPostFormattingException {
        saito.getConfig().setDirectoryIndexes(true);
        saito.build();

        Path buildDir = saito.getWorkingDir().resolve("build");

        assertThat(buildDir.resolve("friends/tom/index.html")).hasContent("<p>My friend Tom is of age 15</p>");
        assertThat(buildDir.resolve("friends/dick/index.html")).hasContent("<p>My friend Dick is of age 20</p>");
        assertThat(buildDir.resolve("friends/harry/index.html")).hasContent("<p>My friend Harry is of age 22</p>");
    }

}

