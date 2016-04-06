package com.marcobehler.saito.core.events;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public interface FileEventSubscriber {

    void onFileEvent(FileEvent event);
}
