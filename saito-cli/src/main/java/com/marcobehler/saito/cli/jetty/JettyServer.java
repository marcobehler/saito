package com.marcobehler.saito.cli.jetty;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class JettyServer {

    private Server server;

    /**
     * Run Jetty web server serving out supplied path on supplied port
     *
     * @param dir
     * @param port
     */
    public void start(String dir, Integer port) {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase(dir);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
        server.setHandler(handlers);

        log.info("Serving out contents of: [{}] on http://localhost:{}/", dir, port);
        log.info("(To stop server hit CTRL-C)");

        try {
            server.start();
            openBrowserIfPossible(port);
            server.join();
        } catch (Exception e) {
            log.error("Problems with Jetty Server", e);
        }
    }

    private void openBrowserIfPossible(Integer port) throws IOException, URISyntaxException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://localhost:" + port));
        }
    }
}
