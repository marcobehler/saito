package com.marcobehler.saito.cli;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.plugins.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class SaitoCLITest {


    @Mock
    private Saito saito;

    @Mock
    private Set<Plugin> plugins;

    @InjectMocks
    private SaitoCLI saitoCLI;

    @Before
    public void setup() {
        saitoCLI.cliPlugins = new HashSet<>();
    }

    @Test
    public void cli_can_run_inits() {
        saitoCLI.run(new String[]{"init"});
        verify(saito).init(null);
    }

    @Test
    public void cli_can_run_inits_in_subfolder() {
        saitoCLI.run(new String[]{"init", "mySubFolder"});
        verify(saito).init("mySubFolder");
    }

    @Test
    public void cli_can_run_builds() {
        saitoCLI.run(new String[]{"build"});
        verify(saito).build(anySet());
    }

    @Test
    public void cli_can_run_server() {
        saitoCLI.run(new String[]{"server"});
        verify(saito).build(anySet());
    }

    @Test
    public void cli_can_run_help() {
        PrintStream out = mock(PrintStream.class);
        System.setOut(out);
        saitoCLI.run(new String[]{"-help"});
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
        verify(saito).clean();
    }


    @Test
    public void cli_can_show_version() {
        PrintStream out = mock(PrintStream.class);
        System.setOut(out);
        saitoCLI.run(new String[]{"-version"});
        verify(out).println(contains("Saito 1.0-SNAPSHOT"));
    }

}
