package com.marcobehler.saito.core;

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
