package com.marcobehler.saito.cli.dagger;

import com.marcobehler.saito.cli.SaitoCLI;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.SaitoModule;
import com.marcobehler.saito.core.events.FileEventSubscriber;
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
    static SaitoCLI saitoCLI(Saito saito, Set<Plugin> plugins) {
        return new SaitoCLI(saito, plugins);
    }

    @Provides(type = SET)
    @Singleton
    static Plugin jettyPlugin() {
        return new JettyPlugin();
    }

    @Provides(type = SET)
    @Singleton
    static Plugin liveReloadPluginAsSet(LiveReloadPlugin liveReloadPlugin) {
        return liveReloadPlugin;
    }

    @Provides(type = SET)
    @Singleton
    static FileEventSubscriber liveReloadAsFileEventSubscriber(LiveReloadPlugin liveReloadPlugin) {
        return liveReloadPlugin;
    }

    @Provides
    @Singleton
    static LiveReloadPlugin liveReloadPlugin() {
        return new LiveReloadPlugin();
    }

    @Provides(type = SET)
    @Singleton
    static Plugin fileWatcherPlugin(Set<FileEventSubscriber> fileEventSubscribers) {
        return new FileWatcherPlugin(fileEventSubscribers);
    }
}
