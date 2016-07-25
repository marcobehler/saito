package com.marcobehler.saito.core.markdown;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.pegdown.PegDownProcessor;

import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.rendering.Renderer;
import com.marcobehler.saito.core.rendering.RenderingModel;

import lombok.extern.slf4j.Slf4j;

/**
 * A Markdown renderer on steroids
 *
 * See https://github.com/sirthias/pegdown
 */
@Singleton
@Slf4j
public class PegdownRenderer implements Renderer {

    @Override
    public boolean canRender(final Template template) {
        final String fileName = template.getRelativePath().toString().toLowerCase();
        // github markdown extensions https://github.com/github/markup/blob/b865add2e053f8cea3d7f4d9dcba001bdfd78994/lib/github/markups.rb#L1
        return fileName.endsWith(".markdown") || fileName.endsWith(".mdown") || fileName.endsWith(".mkdn") || fileName.endsWith(".mkd") || fileName.endsWith(".md");
    }

    public static void main(String[] args) {
        final String render = new PegdownRenderer().render(null, null);
        System.out.println(render);
    }

    @Override
    public String render(final Template template, final RenderingModel renderingModel) {
        final PegDownProcessor pegDownProcessor = new PegDownProcessor();
        final String s = pegDownProcessor.markdownToHtml("## das ist ein test");
        return s;
    }
}
