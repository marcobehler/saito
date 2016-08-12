package com.marcobehler.saito.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class JimFsPathsModule {

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
