package com.marcobehler.saito.core.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.dagger.PathsModule;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
public class LinkHelper {

    private final SaitoConfig config;
    private final Path buildDir;

    @Inject
    public LinkHelper(final SaitoConfig config, @Named(PathsModule.BUILD_DIR) Path buildDir) {
        this.config = config;
        this.buildDir = buildDir;
    }

    public String styleSheet(List<String> styleSheets, Path targetFile) {
        StringBuilder builder = new StringBuilder();
        styleSheets.forEach(s -> {

            String href = null;

            if (config.isRelativeLinks()) {
                final Path cssFile = buildDir.resolve("stylesheets/" + s + ".css");
                final Path relativize = targetFile.relativize(cssFile);
                href = relativize.toString();
            } else {
                href = "/stylesheets/" + s + ".css";
            }

            builder.append("<link rel=\"stylesheet\" href=\"").append(href).append("\"/>\n");
        });
        return builder.toString();
    }

    public String javascript(List<String> javaScripts, Path targetFile) {
        StringBuilder builder = new StringBuilder();

        javaScripts.forEach(s -> {

            String src = null;
            if (config.isRelativeLinks()) {
                final Path javascriptFile = buildDir.resolve("javascripts/" + s + ".css");
                final Path relativize = targetFile.relativize(javascriptFile);
                src = relativize.toString();
            } else {
                src = "/javascripts/" + s + ".js";
            }
            builder.append("<script src=\"").append(src).append("\" ></script>");
        });
        return builder.toString();
    }
}
