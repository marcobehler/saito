package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.rendering.Model;

/**
 * Created by marco on 17.09.2016.
 */
public interface Processor<T extends SaitoFile> {

    public void process(T t, Model model);
}
