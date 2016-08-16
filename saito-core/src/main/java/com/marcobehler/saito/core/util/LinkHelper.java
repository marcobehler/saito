package com.marcobehler.saito.core.util;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.rendering.RenderingModel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
@Slf4j
public class LinkHelper {

    private final RenderingModel renderingModel;
    private final Path workingDirectory;

    @Inject
    public LinkHelper(RenderingModel renderingModel, @Named(PathsModule.WORKING_DIR) Path workingDirectory) {
        this.renderingModel = renderingModel;
        this.workingDirectory = workingDirectory;
    }

    public String styleSheet(List<String> styleSheets) {
        StringBuilder builder = new StringBuilder();
        styleSheets.forEach(s -> builder.append("<link rel=\"stylesheet\" href=\"")
                .append(directory("stylesheets"))
                .append(s)
                .append(getCompressedCssSuffix())
                .append(".css")
                .append("\"/>\n"));
        return builder.toString();
    }


    public String javascript(List<String> javaScripts) {
        StringBuilder builder = new StringBuilder();
        javaScripts.forEach(s -> builder.append("<script src=\"")
                .append(directory("javascripts"))
                .append(s)
                .append(getCompressedJSSuffix())
                .append(".js")
                .append("\" ></script>"));
        return builder.toString();
    }


    public String favicon(String name) {
        String favIcon = "<link rel=\"icon\" type=\"[mime]\" href=\"[href]\"/>";

        String mimeType = getMimeType(name);
        String href = directory("images") + name;

        return favIcon.replace("[href]", href).replace("[mime]", mimeType);
    }

    public String imageTag(String image, Integer width, Integer height, String clazz, String data, String alt){
        StringBuilder builder = new StringBuilder();

        String href = directory("images") + image;

        builder.append("<img src=\"").append(href).append("\" ");
        if (width != null && width > 0) {
            builder.append("width=\"").append(width).append("\" ");
        }
        if (height != null && height > 0) {
            builder.append("height=\"").append(height).append("\" ");
        }
        if (clazz != null && !clazz.trim().isEmpty()) {
            builder.append("class=\"").append(clazz).append("\" ");
        }
        if (data != null && !data.trim().isEmpty()) {
            builder.append("data-title=\"").append(data).append("\" ");
        }
        if (alt != null && !alt.trim().isEmpty()) {
            builder.append("alt=\"").append(alt).append("\" ");
        }
        builder.append("/>");
        return builder.toString();
    }


    private String getCompressedJSSuffix() {
        return renderingModel.getSaitoConfig().isCompressJs() ? getCompressedSuffix(renderingModel) : "";
    }

    private String getCompressedCssSuffix() {
        return renderingModel.getSaitoConfig().isCompressCss() ? getCompressedSuffix(renderingModel) : "";
    }

    private String getMimeType(String filename) {
        try {
            final FileSystem fs = workingDirectory.getFileSystem();
            return  Files.probeContentType(fs.getPath(filename));
        } catch (IOException e) {
            log.warn("Problem detecting mimetype", e);
            return null;
        }
    }

    private String directory(String directoryName) {
        if (renderingModel.getSaitoConfig().isRelativeLinks()) {
            // TODO cast and error check
            ThreadLocal<Path> outputPathTL = (ThreadLocal<Path>) renderingModel.getParameters().get(RenderingModel.TEMPLATE_OUTPUT_PATH);
            Path outputPath = outputPathTL.get();

            // assets are either in /javascript/ or /stylesheets/
            // to not have two different methods, I am coming up with a fake directory "assets", which simulates one directory level
            Path buildDir = workingDirectory.resolve("build/" + directoryName + "/");
            return outputPath.getParent().relativize(buildDir).toString().replaceAll("\\\\", "/") + "/";
        } else {
            return "/" + directoryName + "/";
        }
    }

    // TODO remove duplicate in linkhelper
    private String getCompressedSuffix(RenderingModel renderingModel) {
        String datePart = new SimpleDateFormat("yyyyMMddHHmmss").format(
                renderingModel.getParameters().get(RenderingModel.BUILD_TIME_PARAMETER));
        return "-" + datePart + ".min";
    }

}
