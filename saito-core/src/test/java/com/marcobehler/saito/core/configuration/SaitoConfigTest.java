package com.marcobehler.saito.core.configuration;

import com.marcobehler.saito.core.AbstractInMemoryFileSystemTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SaitoConfigTest extends AbstractInMemoryFileSystemTest {

    @Before
    public void setup() {

    }

    @Test
    public void get_default_config_with_null_path() {
       /* SaitoConfig defaultConfig = SaitoConfig.getOrDefault(null);

        assertThat(defaultConfig.isDirectoryIndexes()).isEqualTo(false);
        assertThat(defaultConfig.isRelativeLinks()).isEqualTo(false);*/
    }

    @Test
    public void get_default_config_with_non_existing_path() {
      /*  SaitoConfig defaultConfig = SaitoConfig.getOrDefault(Paths.get("/bla.yaml"));

        assertThat(defaultConfig.isDirectoryIndexes()).isEqualTo(false);
        assertThat(defaultConfig.isRelativeLinks()).isEqualTo(false);*/
    }


    @Test
    public void get_config_from_path() throws IOException {
      /*  Path configFile = Files.write(fs.getPath("/test.yaml"), "directoryIndexes : true\nrelativeLinks: true".getBytes("UTF-8"));

        SaitoConfig defaultConfig = SaitoConfig.getOrDefault(configFile);

        assertThat(defaultConfig.isDirectoryIndexes()).isEqualTo(true);
        assertThat(defaultConfig.isRelativeLinks()).isEqualTo(true);*/
    }
}
