package com.marcobehler.saito.core.dagger;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;


@Module
public class PathsModule {

    public static final String WORKING_DIR = "workingDir";
    public static final String CONFIG_FILE = "configFile";

    @Singleton
    @Named(WORKING_DIR)
    @Provides
    public static Path workingDir() {
        return Paths.get(".").toAbsolutePath().normalize();
    }


    @Singleton
    @Named(CONFIG_FILE)
    @Provides
    public static Path configFile() {
        return Paths.get("./config.yaml").toAbsolutePath().normalize();
    }
}
