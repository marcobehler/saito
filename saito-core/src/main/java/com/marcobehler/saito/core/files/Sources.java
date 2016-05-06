package com.marcobehler.saito.core.files;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.marcobehler.saito.core.rendering.RenderingModel;
import com.marcobehler.saito.core.rendering.RenderingEngine;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@Getter
public class Sources {

    private List<DataFile> dataFiles = new ArrayList<>();

    private List<Layout> layouts = new ArrayList<>();

    private List<Template> templates = new ArrayList<>();

    private List<Other> others = new ArrayList<>();

    public void process(RenderingModel config, Path projectDirectory, RenderingEngine engine) {
        calculateDependencies();
        dataFiles.forEach(d -> d.process(config));
        templates.forEach(t -> t.process(config, projectDirectory, engine));
        others.forEach(o -> o.process(config, projectDirectory));
    }

    private void calculateDependencies() {
        ImmutableMap<String, Layout> layoutsByName = Maps.uniqueIndex(layouts, Layout::getName);

        for (Template template : templates) {
            String layoutName = template.getLayoutName();
            if (!layoutsByName.containsKey(template.getLayoutName())) {
                throw new IllegalStateException("There is no layout file for " + template.getLayoutName() + ".ftl for layout: " + template.getLayoutName());
            }
            Layout layout = layoutsByName.get(layoutName);
            template.setLayout(layout);
        }
    }
}

