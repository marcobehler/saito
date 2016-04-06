package com.marcobehler.saito.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Commands {

    @Parameters(commandDescription = "Cleans the process directory")
    class CleanCommand {}

    @Parameters(commandDescription = "Builds the static site for deployment")
    class BuildCommand {}

    @Parameters(commandDescription = "Create new project at TARGET")
    class InitCommand {

        @Parameter(description = "the TARGET sub-folder")
        List<String> target = null;

        public String getTarget() {
            return target != null ? target.get(0) : null;
        }
    }

    @Parameters(commandDescription = "Start the preview server")
    class ServerCommand {}
}
