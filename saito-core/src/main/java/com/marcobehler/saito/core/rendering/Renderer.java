package com.marcobehler.saito.core.rendering;

import java.util.List;

import com.marcobehler.saito.core.files.Template;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Renderer {

    default boolean canRender(Template template) {
        final String fileName = template.getRelativePath().toString().toLowerCase();

        for (String extension : getSupportedExtensions()) {
            if (fileName.endsWith("."+ extension) ) {
                return true;
            }
        }
        return false;
    }

    public List<String> getSupportedExtensions();

    String render(Template template, final Model model);

}
