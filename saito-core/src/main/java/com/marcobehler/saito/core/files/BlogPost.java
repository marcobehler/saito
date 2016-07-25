package com.marcobehler.saito.core.files;

import java.nio.file.Path;

import sun.plugin.dom.exception.InvalidStateException;

/**
 * // {year}-{month}-{day}-{title}.html
 */
public class BlogPost extends Template {

    private final String year;
    private final String month;
    private final String day;
    private final String title;

    public BlogPost(final Path sourceDirectory, final Path relativePath) {
        super(sourceDirectory, relativePath);

        String fileName = relativePath.getFileName().toString();
        fileName = fileName.substring(fileName.indexOf("."));

        final String[] splitFileName = fileName.split("-");
        if (splitFileName.length != 4) {
            throw new InvalidStateException("Not a blog post filename");
        }

        this.year = splitFileName[0];
        this.month = splitFileName[1];
        this.day = splitFileName[2];
        this.title = splitFileName[3];
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getTitle() {
        return title;
    }
}
