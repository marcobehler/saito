package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.BlogPost;
import com.marcobehler.saito.core.files.Other;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.pagination.Page;
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

    public <T extends SaitoFile> Path find(T file) {
        Path targetFile = getOutputPath(file);
        if (isDirectoryIndexEnabled(file)) {
            targetFile = toDirectoryIndex(targetFile);
        }
        return toAbsolutePath(file, targetFile);
    }


    public Path find(Template template, Page page) {
        if (page == null) {
            throw new NullPointerException("Page is missing for pagination");
        }

        if (page.getPageNumber() == 1) {
            return find(template);
        }

        Path targetFile;

        if (isDirectoryIndexEnabled(template)) {
            Path directoryIndexDir = toDirectoryIndex(getOutputPath(template)); // BE BUGGUNG
            targetFile = directoryIndexDir.resolve("pages/" + page.getPageNumber());
        }
        else { // plain file naming
            Path relativePath = template.getRelativePath();
            Path parent = relativePath.getParent();
            String fileName = relativePath.getFileName().toString().replaceAll("(.*)(\\.html)\\.ftl", "$1-page" + page.getPageNumber() + "$2");
            targetFile = parent == null ? relativePath.getFileSystem().getPath(fileName) : parent.resolve(fileName);
        }

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


    public Path getOutputPath(SaitoFile file) {
        if (file instanceof BlogPost) {
            final Path relativePath = file.getRelativePath();
            final String asString = relativePath.toString();
            final FileSystem fs = relativePath.getFileSystem();
            final String blogPath = BlogPost.BLOG_POST_PATTERN.matcher(asString).replaceAll("$1/$2/$3/$4$5");
            return fs.getPath(blogPath);
        }

        if (file instanceof Template) {
            String dotHtml = PathUtils.stripExtension(file.getRelativePath(), ".ftl");
            return file.getRelativePath().getFileSystem().getPath(dotHtml);
        }

        if (file instanceof Other) {
            return file.getRelativePath();
        }
        throw new IllegalStateException("Trying to get path for unsupported file type");
    }

    @SneakyThrows
    private Path toDirectoryIndex(Path targetFile) {
        String directoryName = PathUtils.stripExtension(targetFile, ".html");
        return targetFile.getFileSystem().getPath(directoryName, "index.html");
    }


    private <T extends SaitoFile> Path toAbsolutePath(T saitoFile, Path targetFile) {
        Path absolutePath = buildDir.resolve(targetFile);
        if (shouldCreateDirectories(saitoFile)){
            createDirectoriesIfNecessary(absolutePath);
        }
        return absolutePath;
    }

    private boolean isDirectoryIndexEnabled(SaitoFile file) {
        if (file.getRelativePath().toString().contains("index.html")) {
            return false;
        }
        return saitoConfig.isDirectoryIndexes();
    }


      /*  getdirectory
        isindex  ----. currentdir vs cyrentdur/name
        isproxy name = pattern...
        ispaginate = currentdir ] page
                getfilename
    /d.html
                /d.html
                /d/{pattern}.html
                /d.html -> indexing on
        /d/index.html
                /d/{pattern}/index.html*/
       /* ThreadLocal<Path> tl = (ThreadLocal<Path>) model.getParameters().get(Model.TEMPLATE_OUTPUT_PATH);
        tl.set(targetFile);
        return null;*/


    // todo enable pagination again
    /*   if (pagination.isPresent() && pagination.get().getCurrentPage() > 1) {

    }*/
}
