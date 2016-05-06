package com.marcobehler.saito.core.rendering;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.marcobehler.saito.core.freemarker.FreemarkerRenderer;

import static dagger.Provides.Type.SET_VALUES;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Module
public class RenderingModule {

    @Provides(type = SET_VALUES)
    @Singleton
    static Set<Renderer> renderers(FreemarkerRenderer freemarkerRenderer) {
        return new HashSet<>(Arrays.asList(freemarkerRenderer));
    }
}
