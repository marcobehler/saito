package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.BlogPost;
import com.marcobehler.saito.core.files.Other;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.pagination.Page;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static com.marcobehler.saito.core.dagger.PathsModule.BUILD_DIR;

/**
 * Created by marco on 17.09.2016.
 */
@Singleton
@Slf4j
public class TargetPathFinder {

    @Getter
    private SaitoConfig saitoConfig;

    private final Path buildDir;

    @Inject
    public TargetPathFinder(SaitoConfig saitoConfig, @Named(BUILD_DIR) Path buildDir) {
        this.saitoConfig = saitoConfig;
        this.buildDir = buildDir;
    }

    // ============= PUBLIC API ====================

    public <T extends SaitoFile> Path find(T file, Model model) {
        Path targetFile = getOutputPath(file, model);
        if (isDirectoryIndexEnabled(targetFile.toString()) && targetFile instanceof Template) {
            targetFile = toDirectoryIndex(targetFile);
        }
        return toAbsolutePath(file, targetFile);
    }

    //  ## normal
    // /dir/file.html
    // /dir/file/index.html

    // ## pagination
    // /dir/file.html
    // /dir/file/pages/2.html
    // /dir/file/pages/3.html

    // /dir/file/index.html
    // /dir/file/pages/2/index.html
    // /dir/file/pages/3/index.html

    // ## proxy
    // /dir/${pattern}.html
    // /dir/${pattern}/index.html


    // ## proxy & pagination
    // /dir/file/index.html
    // /dir/file/pages/2/index.html
    // /dir/file/pages/3/index.html

    // /dir/${pattern}/index.html
    // /dir/${pattern}/pages/2/index.html
    // /dir/${pattern}/pages/3/index.html


    public Path find(Template template, Optional<Page> page, Optional<String> templatePattern) {
        StringBuilder builder = new StringBuilder();

        String parent = template.getRelativePath().getParent() != null ? template.getRelativePath().getParent().toString() : null;
        if (parent != null) {
            builder.append(parent);

            if (!parent.endsWith("/")) {
                builder.append("/");
            }
        }

        builder.append(template.getFileNameWithoutExtension());

        if (templatePattern.isPresent() && !templatePattern.get().equals(template.getFileNameWithoutExtension())) {
            builder.append("/");
            builder.append(templatePattern.get());
        }

        if (page.isPresent() && page.get().getPageNumber() > 1) {
            builder.append("/");
            builder.append("pages");
            builder.append("/");
            builder.append(page.get().getPageNumber());
        }

        if (isDirectoryIndexEnabled(builder.toString()) && !template.getFileNameWithoutExtension().equals("index")) {
            builder.append("/");
            builder.append("index");
        }

        builder.append(template.getSecondLastExtension());

        Path targetFile = template.getRelativePath().getFileSystem().getPath(builder.toString());
        return toAbsolutePath(template, targetFile);
    }


    // ============= PRIVATE API ====================

    private <T extends SaitoFile> boolean shouldCreateDirectories(T file) {
        if (file instanceof Template && !((Template) file).shouldProcess()) {
            return false;
        }
        return true;
    }

    private <T extends SaitoFile> void createDirectoriesIfNecessary(Path absolutePath) {
        if (!Files.exists(absolutePath.getParent())) {
            try {
                Files.createDirectories(absolutePath.getParent());
            } catch (IOException e) {
                log.error("Error creating directory", e);
            }
        }
    }


    public Path getOutputPath(SaitoFile file, Model model) {
        if (file instanceof BlogPost) {
            final Path relativePath = file.getRelativePath();
            final String asString = relativePath.toString();
            final FileSystem fs = relativePath.getFileSystem();
            final String blogPath = BlogPost.BLOG_POST_PATTERN.matcher(asString).replaceAll("$1/$2/$3/$4$5");
            return fs.getPath(blogPath);
        }

        if (file instanceof Template) {
            return find((Template) file, Optional.empty(), Optional.empty());
        }

        if (file instanceof Other) {
            if (((Other) file).isCss()) {
                String path = file.getRelativePath().toString();
                path = path.replaceAll("\\.css", getCompressedCssSuffix(model) + ".css");
                return file.getRelativePath().getFileSystem().getPath(path);
            } else if (((Other) file).isJs()) {
                String path = file.getRelativePath().toString();
                path = path.replaceAll("\\.js", getCompressedJSSuffix(model) + ".js");
                return file.getRelativePath().getFileSystem().getPath(path);
            } else {
                return file.getRelativePath();
            }
        }
        throw new IllegalStateException("Trying to get path for unsupported file type");
    }


    private String getCompressedSuffix(Model model) {
        String datePart = new SimpleDateFormat("yyyyMMddHHmmss").format(
                model.get(Model.BUILD_TIME_PARAMETER));
        return "-" + datePart + ".min";
    }

    private String getCompressedJSSuffix(Model model) {
        return saitoConfig.isCompressJs() ? getCompressedSuffix(model) : "";
    }

    private String getCompressedCssSuffix(Model model) {
        return saitoConfig.isCompressCss() ? getCompressedSuffix(model) : "";
    }

    @SneakyThrows
    private Path toDirectoryIndex(Path targetFile) {
        String directoryName = PathUtils.stripExtension(targetFile, ".html");
        return targetFile.getFileSystem().getPath(directoryName, "index.html");
    }


    private <T extends SaitoFile> Path toAbsolutePath(T saitoFile, Path targetFile) {
        Path absolutePath = buildDir.resolve(targetFile);
        if (shouldCreateDirectories(saitoFile)) {
            createDirectoriesIfNecessary(absolutePath);
        }
        return absolutePath;
    }

    private boolean isDirectoryIndexEnabled(String path) {
        if (path.startsWith("index.") || path.contains("/index")) {
            return false;
        }
        return saitoConfig.isDirectoryIndexes();
    }
}
