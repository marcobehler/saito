package com.marcobehler.saito.core.rendering;

import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.freemarker.FreemarkerModule;
import com.marcobehler.saito.core.freemarker.FreemarkerRenderer;
import com.marcobehler.saito.core.freemarker.FreemarkerTemplateLoader;
import com.marcobehler.saito.core.util.LinkHelper;

import dagger.Lazy;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class FreemarkerRendererTest extends BaseInMemoryFSTest {

    @Test
    public void canRender_files_with_ftl_extension() throws IOException {
        String fileName = "ble.fTl";
        Files.write(workingDirectory.resolve(fileName), "test".getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath(fileName));
        boolean canRender = new FreemarkerRenderer(null).canRender(saitoTemplate);
        assertThat(canRender).isTrue();
    }

    @Test
    public void cannot_render_file_with_different_extension() throws IOException {
        String randomExtension = RandomStringUtils.randomAlphanumeric(3);
        String fileName = "ble." + randomExtension;
        Files.write(workingDirectory.resolve(fileName), "test".getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath(fileName));
        boolean canRender = new FreemarkerRenderer(null).canRender(saitoTemplate);
        assertThat(canRender).isFalse();
    }



    @Test
    public void simple_render_works() throws IOException {
        String templateFileName = "index.ftl";
        Files.write(workingDirectory.resolve(templateFileName), ("---\n" + "layout: layout\n" + "---This is not a test").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(workingDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath("index.ftl"));
        saitoTemplate.setLayout(new Layout(workingDirectory, fs.getPath("layout.ftl")));

        String rendered = new FreemarkerRenderer(new FreemarkerTemplateLoader(freemarkerConfig())).render(saitoTemplate, new RenderingModel(mock(SaitoConfig.class)));
        assertThat(rendered).isEqualTo("<p>This is not a test</p>");
    }


    @Test
    public void frontMatter_variable_works() throws IOException {
        String templateFileName = "index.ftl";
        Files.write(workingDirectory.resolve(templateFileName), ("---\n" + "layout: layout\ntitle: Das ist mein title" + "---This is not a test, ${title}").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(workingDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath("index.ftl"));
        saitoTemplate.setLayout(new Layout(workingDirectory, fs.getPath("layout.ftl")));

        String rendered = new FreemarkerRenderer(new FreemarkerTemplateLoader(freemarkerConfig())).render(saitoTemplate, new RenderingModel(mock(SaitoConfig.class)));
        assertThat(rendered).isEqualTo("<p>This is not a test, Das ist mein title</p>");
    }


    @Test
    public void nested_frontMatter_variable_works() throws IOException {
        String templateFileName = "index.ftl";
        Files.write(workingDirectory.resolve(templateFileName), ("---\n" + "layout: layout\ntitle:\n  new: neuer title\n  old: alter title" + "---This is not a test, ${title.old}").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(workingDirectory.resolve(layoutFileName), ("<p>[@saito.yield/]</p>").getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath("index.ftl"));
        saitoTemplate.setLayout(new Layout(workingDirectory, fs.getPath("layout.ftl")));

        String rendered = new FreemarkerRenderer(new FreemarkerTemplateLoader(freemarkerConfig())).render(saitoTemplate, new RenderingModel(mock(SaitoConfig.class)));
        assertThat(rendered).isEqualTo("<p>This is not a test, alter title</p>");
    }

    private Lazy<Configuration> freemarkerConfig() {
        return () -> FreemarkerModule.configuration(mock(LinkHelper.class), new MultiTemplateLoader(new TemplateLoader[]{new ClassTemplateLoader(Saito.class.getClassLoader(), "/")}));
    }
}
