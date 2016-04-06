package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;

import java.util.Comparator;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Plugin extends Comparator<Plugin> {

    void start(Saito saito);

    Integer getOrder();

    @Override
    default int compare(Plugin o1, Plugin o2) {
        return o1.getOrder().compareTo(o2.getOrder());
    }

}
