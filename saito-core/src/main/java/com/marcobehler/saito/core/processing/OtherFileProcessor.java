package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.assets.YuiWrapper;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.Other;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by marco on 17.09.2016.
 */
@Slf4j
public class OtherFileProcessor implements Processor<Other> {

    private final SaitoConfig saitoConfig;

    private final TargetPathFinder targetPathFinder;

    @Inject
    public OtherFileProcessor(SaitoConfig saitoConfig, TargetPathFinder targetPathFinder) {
        this.saitoConfig = saitoConfig;
        this.targetPathFinder = targetPathFinder;
    }

    @Override
    public void process(Other otherFile) {
        try {
            Path targetPath = targetPathFinder.find(otherFile);
            byte[] targetData = compressIfWanted(otherFile);
            Files.write(targetPath, targetData);
            log.info("created {}", targetPath);
        } catch (IOException e) {
            log.error("Error processing file", e);
        }
    }

    private byte[] compressIfWanted(Other otherFile) {
        if (otherFile.isCss() && saitoConfig.isCompressCss()) {
            return new YuiWrapper().compressCSS(otherFile.getData());
        }

        if (otherFile.isJs() && saitoConfig.isCompressJs()) {
            return new YuiWrapper().compressJavaScript(otherFile.getData());
        }

        return  otherFile.getData();
    }
}

