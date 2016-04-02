package com.marcobehler.saito.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.Parameter;
import com.marcobehler.saito.cli.jetty.JettyServer;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.watcher.SourceWatcher;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */

public class SaitoCLI {

    @Parameter(names = {"-help", "-h"}, help = true)
    private boolean help;

    @Parameter(names = {"-version", "-v"})
    private boolean version;

    private InitCommand initCommand = new InitCommand();
    private BuildCommand buildCommand = new BuildCommand();
    private CleanCommand cleanCommand = new CleanCommand();
    private ServerCommand serverCommand = new ServerCommand();

    private Saito saito;
    private JCommander jc;

    public SaitoCLI() {
        jc = jCommander();
        saito = new Saito();
    }

    /**
     * Main Entry Point
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SaitoCLI saitoCLI = new SaitoCLI();
        saitoCLI.run(args);
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

        // TODO proper values here
        if (version) {
            System.out.println("------------------------------------------------------------");
            System.out.println("Saito 0.1");
            System.out.println("------------------------------------------------------------");
            System.out.println("");
            System.out.println("Build time: " + "whenever");
            return;
        }

        Path workingDirectory = getCurrentWorkingDir();

        if ("init".equals(jc.getParsedCommand())) {
            saito.init(workingDirectory, initCommand.getTarget());

        } else if ("build".equals(jc.getParsedCommand())) {
            saito.build(workingDirectory);

        } else if ("clean".equals(jc.getParsedCommand())) {
            saito.clean(workingDirectory);
        }
         else if ("server".equals(jc.getParsedCommand())) {
            saito.build(workingDirectory);
            String buildDir = workingDirectory.resolve("build").toAbsolutePath().normalize().toString();

            new Thread(() -> {
                try {
                    new SourceWatcher(workingDirectory.resolve("source"), true).processEvents();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            new JettyServer().run(buildDir, 8820);
        }
    }

    private JCommander jCommander() {
        JCommander jc = new JCommander(this);
        jc.addCommand("init", initCommand);
        jc.addCommand("build", buildCommand);
        jc.addCommand("clean", cleanCommand);
        jc.addCommand("server", serverCommand);
        return jc;
    }

    Path getCurrentWorkingDir() {
        return Paths.get(".").toAbsolutePath().normalize();
    }
}
