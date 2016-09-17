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

    public Path getOutputPath() {throw new UnsupportedOperationException();}

    // todo enable pagination again
    /*   if (pagination.isPresent() && pagination.get().getCurrentPage() > 1) {
        relativePath = relativePath.replaceAll("(.*)(\\.html.*)", "$1-page" + pagination.get().getCurrentPage() + "$2");
    }*/

    public Path getTargetFile(final Model model) {
        Path targetFile = getOutputPath();
        if (isDirectoryIndexEnabled(model.getSaitoConfig())) {
            targetFile = toDirectoryIndex(targetFile);
        }
        return targetFile;
    }

    @SneakyThrows
    private Path toDirectoryIndex(Path targetFile){
        // todo enable pagination again
      /*  if (pagination.isPresent() && pagination.get().getCurrentPage() > 1) {
            directoryIndexDir = directoryIndexDir.resolve("pages/" + pagination.get().getCurrentPage());
        }*/
        String directoryName = PathUtils.stripExtension(targetFile, ".html");
        Path directoryIndexPath = targetFile.getFileSystem().getPath(directoryName, "index.html");
        return directoryIndexPath;
    }

    public Path getTargetFile(Path buildDir, Model model) {
        Path relativePath = getTargetFile(model);
        Path absolutePath = buildDir.resolve(relativePath);
        if (!Files.exists(absolutePath.getParent())) {
            try {
                Files.createDirectories(absolutePath.getParent());
            } catch (IOException e) {
                log.error("Error creating directory", e);
            }
        }
        return absolutePath;
    }


    private boolean isDirectoryIndexEnabled(SaitoConfig config) {
        if (getRelativePath().toString().contains("index.html")) {
            return false;
        }
        // todo enable pagination again
        return config.isDirectoryIndexes();
    }
}
