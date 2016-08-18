package com.marcobehler.saito.core.files;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@ToString
@Getter
public class SaitoFile {

    protected Path sourceDirectory;
    protected Path relativePath;

    protected byte[] data;

    SaitoFile(Path sourceDirectory, Path relativePath) {
        this.sourceDirectory = sourceDirectory;
        this.relativePath = relativePath;
        this.data = readFile(sourceDirectory.resolve(relativePath));
    }

    SaitoFile() {}


    public Path getOutputPath() {
        return relativePath;
    }

    @SneakyThrows
    private byte[] readFile(Path path) {
        return Files.readAllBytes(path);
    }

    public String getDataAsString() {
        return new String(data, Charset.forName("UTF-8"));
    }
}
