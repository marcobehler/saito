package com.marcobehler.saito.core.rendering;

import com.marcobehler.saito.core.files.Template;

import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Renderer {

    public void render(Template template, Path targetFile);


}
