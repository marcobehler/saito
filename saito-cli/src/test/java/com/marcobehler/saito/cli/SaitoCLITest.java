package com.marcobehler.saito.cli;

import com.marcobehler.saito.core.Saito;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintStream;

import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class SaitoCLITest {

    @Mock
    private Saito saito;

    @InjectMocks
    private SaitoCLI saitoCLI;

    @Test
    public void cli_can_run_inits() {
        saitoCLI.run(new String[]{"init"});
        verify(saito).init(saitoCLI.getCurrentWorkingDir(), null);
    }

    @Test
    public void cli_can_run_inits_in_subfolder() {
        saitoCLI.run(new String[]{"init", "mySubFolder"});
        verify(saito).init(saitoCLI.getCurrentWorkingDir(), "mySubFolder");
    }

    @Test
    public void cli_can_run_builds() {
        saitoCLI.run(new String[]{"build"});
        verify(saito).build(saitoCLI.getCurrentWorkingDir());
    }

    @Test
    public void cli_can_run_help() {
        PrintStream out = mock(PrintStream.class);
        System.setOut(out);
        saitoCLI.run(new String[]{"--help"});
        verify(out).println(startsWith("Usage:"));
    }

    @Test
    public void cli_shows_help_if_unkown_command_is_given() {
        PrintStream out = mock(PrintStream.class);
        System.setOut(out);
        saitoCLI.run(new String[]{"thisCommandDoesNotExist"});
        verify(out).println(startsWith("Usage:"));
    }

    @Test
    public void cli_can_runs_help_when_no_argument_is_given() {
        PrintStream out = mock(PrintStream.class);
        System.setOut(out);
        saitoCLI.run(new String[]{});
        verify(out).println(startsWith("Usage:"));
    }

    @Test
    public void cli_can_run_clean_command() {
        saitoCLI.run(new String[]{"clean"});
        verify(saito).clean(saitoCLI.getCurrentWorkingDir());
    }
}
