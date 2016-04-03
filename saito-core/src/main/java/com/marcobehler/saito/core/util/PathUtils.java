package com.marcobehler.saito.core.util;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class PathUtils {

    public static String stripExtension(Path path, String extension) {
        String fileName = path.toString();
        return fileName.substring(0, fileName.toLowerCase().indexOf(extension));
    }

    public static List<String> splitByFileSeparator(String path) {
        return Arrays.asList(path.split("[\\\\|/]"));
    }

    public static Path relativize(Path absoluteDirectory, Path subDirectory) {
        return absoluteDirectory.relativize(subDirectory.toAbsolutePath().normalize());
    }
}
