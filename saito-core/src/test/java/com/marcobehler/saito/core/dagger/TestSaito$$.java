package com.marcobehler.saito.core.dagger;

import javax.inject.Singleton;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.freemarker.FreemarkerModule;
import com.marcobehler.saito.core.processing.ProcessingModule;
import com.marcobehler.saito.core.rendering.RenderingModule;

import dagger.Component;

/**
 *
 */
@Singleton
@Component(modules = { TestPathsModule.class, RenderingModule.class, FreemarkerModule.class, ProcessingModule.class})
public interface TestSaito$$ {
    Saito saito();
}
