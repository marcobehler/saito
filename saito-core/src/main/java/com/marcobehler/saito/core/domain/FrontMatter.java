package com.marcobehler.saito.core.domain;

import lombok.EqualsAndHashCode;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@EqualsAndHashCode
public class FrontMatter extends HashMap<String, Object> {

    private static final Pattern pattern = Pattern.compile("---(.*)---(.*)", Pattern.DOTALL);

    public static FrontMatter parse(final String content) {
        if (content == null) {
            return null;
        }

        FrontMatter result = new FrontMatter();
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            String yamlString = matcher.group(1);
            Yaml yaml = new Yaml();
            Map<String, Object> matter = (Map<String, Object>) yaml.load(yamlString);
            if (matter != null) {
                result.putAll(matter);
            }
        }
        return result;
    }
}
