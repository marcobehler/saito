package com.marcobehler.saito.core.dagger;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.rendering.RenderingModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 *
 */
@Singleton
@Component(modules = { PathsModule.class, RenderingModule.class})
public interface Saito$$ {
    Saito saito();
}
