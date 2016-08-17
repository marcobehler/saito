package com.marcobehler.saito.core.files;


import com.marcobehler.saito.core.pagination.PaginationException;
import com.marcobehler.saito.core.rendering.RenderingModel;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.domain.FrontMatter;
import com.marcobehler.saito.core.domain.TemplateContent;
import com.marcobehler.saito.core.rendering.RenderingEngine;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Template extends SaitoFile {

    public static final Pattern PAGING_PATTERN = Pattern.compile("\\[@saito\\.paginate\\s+(.+);.+\\]");

    static final String TEMPLATE_FILE_EXTENSION = ".ftl";

    @Getter
    private final FrontMatter frontmatter;

    @Getter
    private final TemplateContent content; // can be  HTML, asciidoc, md

    @Setter
    @Getter
    private Layout layout;

    public Template(Path sourceDirectory, Path relativePath) {
        super(sourceDirectory, relativePath);
        this.frontmatter = FrontMatter.of(getDataAsString());
        this.content = TemplateContent.of(getDataAsString());
    }

    public void process(RenderingModel renderingModel, Path targetDir, RenderingEngine engine) {
        if (layout == null) {
            throw new IllegalStateException("Layout must not be null");
        }

        if (!shouldProcess()) {
            return;
        }

        String outputPath = PathUtils.stripExtension(getOutputPath(), TEMPLATE_FILE_EXTENSION);

        Path targetFile = getTargetFile(renderingModel, targetDir, outputPath);

        ThreadLocal<Path> tl = (ThreadLocal<Path>) renderingModel.getParameters().get(RenderingModel.TEMPLATE_OUTPUT_PATH);
        tl.set(targetFile);

        try {
            engine.render(this, targetFile, renderingModel);
        } catch (PaginationException e) {
            log.info("Starting to paginate ", e);

            // TODO cleanup, needs more work

            int pages = e.getPages();
            System.out.println(pages);

            final String dataPath = getDataPath();
            System.out.println(dataPath);

            final List<List<Object>> partitions = e.getPartitions();
            System.out.println(partitions);

            for (int i = 1; i < pages; i++ ) {

                // Todo clone the whole data list
                // real partitioning, not via sublist
                // access the list via the last .
                RenderingModel clonedModel = renderingModel.clone();
                final String[] split = dataPath.split("\\.");
                System.out.println(split.length);
                final String newPath = Arrays.stream(split).map(s -> "(" + s + ")").collect(Collectors.joining(""));
                System.out.println(newPath);
                // TODO
                try {
                    final List<Object> dataList = (List<Object>) PropertyUtils.getProperty(clonedModel.getParameters(), newPath);
                    dataList.clear();
                    dataList.addAll(partitions.get(i -1 ));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                    e1.printStackTrace();
                }

                targetFile = isDirectoryIndexEnabled(clonedModel.getSaitoConfig(), outputPath)
                        ? getDirectoryIndexTargetFile(targetDir.resolve( i == 1 ? "" : "/pages/" + i + "/"), outputPath)
                        : getTargetFile(targetDir, targetFile.getParent().getFileName().toString() + ((i == 1) ? "" : "/pages/" + i + ".html"));
                engine.render(this, targetFile, clonedModel);
            }
        }
    }

    public static void main(String[] args) {
        final HashMap<String, Object> m = new HashMap<>();
        final HashMap<Object, Object> friends = new HashMap<>();
        friends.put("friends", Arrays.asList("hansi", "hinter", "huber"));

        final HashMap<Object, Object> dummy = new HashMap<>();
        dummy.put("dummy", friends);

        m.put("data", dummy);

        try {
            final Object property = PropertyUtils.getProperty(m, "(data)(dummy)(friends)");
            System.out.println(property);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }



    private String getDataPath() {
        final Matcher matcher = PAGING_PATTERN.matcher(content.getText());
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalStateException("Could not find data for pagination");
    }

    protected boolean shouldProcess() {
        return true;
    }

    protected Path getTargetFile(final RenderingModel renderingModel, final Path targetDir, final String outputPath) {
        return isDirectoryIndexEnabled(renderingModel.getSaitoConfig(), outputPath)
                    ? getDirectoryIndexTargetFile(targetDir, outputPath)
                    : getTargetFile(targetDir, outputPath);
    }


    @SneakyThrows
    private Path getDirectoryIndexTargetFile(Path targetDir, String relativePath) {
        final FileSystem fs = targetDir.getFileSystem();
        relativePath = PathUtils.stripExtension(fs.getPath(relativePath), ".html");

        Path dir = targetDir.resolve(relativePath);
        Path targetSubDir = Files.createDirectories(dir);

        return targetSubDir.resolve("index.html");
    }


    private Path getTargetFile(Path targetDir, String relativePath) {
        Path targetFile = targetDir.resolve(relativePath);
        if (!Files.exists(targetFile.getParent())) {
            try {
                Files.createDirectories(targetFile.getParent());
            } catch (IOException e) {
                log.error("Error creating directory", e);
            }
        }
        return targetFile;
    }


    private boolean isDirectoryIndexEnabled(SaitoConfig config, String relativePath) {
        return config.isDirectoryIndexes() && !relativePath.endsWith("index.html"); // if the file is already called index.html, skip it
    }



    public String getLayoutName() {
        Map<String, Object> frontMatter = getFrontmatter().getCurrentPage();
        return (String) frontMatter.getOrDefault("layout", "layout");
    }

}
