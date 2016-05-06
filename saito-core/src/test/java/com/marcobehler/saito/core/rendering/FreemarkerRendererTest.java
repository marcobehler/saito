package com.marcobehler.saito.core.rendering;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import com.marcobehler.saito.core.pagination.PaginationException;
import lombok.Getter;
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
import static org.junit.Assert.fail;
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



    @Test
    public void pagination_none_needed() throws IOException {
        String templateFileName = "index.ftl";
        Files.write(workingDirectory.resolve(templateFileName), ("---\n" + "layout: layout\npagination:\n  per_page: 2---\n[@saito.paginate users; u]<p>${u.name}</p>[/@saito.paginate]").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(workingDirectory.resolve(layoutFileName), ("[@saito.yield/]").getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath("index.ftl"));
        saitoTemplate.setLayout(new Layout(workingDirectory, fs.getPath("layout.ftl")));

        RenderingModel renderingModel = new RenderingModel(mock(SaitoConfig.class));
        renderingModel.getParameters().put("users", Arrays.asList(new User("Hans"), new User("Franz")));
        String rendered = new FreemarkerRenderer(new FreemarkerTemplateLoader(freemarkerConfig())).render(saitoTemplate, renderingModel);
        assertThat(rendered).isEqualTo("<p>Hans</p><p>Franz</p>");
    }

    @Test(expected = PaginationException.class)
    public void pagination_throws_exception_when_needed() throws IOException {
        String templateFileName = "index.ftl";
        Files.write(workingDirectory.resolve(templateFileName), ("---\n" + "layout: layout\npagination:\n  per_page: 1---\n[@saito.paginate users; u]<p>${u.name}</p>[/@saito.paginate]").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(workingDirectory.resolve(layoutFileName), ("[@saito.yield/]").getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath("index.ftl"));
        saitoTemplate.setLayout(new Layout(workingDirectory, fs.getPath("layout.ftl")));

        RenderingModel renderingModel = new RenderingModel(mock(SaitoConfig.class));
        renderingModel.getParameters().put("users", Arrays.asList(new User("Hans"), new User("Franz")));
        new FreemarkerRenderer(new FreemarkerTemplateLoader(freemarkerConfig())).render(saitoTemplate, renderingModel);
    }


    @Test
    public void pagination_executed_twice_throws_no_exception() throws IOException {
        String templateFileName = "index.ftl";
        Files.write(workingDirectory.resolve(templateFileName), ("---\n" + "layout: layout\npagination:\n  per_page: 1---\n[@saito.paginate users; u]<p>${u.name}</p>[/@saito.paginate]").getBytes());

        String layoutFileName = "layout.ftl";
        Files.write(workingDirectory.resolve(layoutFileName), ("[@saito.yield/]").getBytes());

        Template saitoTemplate = new Template(workingDirectory, fs.getPath("index.ftl"));
        saitoTemplate.setLayout(new Layout(workingDirectory, fs.getPath("layout.ftl")));

        RenderingModel renderingModel = new RenderingModel(mock(SaitoConfig.class));
        renderingModel.getParameters().put("users", Arrays.asList(new User("Hans"), new User("Franz")));
        try {
            new FreemarkerRenderer(new FreemarkerTemplateLoader(freemarkerConfig())).render(saitoTemplate, renderingModel);
            fail();
        } catch (Exception e) {
           // as expected
        }
        new FreemarkerRenderer(new FreemarkerTemplateLoader(freemarkerConfig())).render(saitoTemplate, renderingModel);
    }



    private Lazy<Configuration> freemarkerConfig() {
        return () -> FreemarkerModule.configuration(mock(LinkHelper.class), new MultiTemplateLoader(new TemplateLoader[]{new ClassTemplateLoader(Saito.class.getClassLoader(), "/")}));
    }

    @Getter
    public static class User {
        private final String name;


        public User(String name) {
            this.name = name;
        }
    }
}
