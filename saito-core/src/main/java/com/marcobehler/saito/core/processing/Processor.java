package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.files.SaitoFile;

/**
 * Created by marco on 17.09.2016.
 */
public interface Processor<T extends SaitoFile> {

    public void process(T t);
}
