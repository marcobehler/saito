package com.marcobehler.saito.core;

import dagger.Component;

import javax.inject.Singleton;

/**
 *
 */
@Singleton
@Component(modules = { PathsModule.class })
public interface Saito$$ {
    Saito saito();
}
