package com.marcobehler.saito.core.watcher;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SourceWatcherTest {

    @Test
    @Ignore
    public void test() throws IOException {
        SourceWatcher s = new SourceWatcher(Paths.get("c:\\Users\\marco\\temp\\"), true);
        s.start();
    }
}
