package com.marcobehler.saito.core.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class TemplateContentTest {

    @Test
    public void parse_template_will_be_trimmed() {
        TemplateContent templateContent = TemplateContent.of("---test: a\nblubb: true--- this is something else");
        assertThat(templateContent.getText()).isEqualTo("this is something else");
    }

    @Test
    public void parse_template_ignores_missing() {
        TemplateContent templateContent = TemplateContent.of("---test: a\nblubb: true---");
        assertThat(templateContent.getText()).isEqualTo("");
    }
}
