package com.marcobehler.saito.core.files;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface FileEventSubscriber {

    void onFileEvent(FileEvent event);
}
