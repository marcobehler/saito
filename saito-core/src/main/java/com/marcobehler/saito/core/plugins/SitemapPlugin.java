package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.BlogPost;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.rendering.RenderingModel;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by BEHLEMA on 22.08.2016.
 */
@Singleton
@Slf4j
public class SitemapPlugin  implements Plugin {

    @Inject
    public SitemapPlugin() {    }

    @Override
    public void start(Saito saito, List<SaitoFile> sources) {
        SaitoConfig cfg = saito.getRenderingModel().getSaitoConfig();

        if (!cfg.isGenerateSitemap()) {
            return;
        }

        try {
            log.info("Generating Sitemap...");
            Path buildDir = saito.getWorkingDir().resolve("build");
            WebSitemapGenerator wsg = WebSitemapGenerator.builder(cfg.getHost(), null)
                    .autoValidate(true)
                    .build();

            // add all normal pages & blog posts
            sources.stream()
                    .filter(s -> s instanceof Template)
                    .map(s -> (Template) s)
                    .forEach(t -> {
                try {
                    wsg.addUrl(join(cfg, t));
                } catch (MalformedURLException e) {
                    log.error("Error", e);
                }
            });

            // write to String as otherwise we can only write to "File", not "Path". Messes up JimFS
            List<String> strings = wsg.writeAsStrings();
            if (strings.size() >1 ) {
                throw new UnsupportedOperationException("Multiple Sitemap files not yet supported");
            }

            // Finally Write to sitemap.xml
            Path sitemapFile = buildDir.resolve("sitemap.xml");
            Files.write(sitemapFile, strings.get(0).getBytes("UTF-8"));
            log.info("Sitemap successfully written to {}", sitemapFile);
        } catch (Exception e) {
            log.error("Sitemap error", e);
        }
    }

    private String join(SaitoConfig cfg, Template t) {
        String host = cfg.getHost();
        if (!host.endsWith("/")) {
            host = host + "/";
        }

        String outputPath = t.getTargetFile(new RenderingModel(cfg)).toString();
        outputPath = outputPath.replaceAll("\\\\", "/");

        if (outputPath.startsWith("/")) {
            outputPath = outputPath.substring(1);
        }

        String url = host + outputPath;

        if (url.endsWith("/index.html")) {
            url = url.substring(0, url.length() -11);
        }

        return url;
    }

    @Override
    public Integer getOrder() {
        return 9;
    }
}
