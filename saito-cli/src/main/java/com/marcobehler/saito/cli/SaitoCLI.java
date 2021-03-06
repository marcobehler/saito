package com.marcobehler.saito.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.Parameter;
import com.marcobehler.saito.cli.dagger.DaggerSaitoCLIComponent;
import com.marcobehler.saito.cli.dagger.SaitoCLIComponent;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.plugins.Plugin;
import com.marcobehler.saito.core.plugins.SitemapPlugin;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */

@Slf4j
@Singleton
public class SaitoCLI {

    @Parameter(names = {"-help", "-h"}, help = true)
    private boolean help;

    @Parameter(names = {"-version", "-v"})
    private boolean version;

    private final Saito saito;
    Set<Plugin> cliPlugins = new HashSet<>();

    private SaitoCLICommands.InitCommand initCommand;
    private JCommander jc;

    @Inject
    public SaitoCLI(Saito saito, Set<Plugin> cliPlugins) {
        jc = jCommander();
        this.saito = saito;
        this.cliPlugins = cliPlugins;
    }

    private JCommander jCommander() {
        JCommander jc = new JCommander(this);
        jc.addCommand("init", initCommand = new SaitoCLICommands.InitCommand());
        jc.addCommand("build", new SaitoCLICommands.BuildCommand());
        jc.addCommand("clean", new SaitoCLICommands.CleanCommand());
        jc.addCommand("server", new SaitoCLICommands.ServerCommand());
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
            TreeSet<Plugin> plugins = cliPlugins.stream().filter(p -> p instanceof SitemapPlugin).collect(Collectors.toCollection(TreeSet::new));
            saito.build(plugins);
        } else if ("clean".equals(jc.getParsedCommand())) {
            saito.clean();
        } else if ("server".equals(jc.getParsedCommand())) {
            saito.build(cliPlugins);
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
