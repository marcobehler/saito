package com.marcobehler.saito.core.files;

import lombok.ToString;

import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@ToString
public class Layout extends SaitoFile {

    public Layout(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
    }

    public String getName() {
        final String filename = getRelativePath().getFileName().toString();
        return filename.substring(0, filename.toLowerCase().indexOf(".ftl"));
    }
}
