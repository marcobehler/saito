package com.marcobehler.saito.core.files;

import com.google.common.base.Charsets;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@ToString
@Getter
public class SaitoFile {

    private final String content;
    private final Path sourceDirectory;
    private final Path relativePath;

    public SaitoFile(Path sourceDirectory, Path relativePath) {
        this.sourceDirectory = sourceDirectory;
        this.relativePath = relativePath;
        this.content = readFile(sourceDirectory.resolve(relativePath));
    }

    private String readFile(Path path) {
        try {
            return new String(Files.readAllBytes(path), Charsets.UTF_8);
        } catch (IOException e) {
            log.error("Error parsing FrontMatter", e);
        }
        return null;
    }
}
