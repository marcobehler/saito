package com.marcobehler.saito.core.domain;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Getter
public class TemplateContent {

    private static final Pattern pattern = Pattern.compile("---(.*)---(.*)", Pattern.DOTALL);

    private final String text;

    public TemplateContent(String text) {
        this.text = text;
    }


    public static TemplateContent parseTemplate(final String content) {
        if (content == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String text = matcher.group(2).trim();

            return new TemplateContent(text);
        }
        return null;
    }
}
