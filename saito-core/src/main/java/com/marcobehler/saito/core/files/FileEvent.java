package com.marcobehler.saito.core.files;

import lombok.Getter;

import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class FileEvent {

    @Getter
    private final Path file;

    public FileEvent(Path file) {
        this.file = file;
    }
}
