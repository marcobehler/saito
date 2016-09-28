package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.files.SaitoFile;
import dagger.MapKey;

/**
 * Created by BEHLEMA on 28.09.2016.
 */
@MapKey
@interface ProcessorClassKey {
    Class<? extends SaitoFile> value();
}
