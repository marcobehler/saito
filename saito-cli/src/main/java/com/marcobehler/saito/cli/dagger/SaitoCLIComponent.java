package com.marcobehler.saito.cli.dagger;

import com.marcobehler.saito.cli.SaitoCLI;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Component(modules = SaitoCLIModule.class)
@Singleton
public interface SaitoCLIComponent {

    SaitoCLI saitoCLI();
}
