package com.marcobehler.saito.cli.dagger;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Singleton;

import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.events.FileEventSubscriber;
import com.marcobehler.saito.core.freemarker.FreemarkerModule;
import com.marcobehler.saito.core.plugins.*;

import com.marcobehler.saito.core.processing.ProcessingModule;
import com.marcobehler.saito.core.rendering.RenderingModule;
import dagger.Module;
import dagger.Provides;
import static dagger.Provides.Type.SET_VALUES;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Module(includes =  {PathsModule.class, RenderingModule.class, FreemarkerModule.class, ProcessingModule.class})
public class SaitoCLIModule {

    // plugins

    @Provides(type = SET_VALUES)
    @Singleton
    static Set<Plugin> plugins(JettyPlugin jettyPlugin, LiveReloadPlugin liveReloadPlugin, FileWatcherPlugin fileWatcherPlugin, SitemapPlugin sitemapPlugin) {
        return new TreeSet<>(Arrays.asList(jettyPlugin, liveReloadPlugin, fileWatcherPlugin, sitemapPlugin));
    }

    // file event subscribers

    @Provides(type = SET_VALUES)
    @Singleton
    static Set<FileEventSubscriber> fileEventSubscribers(LiveReloadPlugin liveReloadPlugin) {
        return Collections.singleton(liveReloadPlugin);
    }
}
