package com.marcobehler.saito.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

import static com.marcobehler.saito.cli.commands.InitCommand.DESCRIPTION;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Parameters(commandDescription = DESCRIPTION)
public class InitCommand {

    public static final String DESCRIPTION = "Create new project at TARGET";

    @Parameter(description = "the TARGET sub-folder")
    List<String> target = null;

    public String getTarget() {
        return target != null ? target.get(0) : null;
    }
}
