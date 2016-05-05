package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.compression.YuiPlugin;
import com.marcobehler.saito.core.configuration.ModelSpace;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

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
     * @param config the SaitoConfig
     * @param targetDirectory the targetDirectory
     */
    public void process(ModelSpace config, Path targetDirectory) {
        try {
            Path sourceFile = getSourceDirectory().resolve(getRelativePath());

            Path targetFile = targetDirectory.resolve(getRelativePath());
            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectories(targetFile.getParent());
            }

            SaitoConfig saitoConfig = config.getSaitoConfig();

            if (saitoConfig.isCompressCss() && isCssAsset(targetFile)) {
                Path compressedFile = getCompressedPath(targetFile, "(?i)\\.css", getCompressedSuffix() + ".css");
                new YuiPlugin().compressCSS(sourceFile, compressedFile);
            } else if (saitoConfig.isCompressJs() && isJsAsset(targetFile)) {
                Path compressedFile = getCompressedPath(targetFile, "(?i)\\.js", getCompressedSuffix() + ".js");
                new YuiPlugin().compressJavaScript(sourceFile, compressedFile);
            } else {
                Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("created {}", targetFile);
        } catch (IOException e) {
            log.error("Error processing file", e);
        }
    }

    private Path getCompressedPath(Path targetFile, String regex, String replacement) {
        String fileName = targetFile.getFileName().toString();
        String compressedFileName = fileName.replaceAll(regex, replacement);
        return targetFile.getParent().resolve(compressedFileName);
    }

    private boolean isJsAsset(Path targetFile) {
        return targetFile.getFileName().toString().toLowerCase().endsWith(".js");
    }

    private boolean isCssAsset(Path targetFile) {
        return targetFile.getFileName().toString().toLowerCase().endsWith(".css");
    }

    private String getCompressedSuffix() {
        String datePart = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "-" + datePart + ".min";
    }
}

