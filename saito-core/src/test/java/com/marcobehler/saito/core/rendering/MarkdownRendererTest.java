package com.marcobehler.saito.core.rendering;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.freemarker.FreemarkerRenderer;
import com.marcobehler.saito.core.freemarker.FreemarkerTemplateLoader;
import com.marcobehler.saito.core.markdown.MarkdownRenderer;

import static com.marcobehler.saito.core.rendering.FreemarkerRendererTest.freemarkerConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class MarkdownRendererTest extends BaseInMemoryFSTest {

    @Test
    public void simple_render_works() throws IOException {
        String templateFileName = "index.md";
        Files.write(workingDirectory.resolve(templateFileName), ("---\n" + "layout: layout\n" + "--- ##Dada Pegdown").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(workingDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath("index.md"));
        saitoTemplate.setLayout(new Layout(workingDirectory, fs.getPath("layout.ftl")));

        final FreemarkerRenderer freemarkerRenderer = new FreemarkerRenderer(
                new FreemarkerTemplateLoader(freemarkerConfig()));
        String rendered = new MarkdownRenderer(
                freemarkerRenderer).render(saitoTemplate, new Model());
        assertThat(rendered).isEqualTo("<p><h2>Dada Pegdown</h2></p>");
    }

}
