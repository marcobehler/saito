package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.util.PathUtils;
import lombok.ToString;

import java.nio.file.Path;

/**
 * Layouts are the base pages for your .html files. Every template can have a specific layout.
 *
 * @author Marco Behler <marco@marcobehler.com>
 */
@ToString
public class Layout extends SaitoFile {

    private static final String LAYOUT_FILE_EXTENSION = ".ftl";

    public Layout(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
    }

    public String getName() {
        return PathUtils.stripExtension(getRelativePath(), LAYOUT_FILE_EXTENSION);
    }
}
