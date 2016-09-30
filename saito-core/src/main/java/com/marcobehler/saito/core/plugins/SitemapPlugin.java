package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.rendering.Model;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.marcobehler.saito.core.dagger.PathsModule.BUILD_DIR;

/**
 * Created by BEHLEMA on 22.08.2016.
 */
@Singleton
@Slf4j
public class SitemapPlugin  implements Plugin, TemplatePostProcessor {

    private final SaitoConfig cfg;
    private final Path buildDir;

    private List<Path> renderedPaths = new ArrayList<>();

    @Inject
    public SitemapPlugin(SaitoConfig saitoConfig, @Named(BUILD_DIR) Path buildDir) {
        this.cfg = saitoConfig; this.buildDir = buildDir;
    }


    @Override
    public String onBeforeTemplateWrite(Path targetFile, String rendered) {
        renderedPaths.add(targetFile);
        return rendered;
    }

    @Override
    public void start(Saito saito, List<? extends SaitoFile> sources) {
        if (!cfg.isGenerateSitemap()) {
            log.info("Sitemaps are turned off.");
            return;
        }

        if (renderedPaths.isEmpty()) {
            return;
        }

        if (cfg.getHost() == null) {
            log.warn("You did not specify a 'host' property in config.yaml. Skipping generating sitemap...");
        }

        try {
            log.info("Generating Sitemap...");
            Path buildDir = saito.getWorkingDir().resolve("build");
            WebSitemapGenerator wsg = WebSitemapGenerator.builder(cfg.getHost(), null)
                    .autoValidate(true)
                    .build();

            renderedPaths.forEach(path -> {
                try {
                    wsg.addUrl(join(cfg, path));
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

    private String join(SaitoConfig cfg, Path absolutePath) {
        String host = cfg.getHost();
        if (!host.endsWith("/")) {
            host = host + "/";
        }


        String outputPath = buildDir.relativize(absolutePath).toString();
        outputPath = outputPath.replaceAll("\\\\", "/");

        if (outputPath.startsWith("/")) {
            outputPath = outputPath.substring(1);
        }

        String url = host + outputPath;

        if (url.endsWith("/index.html")) {
            url = url.substring(0, url.length() -11);
        }

        log.trace("Added URL to Sitemap: {}", url);

        return url;
    }

    @Override
    public Integer getOrder() {
        return 9;
    }

}
