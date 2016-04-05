package com.marcobehler.saito.cli.jetty;

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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class JettyServerStart {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void start_jetty() throws IOException {
        File indexHtml = folder.newFile("index.html");
        String htmlContent = "<p>Hello World</p>";
        final int port = 1111;

        Files.write(indexHtml.toPath(), htmlContent.getBytes());

        new Thread(() -> {
            new JettyServer().start(indexHtml.getParent(), port);
        }).start();

        String inputLine = httpGet(port);
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
