package com.marcobehler.saito.core;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public abstract class BaseInMemoryFSTest {

    protected FileSystem fs;

    protected Path workingDirectory;
    protected Path sourceDirectory;

    @Before
    public void before() throws IOException {

        fs = Jimfs.newFileSystem("saito-test", Configuration.unix());
        workingDirectory = fs.getPath("/");
        sourceDirectory = fs.getPath("/source");
        Files.createDirectories(sourceDirectory);
    }

    @After
    public void after() throws IOException {
        if (fs != null) {
            fs.close();
        }
    }

    protected Path newFile(String filename) throws IOException {
        final Path f = workingDirectory.resolve(filename);
        Files.write(f, "".getBytes());
        return f;
    }
}
