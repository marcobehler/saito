package com.marcobehler.saito.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.Parameter;
import com.marcobehler.saito.cli.commands.BuildCommand;
import com.marcobehler.saito.cli.commands.CleanCommand;
import com.marcobehler.saito.cli.commands.InitCommand;
import com.marcobehler.saito.cli.commands.ServerCommand;
import com.marcobehler.saito.cli.dagger.DaggerSaitoCLIComponent;
import com.marcobehler.saito.cli.dagger.SaitoCLIComponent;
import com.marcobehler.saito.cli.jetty.JettyServer;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.watcher.SourceWatcher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.livereload.LiveReloadServer;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */

@Slf4j
public class SaitoCLI {

    @Parameter(names = {"-help", "-h"}, help = true)
    private boolean help;

    @Parameter(names = {"-version", "-v"})
    private boolean version;

    private final Saito saito;

    private InitCommand initCommand;
    private JCommander jc;

    @Inject
    public SaitoCLI(Saito saito) {
        jc = jCommander();
        this.saito = saito;
    }

    private JCommander jCommander() {
        JCommander jc = new JCommander(this);
        jc.addCommand("init", initCommand = new InitCommand());
        jc.addCommand("build", new BuildCommand());
        jc.addCommand("clean", new CleanCommand());
        jc.addCommand("server", new ServerCommand());
        return jc;
    }

    /**
     * Main Entry Point
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SaitoCLIComponent cliComponent = DaggerSaitoCLIComponent.builder().build();
        SaitoCLI saitoCli = cliComponent.saitoCLI();
        saitoCli.run(args);
    }

    @SneakyThrows
    void run(String[] args) {
        if ((args == null || args.length == 0)) {
            jc.usage();
            return;
        }

        try {
            jc.parse(args);
        } catch (MissingCommandException e) {
            jc.usage();
            return;
        }

        if (help) {
            jc.usage();
            return;
        }

        if (version) {
            printVersionInformation();
            return;
        }


        handleCommand();
    }

    private void handleCommand() {
        if ("init".equals(jc.getParsedCommand())) {
            saito.init(initCommand.getTarget());
        } else if ("build".equals(jc.getParsedCommand())) {
            saito.build();
        } else if ("clean".equals(jc.getParsedCommand())) {
            saito.clean();
        } else if ("server".equals(jc.getParsedCommand())) {
            saito.build();

            LiveReloadServer liveReloadServer = enableLiveReloadIfNeeded();

            Path sourceDir = saito.getWorkingDir().resolve("source");
            startFileWatcher(sourceDir, liveReloadServer);

            Path buildDir = saito.getWorkingDir().resolve("build");
            startWebServer(buildDir);
        }
    }

    private LiveReloadServer enableLiveReloadIfNeeded() {
        LiveReloadServer liveReloadServer = null;
        SaitoConfig config = saito.getSaitoConfig();
        if (config.isLiveReloadEnabled()) {
            try {
                liveReloadServer = new LiveReloadServer();
                liveReloadServer.start();
            } catch (IOException e) {
                log.error("Problem starting LiveReload", e);
            }
        }
        return liveReloadServer;
    }

    private void startFileWatcher(Path dir, LiveReloadServer liveReloadServer) {
        new Thread(() -> {
            try {
                new SourceWatcher(dir, true).setLiveReload(liveReloadServer).processEvents();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startWebServer(Path dir) {
        new JettyServer().start(dir.toString(), 8820);
    }

    private void printVersionInformation() throws IOException {
        Properties properties = new Properties();
        properties.load(SaitoCLI.class.getResourceAsStream("/project.properties"));
        System.out.println("");
        System.out.println("------------------------------------------------------------");
        System.out.println("Saito " + properties.get("PROJECT_VERSION"));
        System.out.println("------------------------------------------------------------");
        System.out.println("");
        System.out.println("Build time: " + properties.get("PROJECT_BUILD_DATE"));
        System.out.println("");
    }
}
