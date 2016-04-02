package com.marcobehler.saito.core;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.FileSystem;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class AbstractInMemoryFileSystemTest {

    protected FileSystem fs;

    @Before
    public void before() {
        fs = Jimfs.newFileSystem(Configuration.unix());
    }

    @After
    public void after() throws IOException {
        if (fs != null) {
            fs.close();
        }
    }
}
