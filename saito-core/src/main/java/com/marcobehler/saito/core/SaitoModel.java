package com.marcobehler.saito.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.DataFile;
import com.marcobehler.saito.core.files.Layout;
import com.marcobehler.saito.core.files.Other;
import com.marcobehler.saito.core.files.Template;
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
public class SaitoModel {

    private List<DataFile> dataFiles = new ArrayList<>();

    private List<Layout> layouts = new ArrayList<>();

    private List<Template> templates = new ArrayList<>();

    private List<Other> others = new ArrayList<>();

    public void process(SaitoConfig config, Path projectDirectory) {
        calculateDependencies();
        dataFiles.forEach(DataFile::process);
        templates.forEach(t -> t.process(config, projectDirectory));
        others.forEach(o -> o.process(config, projectDirectory));
    }

    private void calculateDependencies() {
        ImmutableMap<String, Layout> layoutsByName = Maps.uniqueIndex(layouts, Layout::getName);

        for (Template template : templates) {
            String layoutName = template.getLayout();
            if (!layoutsByName.containsKey(template.getLayout())) {
                throw new IllegalStateException("There is no layout file for " + template.getLayout() + ".ftl for layout: " + template.getLayout());
            }
            Layout layout = layoutsByName.get(layoutName);
            template.setLayout(layout);
        }
    }
}

