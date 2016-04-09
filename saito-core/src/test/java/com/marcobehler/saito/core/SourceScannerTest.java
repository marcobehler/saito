package com.marcobehler.saito.core;

import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.processing.SourceScanner;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SourceScannerTest extends BaseInMemoryFSTest {

    @Test
    public void scanner_finds_valid_layouts() throws IOException {
        Path workingDirectory = fs.getPath("/");

        Path layoutsFolder = Files.createDirectories(workingDirectory.resolve("source/layouts"));
        Files.createFile(layoutsFolder.resolve("layout.ftl"));
        Files.createFile(layoutsFolder.resolve("mySecond.fTl"));
        Files.createFile(layoutsFolder.resolve("ble_fourth.ftl"));

        SaitoModel model = new SourceScanner().scan(workingDirectory);

        List<Layout> layouts = model.getLayouts();
        assertThat(layouts.size()).isEqualTo(3);
    }

    @Test
    public void scanner_finds_valid_template() throws IOException {
        Path workingDirectory = fs.getPath("/");

        Path sourceFolder = Files.createDirectories(workingDirectory.resolve("source/someDir")).getParent();
        Files.createFile(sourceFolder.resolve("index.html.ftl"));
        Files.createFile(sourceFolder.resolve("index.hTmL.Ftl"));
        Files.createFile(sourceFolder.resolve("someDir/index.html.ftl"));

        SaitoModel model = new SourceScanner().scan(workingDirectory);

        List<Template> templates = model.getTemplates();
        assertThat(templates.size()).isEqualTo(3);
    }


    @Test
    public void scanner_ignores_underscore_file() throws IOException {
        Path workingDirectory = fs.getPath("/");

        Path layoutsFolder = Files.createDirectories(workingDirectory.resolve("source/layouts"));
        Files.createFile(layoutsFolder.resolve("_myThird.ftl"));

        SaitoModel model = new SourceScanner().scan(workingDirectory);

        List<Layout> layouts = model.getLayouts();
        assertThat(layouts.size()).isEqualTo(0);
    }
}
