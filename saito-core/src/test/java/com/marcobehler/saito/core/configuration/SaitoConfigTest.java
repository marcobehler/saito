package com.marcobehler.saito.core.configuration;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SaitoConfigTest extends BaseInMemoryFSTest {

    @Test
    public void get_default_config_with_null_path_returns_default() {
        SaitoConfig config = new SaitoConfig(null);

        assertThat(config.isDirectoryIndexes()).isEqualTo(false);
        assertThat(config.isRelativeLinks()).isEqualTo(false);
    }

    @Test
    public void get_default_config_with_non_existing_path_returns_default() {
        SaitoConfig config = new SaitoConfig(fs.getPath("/bla.yaml"));

        assertThat(config.isDirectoryIndexes()).isEqualTo(false);
        assertThat(config.isRelativeLinks()).isEqualTo(false);
    }


    @Test
    public void get_config_from_path_directory_indexes() throws IOException {
        Path configFile = Files.write(fs.getPath("/test.yaml"), "directoryIndexes : true".getBytes("UTF-8"));

        SaitoConfig config = new SaitoConfig(configFile);
        assertThat(config.isDirectoryIndexes()).isEqualTo(true);
    }


    @Test
    public void get_config_from_path_relative_links() throws IOException {
        Path configFile = Files.write(fs.getPath("/test.yaml"), "relativeLinks: true".getBytes("UTF-8"));

        SaitoConfig config = new SaitoConfig(configFile);
        assertThat(config.isRelativeLinks()).isEqualTo(true);
    }


    @Test
    public void get_config_from_path_port() throws IOException {
        Path configFile = Files.write(fs.getPath("/test.yaml"), "port : 9999".getBytes("UTF-8"));

        SaitoConfig config = new SaitoConfig(configFile);
        assertThat(config.getPort()).isEqualTo(9999);
    }

    @Test
    public void get_config_from_path_livereload() throws IOException {
        Path configFile = Files.write(fs.getPath("/test.yaml"), "liveReloadEnabled : true".getBytes("UTF-8"));

        SaitoConfig config = new SaitoConfig(configFile);
        assertThat(config.isLiveReloadEnabled()).isEqualTo(true);
    }


    @Test
    public void get_config_from_path_generateSitemap() throws IOException {
        Path configFile = Files.write(fs.getPath("/test.yaml"), "generateSitemap : true".getBytes("UTF-8"));

        SaitoConfig config = new SaitoConfig(configFile);
        assertThat(config.isGenerateSitemap()).isEqualTo(true);
    }
}
