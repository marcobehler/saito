package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.files.BlogPost;
import com.marcobehler.saito.core.files.Sources;
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
    private static final Pattern templatePattern = Pattern.compile("(?i).+\\.html\\.(ftl|asciidoc|adoc|asc|markdown|mdown|mkdn|mkd|md)");  // asciidoc und markdown TODO
    private static final Pattern filePattern = Pattern.compile("(?i).+\\..+");
    private static final Pattern dataPattern = Pattern.compile("(?i).+\\.json");

    public Sources scan(Path directory) {
        Sources result = new Sources();

        Path sourcesDir = directory.resolve("source");
        scanSourceDirectory(sourcesDir, result);

        Path dataDir = directory.resolve("data");
        scanDataDirectory(dataDir, result);

        Path blogPostsDir = directory.resolve("posts");
        scanBlogdirectory(blogPostsDir, result);

        return result;
    }

    private void scanSourceDirectory(Path directory, Sources result) {
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

    private void scanDataDirectory(Path directory, Sources result) {
        if (!Files.exists(directory)) {
            log.info("No 'data' directory found in project dir, skipping...");
            return;
        }

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

    private void scanBlogdirectory(final Path directory, Sources result) {
        if (!Files.exists(directory)) {
            log.info("No 'blog' directory found in project dir, skipping...");
            return;
        }

        Path absoluteDirectory = directory.toAbsolutePath().normalize();
        try {
            Files.walk(absoluteDirectory).parallel().forEach(p -> {
                Path relativePath = PathUtils.relativize(absoluteDirectory, p);
                if  (templatePattern.matcher(relativePath.toString()).matches()) {
                    try {
                        result.getBlogPosts().add(new BlogPost(directory, relativePath));
                    } catch (BlogPost.BlogPostFormattingException e) {
                        log.error("Problem parsing blog post {}", e);
                    }
                }
            });

        } catch (IOException e) {
            log.error("Problem walking {}", directory, e);
        }
    }
}
