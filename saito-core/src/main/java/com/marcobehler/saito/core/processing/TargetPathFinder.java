package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.BlogPost;
import com.marcobehler.saito.core.files.Other;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.util.PathUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by marco on 17.09.2016.
 */
@Singleton
@Slf4j
public class TargetPathFinder {

    private SaitoConfig saitoConfig;

    @Inject
    public TargetPathFinder(SaitoConfig saitoConfig) {
        this.saitoConfig = saitoConfig;
    }

    public <T extends SaitoFile> Path find(T file) {
        Path targetFile = getOutputPath(file);
        if (isDirectoryIndexEnabled(file)) {
            targetFile = toDirectoryIndex(targetFile);
        }
        return targetFile;
    }

    public Path getOutputPath(SaitoFile file) {
        if (file instanceof BlogPost) {
            final Path relativePath = file.getRelativePath();
            final String asString = relativePath.toString();
            final FileSystem fs = relativePath.getFileSystem();
            final String blogPath = BlogPost.BLOG_POST_PATTERN.matcher(asString).replaceAll("$1/$2/$3/$4$5");
            return fs.getPath(blogPath);

        } else if (file instanceof Template) {
            final String pathWithoutExtension = PathUtils.stripExtension(file.getRelativePath(), ".ftl");
            return file.getRelativePath().getFileSystem().getPath(pathWithoutExtension);
        } else if (file instanceof Other) {
            return file.getRelativePath();
        }
        throw new IllegalStateException("Trying to get path for unsupported file type");
    }

    @SneakyThrows
    private Path toDirectoryIndex(Path targetFile) {
        // todo enable pagination again
      /*  if (pagination.isPresent() && pagination.get().getCurrentPage() > 1) {
            directoryIndexDir = directoryIndexDir.resolve("pages/" + pagination.get().getCurrentPage());
        }*/
        String directoryName = PathUtils.stripExtension(targetFile, ".html");
        Path directoryIndexPath = targetFile.getFileSystem().getPath(directoryName, "index.html");
        return directoryIndexPath;
    }

   /* public Path getTargetFile(Path buildDir, Model model) {
        Path relativePath = getTargetFile(model);
        Path absolutePath = buildDir.resolve(relativePath);
        if (!Files.exists(absolutePath.getParent())) {
            try {
                Files.createDirectories(absolutePath.getParent());
            } catch (IOException e) {
                log.error("Error creating directory", e);
            }
        }
        return absolutePath;
    }*/


    private boolean isDirectoryIndexEnabled(SaitoFile file) {
        if (file.getRelativePath().toString().contains("index.html")) {
            return false;
        }
        // todo enable pagination again
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
                /d/{pattern}/index.html
*/

       /* ThreadLocal<Path> tl = (ThreadLocal<Path>) model.getParameters().get(Model.TEMPLATE_OUTPUT_PATH);
        tl.set(targetFile);
        return null;*/


    // todo enable pagination again
    /*   if (pagination.isPresent() && pagination.get().getCurrentPage() > 1) {
        relativePath = relativePath.replaceAll("(.*)(\\.html.*)", "$1-page" + pagination.get().getCurrentPage() + "$2");
    }*/
}