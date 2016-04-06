package com.marcobehler.saito.cli.dagger;

import com.marcobehler.saito.cli.SaitoCLI;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.SaitoModule;
import com.marcobehler.saito.core.plugins.FileWatcherPlugin;
import com.marcobehler.saito.core.plugins.JettyPlugin;
import com.marcobehler.saito.core.plugins.LiveReloadPlugin;
import com.marcobehler.saito.core.plugins.Plugin;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

import java.util.Set;

import static dagger.Provides.Type.SET;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Module(includes = SaitoModule.class)
public class SaitoCLIModule {

    @Singleton
    @Provides
    public static SaitoCLI saitoCLI(Saito saito, Set<Plugin> plugins) {
        return new SaitoCLI(saito, plugins);
    }

    @Provides(type = SET)
    Plugin jettyPlugin() {
        return new JettyPlugin();
    }

    @Provides(type = SET)
    Plugin liveReloadPluginSetParam() {
        return new LiveReloadPlugin();
    }

    @Provides(type = SET)
    Plugin fileWatcherPlugin() {
        return new FileWatcherPlugin();
    }
}
