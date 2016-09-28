package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.configuration.SaitoConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class JettyPluginTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void start_jetty() throws IOException {

        Saito saito = mock(Saito.class);
        when(saito.getWorkingDir()).thenReturn(Paths.get(folder.getRoot().toString()));

        SaitoConfig saitoConfig = mock(SaitoConfig.class);
        Model model = mock(Model.class);
        when(saito.getModel()).thenReturn(model);
        when(saitoConfig.getPort()).thenReturn(1111);

        File buildFolder = folder.newFolder("build");

        String htmlContent = "<p>Hello World</p>";
        Files.write(new File(buildFolder, "index.html").toPath(), htmlContent.getBytes());

        new Thread(() -> {
            new JettyPlugin(saitoConfig).start(saito, Collections.emptyList());
        }).start();

        String inputLine = httpGet(saitoConfig.getPort());
        assertThat(inputLine).isEqualToIgnoringWhitespace(htmlContent);
    }

    private String httpGet(Integer port) throws IOException {
        URL oracle = new URL("http://localhost:" + port);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine = in.readLine();
        in.close();
        return inputLine;
    }

}
