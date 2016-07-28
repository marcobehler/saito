package com.marcobehler.saito.core.dagger;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class TestPathsModule {

    public static final String WORKING_DIR = "workingDir";
    public static final String SOURCES_DIR = "source";
    public static final String CONFIG_FILE = "configFile";

    @Singleton
    @Named(WORKING_DIR)
    @Provides
    public static Path workingDir(FileSystem fs) {
        return fs.getPath(".").toAbsolutePath().normalize();
    }


    @Singleton
    @Named(CONFIG_FILE)
    @Provides
    public static Path configFile(FileSystem fs) {
        return fs.getPath("./config.yaml").toAbsolutePath().normalize();
    }

    @Singleton
    @Named(SOURCES_DIR)
    @Provides
    public static Path sourceDir(FileSystem fs) {
        return fs.getPath(".").resolve("source").toAbsolutePath().normalize();
    }



    @Singleton
    @Provides
    public static FileSystem getFs() {
       return Jimfs.newFileSystem("saito-test", Configuration.unix());
    }
}
