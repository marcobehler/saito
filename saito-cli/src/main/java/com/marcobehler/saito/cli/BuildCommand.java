package com.marcobehler.saito.cli;

import com.beust.jcommander.Parameters;

import static com.marcobehler.saito.cli.BuildCommand.DESCRIPTION;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Parameters(commandDescription = DESCRIPTION)
class BuildCommand {

    public static final String DESCRIPTION = "Builds the static site for deployment";
}
