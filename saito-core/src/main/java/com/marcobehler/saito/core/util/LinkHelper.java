package com.marcobehler.saito.core.util;

import com.marcobehler.saito.core.configuration.ModelSpace;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Singleton
public class LinkHelper {

    private final ModelSpace modelSpace;

    // TODO relative paths

    @Inject
    public LinkHelper(ModelSpace modelSpace) {
        this.modelSpace = modelSpace;
    }

    public String styleSheet(List<String> styleSheets) {
        StringBuilder builder = new StringBuilder();
        styleSheets.forEach(s -> builder.append("<link rel=\"stylesheet\" href=\"/stylesheets/").append(s).append(modelSpace.getSaitoConfig().isCompressCss() ? getCompressedSuffix(modelSpace) : "").append(".css").append("\"/>\n"));
        return builder.toString();
    }

    public String javascript(List<String> javaScripts) {
        StringBuilder builder = new StringBuilder();
        javaScripts.forEach(s -> builder.append("<script src=\"/javascripts/").append(s).append(modelSpace.getSaitoConfig().isCompressJs() ? getCompressedSuffix(modelSpace) : "").append(".js").append("\" ></script>"));
        return builder.toString();
    }

    // TODO remove duplicate in linkhelper
    private String getCompressedSuffix(ModelSpace modelSpace) {
        String datePart = new SimpleDateFormat("yyyyMMddHHmmss").format(modelSpace.getParameters().get(ModelSpace.BUILD_TIME_PARAMETER));
        return "-" + datePart + ".min";
    }

}
