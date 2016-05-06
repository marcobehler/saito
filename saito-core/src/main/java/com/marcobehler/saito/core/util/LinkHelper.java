package com.marcobehler.saito.core.util;

import com.marcobehler.saito.core.rendering.RenderingModel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
public class LinkHelper {

    private final RenderingModel renderingModel;

    @Inject
    public LinkHelper(RenderingModel renderingModel) {
        this.renderingModel = renderingModel;
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


    private String getCompressedJSSuffix() {
        return renderingModel.getSaitoConfig().isCompressJs() ? getCompressedSuffix(renderingModel) : "";
    }

    private String getCompressedCssSuffix() {
        return renderingModel.getSaitoConfig().isCompressCss() ? getCompressedSuffix(renderingModel) : "";
    }


    private String directory(String directoryName) {
        if (renderingModel.getSaitoConfig().isRelativeLinks()) {
            // TODO cast and error check
            ThreadLocal<Path> outputPathTL = (ThreadLocal<Path>) renderingModel.getParameters().get(RenderingModel.TEMPLATE_OUTPUT_PATH);
            Path outputPath = outputPathTL.get();

            // assets are either in /javascript/ or /stylesheets/
            // to not have two different methods, I am coming up with a fake directory "assets", which simulates one directory level
            Path workDirectory = renderingModel.getWorkDirectory();
            Path buildDir = workDirectory.resolve("build/" + directoryName + "/");
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
