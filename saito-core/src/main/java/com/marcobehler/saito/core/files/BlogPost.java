package com.marcobehler.saito.core.files;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlogPost extends Template {

    private static final Pattern BLOG_POST_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})-(.+?)(\\..+)");

    private final String year;
    private final String month;
    private final String day;
    private final String title;

    public BlogPost(final Path sourceDirectory, final Path relativePath) throws BlogPostFormattingException {
        super(sourceDirectory, relativePath);

        String fileName = relativePath.getFileName().toString();
        final Matcher m = BLOG_POST_PATTERN.matcher(fileName);

        if (!m.matches()) {
            throw new BlogPostFormattingException("Blog Post : " + relativePath.toAbsolutePath().toString()
                    + " does not match pattern: {year}-{month}-{day}-{title}.html");
        }
        this.year = m.group(1);
        this.month = m.group(2);
        this.day = m.group(3);
        this.title = m.group(4);
    }

    @Override
    protected boolean shouldProcess() {
        return !getFrontmatter().getCurrentPage().containsKey("published") || getFrontmatter().getCurrentPage().get("published").equals(Boolean.TRUE);
    }

    @Override
    public Path getOutputPath() {
        final Path relativePath = getRelativePath();
        final String asString = relativePath.toString();
        final FileSystem fs = relativePath.getFileSystem();
        final String blogPath = BLOG_POST_PATTERN.matcher(asString).replaceAll("$1/$2/$3/$4$5");
        return fs.getPath(blogPath);
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

    public class BlogPostFormattingException extends Exception {
        public BlogPostFormattingException(final String message) {
            super(message);
        }
    }
}
