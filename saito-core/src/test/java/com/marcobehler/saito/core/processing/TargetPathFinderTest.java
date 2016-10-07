package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.Template;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by BEHLEMA on 07.10.2016.
 */
public class TargetPathFinderTest extends BaseInMemoryFSTest {

    private SaitoConfig config;

    private Path buildDir;
    private Path sourcesDir;

    @Before
    public void setup() throws IOException {
        this.config = new SaitoConfig(null);

        this.buildDir = Files.createDirectories(fs.getPath("/build"));
        this.sourcesDir = Files.createDirectories(fs.getPath("/sources"));

    }

    @Test
    public void indexFile_without_directory_indeces() throws IOException {
        config.setDirectoryIndexes(false);
        Files.createFile(fs.getPath("/sources").resolve("index.html.ftl"));

        TargetPathFinder targetPathFinder = new TargetPathFinder(config, buildDir);

        Path path = targetPathFinder.find(new Template(sourcesDir, fs.getPath("index.html.ftl")), Optional.empty(), Optional.empty());
        assertThat(path).isEqualTo(fs.getPath("/build/index.html"));
    }


    @Test
    public void indexFile_with_directory_indeces() throws IOException {
        config.setDirectoryIndexes(true);
        Files.createFile(fs.getPath("/sources").resolve("index.html.ftl"));

        TargetPathFinder targetPathFinder = new TargetPathFinder(config, buildDir);

        Path path = targetPathFinder.find(new Template(sourcesDir, fs.getPath("index.html.ftl")), Optional.empty(), Optional.empty());
        assertThat(path).isEqualTo(fs.getPath("/build/index.html"));
    }
}
