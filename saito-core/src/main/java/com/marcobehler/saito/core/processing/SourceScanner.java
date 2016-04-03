package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.SaitoModel;
import com.marcobehler.saito.core.files.DataFile;
import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Other;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class SourceScanner {

    private static final Pattern layoutPattern = Pattern.compile("(?i)layouts[\\\\|/][^_].+\\.ftl");
    private static final Pattern templatePattern = Pattern.compile("(?i).+\\.html\\.ftl");
    private static final Pattern filePattern = Pattern.compile("(?i).+\\..+");
    private static final Pattern dataPattern = Pattern.compile("(?i).+\\.json");

    public SaitoModel scan(Path directory) {
        SaitoModel result = new SaitoModel();

        Path sourcesDir = directory.resolve("source");
        scanSourceDirectory(sourcesDir, result);

        Path dataDir = directory.resolve("data");
        scanDataDirectory(dataDir, result);

        return result;
    }


    private void scanSourceDirectory(Path directory, SaitoModel result) {
        Path absoluteDirectory = directory.toAbsolutePath().normalize();

        try {
            Files.walk(directory).parallel().forEach(p -> {
                Path relativePath = PathUtils.relativize(absoluteDirectory, p);
                if (layoutPattern.matcher(relativePath.toString()).matches()) {
                    result.getLayouts().add(new Layout(directory, relativePath));
                } else if (templatePattern.matcher(relativePath.toString()).matches()) {
                    result.getTemplates().add(new Template(directory, relativePath));
                } else if (filePattern.matcher(relativePath.toString()).matches()) {
                    result.getOthers().add(new Other(directory, relativePath));
                }
            });
        } catch (IOException e) {
            log.error("Problem walking {}", directory, e);
        }
    }

    private void scanDataDirectory(Path directory, SaitoModel result) {
        Path absoluteDirectory = directory.toAbsolutePath().normalize();

        try {
            Files.walk(directory).parallel().forEach(p -> {
                Path relativePath = PathUtils.relativize(absoluteDirectory, p);
                if (dataPattern.matcher(relativePath.toString()).matches()) {
                    result.getDataFiles().add(new DataFile(directory, relativePath));
                }
            });
        } catch (IOException e) {
            log.error("Problem walking {}", directory, e);
        }
    }


}
