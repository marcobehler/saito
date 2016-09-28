package com.marcobehler.saito.core.watcher;

import com.marcobehler.saito.core.BaseInMemoryFSTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SourceWatcherTest extends BaseInMemoryFSTest {

    @Test
    public void test() throws IOException, InterruptedException {
        Path path = fs.getPath("/");

        CountDownLatch latch = new CountDownLatch(1);

        (new Thread(() -> {
            SourceWatcher s = null;
            try {
                s = new SourceWatcher(path, true) {
                    @Override
                    protected void onEntryCreate(Path modifiedFile) {
                        latch.countDown();
                    }
                };
                s.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();

        Thread.sleep(500);

        Files.write(path.resolve("neueDatei.txt"), "hallo".getBytes());
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();

    }
}
