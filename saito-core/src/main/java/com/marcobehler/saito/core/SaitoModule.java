package com.marcobehler.saito.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;

/**
 *
 */
@dagger.Module
public class SaitoModule {

    @Provides
    @Singleton
    @Named("workingDir")
    Path workingDir() {
        return Paths.get(".");
    }

    @Provides
    @Singleton
    @Named("configFile")
    Path configFile() {
        return Paths.get("./config.yaml");
    }
}
