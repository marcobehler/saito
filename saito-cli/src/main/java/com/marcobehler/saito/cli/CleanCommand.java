package com.marcobehler.saito.cli;

import com.beust.jcommander.Parameters;

import static com.marcobehler.saito.cli.InitCommand.DESCRIPTION;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Parameters(commandDescription = DESCRIPTION)
public class CleanCommand {
    public static final String DESCRIPTION = "Cleans the process directory";
}
