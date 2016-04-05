package com.marcobehler.saito.core;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;


@Module
public class PathsModule {

    @Singleton
    @Named("workingDir")
    @Provides
    public static Path workingDir() {
        return Paths.get(".").toAbsolutePath().normalize();
    }


    @Singleton
    @Named("configFile")
    @Provides
    public static Path configFile() {
        return Paths.get("./config.yaml").toAbsolutePath().normalize();
    }
}
