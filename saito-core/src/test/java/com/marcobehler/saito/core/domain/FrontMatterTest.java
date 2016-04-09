package com.marcobehler.saito.core.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class FrontMatterTest {

    @Test
    public void parse_frontMatter() {
        FrontMatter frontMatter = FrontMatter.parse("---test: a\nblubb: true--- this is something else");
        assertThat(frontMatter.get("blubb")).isEqualTo(true);
        assertThat(frontMatter.get("test")).isEqualTo("a");
    }

    @Test
    public void parse_frontMatter_ignores_whitespace() {
        FrontMatter frontMatter = FrontMatter.parse("     ---        test: a\nblubb: true      ---     this is something else");
        assertThat(frontMatter.get("blubb")).isEqualTo(true);
        assertThat(frontMatter.get("test")).isEqualTo("a");
    }

    @Test
    public void parse_frontMatter_ignores_empty_frontmatter() {
        FrontMatter frontMatter = FrontMatter.parse("     ---\n---     this is something else");
        assertThat(frontMatter.size()).isEqualTo(0);
    }

    @Test
    public void parse_frontMatter_ignores_missing_frontmatter() {
        FrontMatter frontMatter = FrontMatter.parse("    this is something else");
        assertThat(frontMatter.size()).isEqualTo(0);
    }
}
