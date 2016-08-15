package com.marcobehler.saito.api;

import com.marcobehler.saito.api.dagger.DaggerSaitoApiComponent;
import com.marcobehler.saito.api.dagger.SaitoApiComponent;
import com.marcobehler.saito.core.Saito;
import com.marcobehler.saito.core.dagger.PathsModule;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class SaitoApi {

    public static Saito newInstance(Path workingDirectory) {
        SaitoApiComponent apiComponent = DaggerSaitoApiComponent.builder()
                .pathsModule(new PathsModule() {
                    @Override
                    public Path workingDir() {
                        return workingDirectory.toAbsolutePath().normalize();
                    }

                    @Override
                    public Path sourceDir() {
                        return workingDirectory.resolve("source").toAbsolutePath().normalize();
                    }

                    @Override
                    public Path configFile() {
                        return null;
                    }
                }).build();
        return apiComponent.saito();
    }
}
