package com.marcobehler.saito.core.dagger;

import dagger.Module;
import dagger.Provides;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;


@Module
public class PathsModule {

    public static final String WORKING_DIR = "workingDir";
    public static final String SOURCES_DIR = "source";
    public static final String CONFIG_FILE = "configFile";

    @Singleton
    @Named(SOURCES_DIR)
    @Provides
    public Path sourceDir() {
        return Paths.get(".").resolve("source").toAbsolutePath().normalize();
    }

    @Singleton
    @Named(WORKING_DIR)
    @Provides
    public Path workingDir() {
        return Paths.get(".").toAbsolutePath().normalize();
    }


    @Singleton
    @Named(CONFIG_FILE)
    @Provides
    @Nullable
    public Path configFile() {
        return Paths.get("./config.yaml").toAbsolutePath().normalize();
    }
}
