package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class PluginTest {

    @Test
    public void test_ordering() {
        Set<Plugin> plugins = new HashSet<>();

        FileWatcherPlugin fileWatcherPlugin = new FileWatcherPlugin(Collections.emptySet());
        plugins.add(fileWatcherPlugin);

        SaitoConfig config = new SaitoConfig(null);

        JettyPlugin jettyPlugin = new JettyPlugin(config);
        plugins.add(jettyPlugin);

        LiveReloadPlugin liveReloadPlugin = new LiveReloadPlugin(config);
        plugins.add(liveReloadPlugin);

        TreeSet<Plugin> sortedPlugins = new TreeSet<>(plugins);
        assertThat(sortedPlugins.first()).isEqualTo(liveReloadPlugin);
        assertThat(sortedPlugins.last()).isEqualTo(jettyPlugin);
    }


}
