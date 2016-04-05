package com.marcobehler.saito.core;

import com.marcobehler.saito.core.configuration.SaitoConfig;
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
    public static SaitoConfig saitoConfig(@Named("configFile") Path path) {
        return new SaitoConfig(path);
    }

}
