package com.marcobehler.saito.core.rendering;

import java.util.Map;

import com.marcobehler.saito.core.files.Template;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Renderer {

    String render(Template template, final Map<String, Object> renderContext);

    boolean canRender(Template template);
}
