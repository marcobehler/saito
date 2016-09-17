package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.SaitoFile;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
@Singleton
public class JettyPlugin implements Plugin {

    private Server server;

    @Inject
    public JettyPlugin() {
    }

    /**
     * Run Jetty web server serving out supplied path on the supplied port on SaitoConfig
     *
     */
    @Override
    public void start(Saito saito, List<SaitoFile> sources) {
        SaitoConfig cfg = saito.getRenderingModel().getSaitoConfig();
        String dir = saito.getWorkingDir().resolve("build").toString();

        server = new Server();
        ServerConnector connector = new ServerConnector(server);

        connector.setPort(cfg.getPort());
        server.addConnector(connector);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase(dir);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
        server.setHandler(handlers);

        log.info("Serving out contents of: [{}] on http://localhost:{}/", dir, cfg.getPort());
        log.info("(To stop server hit CTRL-C)");

        try {
            server.start();
            openBrowserIfPossible(cfg.getPort());
            server.join();
        } catch (Exception e) {
            log.error("Problems with Jetty Server", e);
        }
    }

    @Override
    public Integer getOrder() {
        return 30;
    }

    private void openBrowserIfPossible(Integer port) throws IOException, URISyntaxException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://localhost:" + port));
        }
    }
}
