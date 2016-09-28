package com.marcobehler.saito.core.processing;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.marcobehler.saito.core.files.*;
import com.marcobehler.saito.core.util.PathUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class SourceScanner {

    private static final Pattern layoutPattern = Pattern.compile("(?i)layouts[\\\\|/][^_].+\\.ftl");
    private static final Pattern templatePattern = Pattern.compile("(?i).+\\.html\\.(ftl|asciidoc|adoc|asc|markdown|mdown|mkdn|mkd|md)");  // asciidoc und markdown TODO
    private static final Pattern filePattern = Pattern.compile("(?i).+\\..+");
    private static final Pattern dataPattern = Pattern.compile("(?i).+\\.json");

    public List<SaitoFile> scan(Path directory) {
        List<SaitoFile> sources = new ArrayList<>();

        Path sourcesDir = directory.resolve("source");
        sources.addAll(scanSourceDirectory(sourcesDir));

        Path dataDir = directory.resolve("data");
        sources.addAll(scanDataDirectory(dataDir));

        Path blogPostsDir = directory.resolve("posts");
        sources.addAll(scanBlogdirectory(blogPostsDir));

        associateLayoutsAndTemplates(sources);

        return sources;
    }

    private List<SaitoFile> scanSourceDirectory(Path directory) {
        List<SaitoFile> result = new ArrayList<>();

        Path absoluteDirectory = directory.toAbsolutePath().normalize();

        try {
            Files.walk(directory).parallel().forEach(p -> {
                Path relativePath = PathUtils.relativize(absoluteDirectory, p);
                if (layoutPattern.matcher(relativePath.toString()).matches()) {
                    result.add(new Layout(directory, relativePath));
                } else if (templatePattern.matcher(relativePath.toString()).matches()) {
                    result.add(new Template(directory, relativePath));
                } else if (filePattern.matcher(relativePath.toString()).matches()) {
                    result.add(new Other(directory, relativePath));
                }
            });
        } catch (IOException e) {
            log.error("Problem walking {}", directory, e);
        }
        return result;
    }

    private List<DataFile> scanDataDirectory(Path directory) {
        List<DataFile> result = new ArrayList<>();

        if (!Files.exists(directory)) {
            log.info("No 'data' directory found in project dir, skipping...");
            return result;
        }

        Path absoluteDirectory = directory.toAbsolutePath().normalize();

        try {
            Files.walk(directory).parallel().forEach(p -> {
                Path relativePath = PathUtils.relativize(absoluteDirectory, p);
                if (dataPattern.matcher(relativePath.toString()).matches()) {
                    result.add(new DataFile(directory, relativePath));
                }
            });
        } catch (IOException e) {
            log.error("Problem walking {}", directory, e);
        }
        return result;
    }

    private List<BlogPost> scanBlogdirectory(final Path directory) {
        List<BlogPost> result = new ArrayList<>();

        if (!Files.exists(directory)) {
            log.info("No 'blog' directory found in project dir, skipping...");
            return result;
        }

        Path absoluteDirectory = directory.toAbsolutePath().normalize();
        try {
            Files.walk(absoluteDirectory).parallel().forEach(p -> {
                Path relativePath = PathUtils.relativize(absoluteDirectory, p);
                if  (templatePattern.matcher(relativePath.toString()).matches()) {
                    try {
                        result.add(new BlogPost(directory, relativePath));
                    } catch (BlogPost.BlogPostFormattingException e) {
                        log.error("Problem parsing blog post {}", e);
                    }
                }
            });

        } catch (IOException e) {
            log.error("Problem walking {}", directory, e);
        }
        return result;
    }

    private void associateLayoutsAndTemplates(List<SaitoFile> sources) {
        Stream<Layout> layouts = sources.stream().filter(s -> s instanceof Layout).map(s -> (Layout) s);
        Stream<Template> templates = sources.stream().filter(s -> s instanceof Template).map( s -> (Template) s);

        ImmutableMap<String, Layout> layoutsByName = Maps.uniqueIndex(layouts.iterator(), Layout::getName);

        templates.forEach(template -> {
            String layoutName = template.getLayoutName();
            if (!layoutsByName.containsKey(template.getLayoutName())) {
                throw new IllegalStateException("There is no layout file for " + template.getLayoutName() + ".ftl for layout: " + template.getLayoutName());
            }
            Layout layout = layoutsByName.get(layoutName);
            template.setLayout(layout);
        });
    }
}
