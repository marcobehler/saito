package com.marcobehler.saito.core.plugins;

import org.junit.Test;

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

        FileWatcherPlugin fileWatcherPlugin = new FileWatcherPlugin();
        plugins.add(fileWatcherPlugin);

        JettyPlugin jettyPlugin = new JettyPlugin();
        plugins.add(jettyPlugin);

        LiveReloadPlugin liveReloadPlugin = new LiveReloadPlugin();
        plugins.add(liveReloadPlugin);

        TreeSet<Plugin> sortedPlugins = new TreeSet<>(plugins);
        assertThat(sortedPlugins.first()).isEqualTo(jettyPlugin);
        assertThat(sortedPlugins.last()).isEqualTo(liveReloadPlugin);
    }


}
