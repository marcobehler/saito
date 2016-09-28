package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@ToString
@Getter
public class SaitoFile {

    static final String TEMPLATE_FILE_EXTENSION = ".ftl";


    protected Path sourceDirectory;
    protected Path relativePath;

    protected byte[] data;

    SaitoFile(Path sourceDirectory, Path relativePath) {
        this.sourceDirectory = sourceDirectory;
        this.relativePath = relativePath;
        this.data = readFile(sourceDirectory.resolve(relativePath));
    }

    SaitoFile() {
    }


    @SneakyThrows
    private byte[] readFile(Path path) {
        return Files.readAllBytes(path);
    }

    public String getDataAsString() {
        return new String(data, Charset.forName("UTF-8"));
    }


}
