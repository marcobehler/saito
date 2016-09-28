package com.marcobehler.saito.core.files;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * Every file in the project/source dir, that is not a layout or template.
 *
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class Other extends SaitoFile {

    public Other(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
    }


    /**
     * Other files get copied as is, without any processing done to them.
     *
     * @param model the SaitoConfig
     * @param targetDirectory the targetDirectory
     */

    public boolean isJs() {
        return getRelativePath().getFileName().toString().toLowerCase().endsWith(".js");
    }

    public boolean isCss() {
        return getRelativePath().getFileName().toString().toLowerCase().endsWith(".css");
    }
}

