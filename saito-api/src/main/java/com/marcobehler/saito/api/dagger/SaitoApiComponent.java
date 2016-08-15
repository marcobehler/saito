package com.marcobehler.saito.api.dagger;

import com.marcobehler.saito.core.Saito;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Component(modules = {SaitoApiModule.class})
@Singleton
public interface SaitoApiComponent {
    Saito saito();
}
