package com.marcobehler.saito.core.domain;

import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class FrontMatter extends HashMap<String, Object> {

    private static final Pattern pattern = Pattern.compile("---(.*)---(.*)", Pattern.DOTALL);

    public FrontMatter(Map<String, Object> map) {
        put("current_page", map);
    }

    public static FrontMatter of(String content) {
        if (content == null) {
            return new FrontMatter(Collections.emptyMap());
        }


        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            return new FrontMatter(Collections.emptyMap());
        }


        String yamlString = matcher.group(1).trim();
        Yaml yaml = new Yaml();
        Map<String, Object> matter = (Map<String, Object>) yaml.load(yamlString);
        if (matter == null) {
            return new FrontMatter(Collections.emptyMap());
        }

        return new FrontMatter(matter);
    }
}
