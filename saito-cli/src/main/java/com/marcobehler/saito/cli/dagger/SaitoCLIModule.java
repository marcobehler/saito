package com.marcobehler.saito.cli.dagger;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Singleton;

import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.events.FileEventSubscriber;
import com.marcobehler.saito.core.plugins.FileWatcherPlugin;
import com.marcobehler.saito.core.plugins.JettyPlugin;
import com.marcobehler.saito.core.plugins.LiveReloadPlugin;
import com.marcobehler.saito.core.plugins.Plugin;

import dagger.Module;
import dagger.Provides;
import static dagger.Provides.Type.SET_VALUES;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Module(includes = PathsModule.class)
public class SaitoCLIModule {

    // plugins

    @Provides(type = SET_VALUES)
    @Singleton
    static Set<Plugin> plugins(JettyPlugin jettyPlugin, LiveReloadPlugin liveReloadPlugin, FileWatcherPlugin fileWatcherPlugin ) {
        return new TreeSet<>(Arrays.asList(jettyPlugin, liveReloadPlugin, fileWatcherPlugin));
    }

    // file event subscribers

    @Provides(type = SET_VALUES)
    @Singleton
    static Set<FileEventSubscriber> fileEventSubscribers(LiveReloadPlugin liveReloadPlugin) {
        return Collections.singleton(liveReloadPlugin);
    }
}
