package com.marcobehler.saito.core.bugs;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.DaggerTestSaito$$;
import com.marcobehler.saito.core.dagger.TestSaito$$;
import com.marcobehler.saito.core.files.BlogPost;
import com.marcobehler.saito.core.files.Layout;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertFalse;

/**
 * Created by BEHLEMA on 23.08.2016.
 */
public class BugsTest {

    @Test
    @Ignore // comment in again once pagination is working
    public void pagination_wrong_dirs_are_created() throws IOException, BlogPost.BlogPostFormattingException {
        final TestSaito$$ saito$$ = DaggerTestSaito$$.builder().build();
        Saito saito = saito$$.saito();

        saito.getModel().getSaitoConfig().setDirectoryIndexes(true);

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
        saito.build();

        boolean existsBuggyDir = Files.exists(workingDir.resolve("friends"));
        assertFalse(existsBuggyDir);
    }
}
