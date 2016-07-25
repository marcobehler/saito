package com.marcobehler.saito.core.files;

import java.nio.file.Path;

/**
 *
 */
public class BlogPost extends Template {

    public BlogPost(final Path sourceDirectory, final Path relativePath) {
        super(sourceDirectory, relativePath);
    }
}
