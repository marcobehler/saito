package com.marcobehler.saito.core.rendering;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.asciidoc.AsciidocRenderer;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.freemarker.FreemarkerRenderer;
import com.marcobehler.saito.core.freemarker.FreemarkerTemplateLoader;
import com.marcobehler.saito.core.markdown.PegdownRenderer;

import static com.marcobehler.saito.core.rendering.FreemarkerRendererTest.freemarkerConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class AsciidocRendererTest extends BaseInMemoryFSTest {

    @Test
    public void simple_render_works() throws IOException {
        String templateFileName = "index.adoc";
        Files.write(workingDirectory.resolve(templateFileName), ("---\n" + "layout: layout\n" + "---  Writing AsciiDoc is _easy_!").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(workingDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath(templateFileName));
        saitoTemplate.setLayout(new Layout(workingDirectory, fs.getPath("layout.ftl")));

        final FreemarkerRenderer freemarkerRenderer = new FreemarkerRenderer(
                new FreemarkerTemplateLoader(freemarkerConfig()));
        String rendered = new AsciidocRenderer(
                freemarkerRenderer).render(saitoTemplate, new RenderingModel(mock(SaitoConfig.class)));
        assertThat(rendered).isEqualTo("<p><div class=\"paragraph\">\n" + "<p>Writing AsciiDoc is <em>easy</em>!</p>\n"
                + "</div></p>");
    }

}
