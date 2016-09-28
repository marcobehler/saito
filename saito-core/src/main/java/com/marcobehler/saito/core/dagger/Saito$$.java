package com.marcobehler.saito.core.dagger;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.freemarker.FreemarkerModule;
import com.marcobehler.saito.core.processing.ProcessingModule;
import com.marcobehler.saito.core.rendering.RenderingModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 *
 */
@Singleton
@Component(modules = { ProcessingModule.class, PathsModule.class, RenderingModule.class, FreemarkerModule.class })
public interface Saito$$ {
    Saito saito();
}
