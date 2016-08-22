package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.files.Sources;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Plugin extends Comparable<Plugin> {

    void start(Saito saito, Sources sources);

    Integer getOrder();

    @Override
    default int compareTo(Plugin o) {
        return getOrder().compareTo(o.getOrder());
    }

}
