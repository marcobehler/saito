package com.marcobehler.saito.core.utils;

import java.nio.file.Paths;

import org.junit.Test;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.rendering.Model;
import com.marcobehler.saito.core.util.LinkHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class LinkHelperTest {

    @Test
    public void favIcon_jpg() {
        final String link = new LinkHelper(mock(SaitoConfig.class), new Model(), Paths.get(".")).favicon("test.jpg");
        assertThat(link).isEqualTo("<link rel=\"icon\" type=\"image/jpeg\" href=\"/images/test.jpg\"/>");
    }

    @Test
    public void favIcon_jpeg() {
        final String link = new LinkHelper(mock(SaitoConfig.class), new Model(), Paths.get(".")).favicon("test.jpeg");
        assertThat(link).isEqualTo("<link rel=\"icon\" type=\"image/jpeg\" href=\"/images/test.jpeg\"/>");
    }


    @Test
    public void favIcon_correct_mimeType() {
        final String link = new LinkHelper(mock(SaitoConfig.class), new Model(), Paths.get(".")).favicon("test.png");
        assertThat(link).isEqualTo("<link rel=\"icon\" type=\"image/png\" href=\"/images/test.png\"/>");
    }



    @Test
    public void img_tag() {
        final String link = new LinkHelper(mock(SaitoConfig.class), new Model(), Paths.get(".")).imageTag("myImage.png", null, null, null, null, null);
        assertThat(link).isEqualTo("<img src=\"/images/myImage.png\" />");
    }

    @Test
    public void img_tag_with_height() {
        final String link = new LinkHelper(mock(SaitoConfig.class), new Model(), Paths.get(".")).imageTag("myImage.png", null, 30, null, null, null);
        assertThat(link).isEqualTo("<img src=\"/images/myImage.png\" height=\"30\" />");
    }


    @Test
    public void img_tag_with_width() {
        final String link = new LinkHelper(mock(SaitoConfig.class), new Model(), Paths.get(".")).imageTag("myImage.png", 25, null, null, null, null);
        assertThat(link).isEqualTo("<img src=\"/images/myImage.png\" width=\"25\" />");
    }

    @Test
    public void img_tag_all_options() {
        final String link = new LinkHelper(mock(SaitoConfig.class), new Model(), Paths.get(".")).imageTag("myImage.png", 32, 64, "huhuCss", "customData", "customAlt");
        assertThat(link).isEqualTo("<img src=\"/images/myImage.png\" width=\"32\" height=\"64\" class=\"huhuCss\" data-title=\"customData\" alt=\"customAlt\" />");
    }
}
