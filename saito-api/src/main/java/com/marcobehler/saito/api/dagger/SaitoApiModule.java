package com.marcobehler.saito.api.dagger;

import com.marcobehler.saito.core.dagger.PathsModule;
import com.marcobehler.saito.core.events.FileEventSubscriber;
import com.marcobehler.saito.core.freemarker.FreemarkerModule;
import com.marcobehler.saito.core.plugins.Plugin;
import com.marcobehler.saito.core.plugins.SitemapPlugin;
import com.marcobehler.saito.core.rendering.RenderingModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Set;

import static dagger.Provides.Type.SET_VALUES;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Module(includes =  {PathsModule.class, RenderingModule.class, FreemarkerModule.class})
public class SaitoApiModule {

    // plugins

    @Provides(type = SET_VALUES)
    @Singleton
    static Set<Plugin> plugins(SitemapPlugin sitemapPlugin) {
        return Collections.singleton(sitemapPlugin);
    }

    // file event subscribers

    @Provides(type = SET_VALUES)
    @Singleton
    static Set<FileEventSubscriber> fileEventSubscribers() {
        return Collections.emptySet();
    }
}
