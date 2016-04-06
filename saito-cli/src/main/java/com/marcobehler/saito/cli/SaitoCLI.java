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
import com.marcobehler.saito.core.plugins.JettyPlugin;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.plugins.Plugin;
import com.marcobehler.saito.core.watcher.SourceWatcher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.livereload.LiveReloadServer;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

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
    private final Set<Plugin> plugins;

    private InitCommand initCommand;
    private JCommander jc;

    @Inject
    public SaitoCLI(Saito saito, Set<Plugin> plugins) {
        jc = jCommander();
        this.saito = saito;
        this.plugins = plugins;
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

            if (plugins != null) {
                new TreeSet<>(plugins)
                        .stream()
                        .forEach(plugin -> plugin.start(saito));
            }
        }
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
