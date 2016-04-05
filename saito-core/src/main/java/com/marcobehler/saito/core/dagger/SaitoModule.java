package com.marcobehler.saito.core.dagger;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.freemarker.FreemarkerConfig;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Module(includes = PathsModule.class)
public class SaitoModule {

    @Singleton
    @Provides
    public static SaitoConfig saitoConfig(@Named(PathsModule.CONFIG_FILE) Path configFile, Lazy<FreemarkerConfig> freemarkerConfig) {
        return new SaitoConfig(configFile, freemarkerConfig);
    }

    @Singleton
    @Provides
    public static FreemarkerConfig freemarkerConfig(@Named(PathsModule.WORKING_DIR) Path workingDir) {
        return new FreemarkerConfig(workingDir);
    }

}
