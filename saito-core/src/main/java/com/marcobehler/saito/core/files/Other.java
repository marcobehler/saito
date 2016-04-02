package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class Other extends SaitoFile{

    public Other(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
    }

    public void process(SaitoConfig config, Path targetDirectory) {
        try {
            Path sourceFile = getSourceDirectory().resolve(getRelativePath());
            Path targetFile = targetDirectory.resolve(getRelativePath());

            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectories(targetFile.getParent());
            }

            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("created {}", targetFile);
        } catch (IOException e) {
            log.error("Error copying file", e);
        }
    }
}

