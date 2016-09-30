package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.files.DataFile;
import com.marcobehler.saito.core.files.Other;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.files.Template;
import com.marcobehler.saito.core.plugins.TemplatePostProcessor;
import com.marcobehler.saito.core.rendering.Renderer;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

import java.util.Set;

/**
 * Created by BEHLEMA on 28.09.2016.
 */
@Module
public class ProcessingModule {

    @Provides
    @IntoMap
    @ProcessorClassKey(DataFile.class)
    static Processor<? extends SaitoFile> dataFileProcessor() {
        return new DataFileProcessor();
    }


    @Provides
    @IntoMap
    @ProcessorClassKey(Other.class)
    static Processor<? extends SaitoFile> otherFileProcessor(SaitoConfig saitoConfig, TargetPathFinder targetPathFinder) {
        return new OtherFileProcessor(saitoConfig, targetPathFinder);
    }

    @Provides
    @IntoMap
    @ProcessorClassKey(Template.class)
    static Processor<? extends SaitoFile> templateProcessor(TargetPathFinder targetPathFinder, Set<Renderer> renderers, Set<TemplatePostProcessor> templatePostProcessors) {
        return new TemplateProcessor(targetPathFinder, renderers, templatePostProcessors);
    }

}
