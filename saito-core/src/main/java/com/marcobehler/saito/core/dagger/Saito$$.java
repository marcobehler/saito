package com.marcobehler.saito.core.dagger;

import com.marcobehler.saito.core.Saito;
import dagger.Component;

import javax.inject.Singleton;

/**
 *
 */
@Singleton
@Component(modules = { SaitoModule.class })
public interface Saito$$ {
    Saito saito();
}
