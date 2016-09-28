package com.marcobehler.saito.core.plugins;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.files.SaitoFile;

import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface Plugin extends Comparable<Plugin> {

    void start(Saito saito, List<? extends SaitoFile> sources);

    Integer getOrder();

    @Override
    default int compareTo(Plugin o) {
        return getOrder().compareTo(o.getOrder());
    }

}
