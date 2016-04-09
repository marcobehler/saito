package com.marcobehler.saito.core;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public abstract class BaseInMemoryFSTest {

    protected FileSystem fs;

    protected Path workingDirectory;

    @Before
    public void before() {
        fs = Jimfs.newFileSystem(Configuration.unix());
        workingDirectory = fs.getPath("/");
    }

    @After
    public void after() throws IOException {
        if (fs != null) {
            fs.close();
        }
    }
}
