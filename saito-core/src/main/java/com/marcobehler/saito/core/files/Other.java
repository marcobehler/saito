package com.marcobehler.saito.core.files;

import com.marcobehler.saito.core.assets.YuiWrapper;
import com.marcobehler.saito.core.rendering.RenderingModel;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

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
     * @param renderingModel the SaitoConfig
     * @param targetDirectory the targetDirectory
     */
    public void process(RenderingModel renderingModel, Path targetDirectory) {
        try {
            Path sourceFile = getSourceDirectory().resolve(getRelativePath());

            Path targetFile = targetDirectory.resolve(getRelativePath());
            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectories(targetFile.getParent());
            }

            SaitoConfig saitoConfig = renderingModel.getSaitoConfig();

            if (saitoConfig.isCompressCss() && isCssAsset(targetFile)) {

                Path compressedFile = getCompressedPath(targetFile, "(?i)\\.css", getCompressedSuffix(renderingModel) + ".css");
                new YuiWrapper().compressCSS(sourceFile, compressedFile);

            } else if (saitoConfig.isCompressJs() && isJsAsset(targetFile)) {

                Path compressedFile = getCompressedPath(targetFile, "(?i)\\.js", getCompressedSuffix(renderingModel) + ".js");
                new YuiWrapper().compressJavaScript(sourceFile, compressedFile);

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

    // TODO remove duplicate in linkhelper
    private String getCompressedSuffix(RenderingModel renderingModel) {
        String datePart = new SimpleDateFormat("yyyyMMddHHmmss").format(
                renderingModel.getParameters().get(RenderingModel.BUILD_TIME_PARAMETER));
        return "-" + datePart + ".min";
    }
}

