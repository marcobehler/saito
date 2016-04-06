package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Plugin extends Comparable<Plugin> {

    void start(Saito saito);

    Integer getOrder();

    @Override
    default int compareTo(Plugin o) {
        return getOrder().compareTo(o.getOrder());
    }

}
