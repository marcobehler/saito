package com.marcobehler.saito.core;

import javax.inject.Singleton;

import dagger.Component;

/**
 *
 */
@Singleton
@Component(modules = { SaitoModule.class })
public interface Saito$$ {
    Saito saito();
}
