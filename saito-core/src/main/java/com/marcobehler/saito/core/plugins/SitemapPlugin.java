package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.events.FileEventSubscriber;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Created by BEHLEMA on 22.08.2016.
 */
@Singleton
@Slf4j
public class SitemapPlugin  implements Plugin {

    @Inject
    public SitemapPlugin() {    }

    @Override
    public void start(Saito saito) {
        SaitoConfig cfg = saito.getRenderingModel().getSaitoConfig();

        if (!cfg.isGenerateSitemap()) {
            return;
        }

        try {
            log.info("Generating Sitemap...");
            Path buildDir = saito.getWorkingDir().resolve("build");
            // TODO urls
            WebSitemapGenerator wsg = WebSitemapGenerator.builder(cfg.getHost(), null)
                    .autoValidate(true)
                    .build();
            wsg.addUrl(cfg.getHost());
            List<String> strings = wsg.writeAsStrings();
            if (strings.size() >1 ) {
                throw new UnsupportedOperationException("Multiple Sitemap files not yet supported");
            }
            Path sitemapFile = buildDir.resolve("sitemap.xml");
            Files.write(sitemapFile, strings.get(0).getBytes("UTF-8"));
            log.info("Sitemap successfully written to {}", sitemapFile);
        } catch (Exception e) {
            log.error("Sitemap error", e);
        }
    }

    @Override
    public Integer getOrder() {
        return 9;
    }
}
