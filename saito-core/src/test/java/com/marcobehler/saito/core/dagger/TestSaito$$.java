package com.marcobehler.saito.core.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.freemarker.FreemarkerModule;
import com.marcobehler.saito.core.processing.ProcessingModule;
import com.marcobehler.saito.core.processing.TargetPathFinder;
import com.marcobehler.saito.core.rendering.RenderingModule;

import dagger.Component;

import java.nio.file.Path;

import static com.marcobehler.saito.core.dagger.PathsModule.BUILD_DIR;
import static com.marcobehler.saito.core.dagger.TestPathsModule.SOURCES_DIR;

/**
 *
 */
@Singleton
@Component(modules = { TestPathsModule.class, RenderingModule.class, FreemarkerModule.class, ProcessingModule.class})
public interface TestSaito$$ {
    Saito saito();

    TargetPathFinder targetPathFinder();

    @Named(SOURCES_DIR)
    Path sourcesDir();

    @Named(BUILD_DIR)
    Path buildDir();
}
