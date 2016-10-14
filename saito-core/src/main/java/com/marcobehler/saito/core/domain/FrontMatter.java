package com.marcobehler.saito.core.domain;

import com.marcobehler.saito.core.rendering.Model;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class FrontMatter extends HashMap<String, Map<String, Object>> {

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

    public Map<String, Object> getCurrentPage() {
        return get("current_page");
    }

    public Map<? extends String, ?> replace(Model model) {
        new HashSet<>(getCurrentPage().keySet()).forEach(key -> {
            if ( getCurrentPage().get(key) != null && getCurrentPage().get(key) instanceof String && getCurrentPage().get(key).toString().contains("${")) {
                String replacedTitle = replacePattern(getCurrentPage().get(key).toString(), model);
                getCurrentPage().put(key, replacedTitle);
            }
        });
        return this;

    }



    private String replacePattern(String variableString, Object data) {
        String result;

        // 1. process proxy
        StringWriter writer = new StringWriter();
        try {
            freemarker.template.Template t = new freemarker.template.Template(variableString, variableString, new Configuration(Configuration.VERSION_2_3_25));
            t.process(data, writer);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

}
