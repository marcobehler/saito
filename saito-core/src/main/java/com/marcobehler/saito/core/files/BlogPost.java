package com.marcobehler.saito.core.files;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlogPost extends Template {

    public static final Pattern BLOG_POST_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})-(.+?)(\\..+)");

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
    public boolean shouldProcess() {
        return !getFrontmatter().getCurrentPage().containsKey("published") || getFrontmatter().getCurrentPage().get("published").equals(Boolean.TRUE);
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
