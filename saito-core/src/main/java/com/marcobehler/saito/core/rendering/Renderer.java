package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.configuration.ModelSpace;
import com.marcobehler.saito.core.files.Template;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Renderer {

    boolean canRender(Template template);

    String render(Template template, final ModelSpace modelSpace);

}
