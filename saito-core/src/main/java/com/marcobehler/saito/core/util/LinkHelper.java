package com.marcobehler.saito.core.util;

import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class LinkHelper {

    public String styleSheet(List<String> styleSheets) {
        StringBuilder builder = new StringBuilder();
        styleSheets.forEach(s -> builder.append("<link rel=\"stylesheet\" href=\"/stylesheets/").append(s).append(".css\"/>\n"));
        return builder.toString();
    }

    public String javascript(List<String> javaScripts) {
        StringBuilder builder = new StringBuilder();
        javaScripts.forEach(s -> builder.append("<script src=\"/javascripts/").append(s).append(".js\" ></script>"));
        return builder.toString();
    }
}
