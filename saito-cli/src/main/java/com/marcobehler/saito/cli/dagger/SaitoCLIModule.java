package com.marcobehler.saito.cli.dagger;

import com.marcobehler.saito.cli.SaitoCLI;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.SaitoModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Module(includes = SaitoModule.class)
public class SaitoCLIModule {

    @Singleton
    @Provides
    public static SaitoCLI saitoCLI(Saito saito) {
        return new SaitoCLI(saito);
    }
}
