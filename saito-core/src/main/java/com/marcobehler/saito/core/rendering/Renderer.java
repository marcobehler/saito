package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.files.Template;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Renderer {

    String render(Template template);

    boolean canRender(Template template);
}
