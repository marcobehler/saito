package com.marcobehler.saito.core.utils;

import java.nio.file.Paths;

import org.junit.Test;

import com.marcobehler.saito.core.configuration.SaitoConfig;
import com.marcobehler.saito.core.rendering.RenderingModel;
import com.marcobehler.saito.core.util.LinkHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class LinkHelperTest {

    @Test
    public void favIcon_jpg() {
        final String link = new LinkHelper(new RenderingModel(mock(SaitoConfig.class)), Paths.get(".")).favicon("test.jpg");
        assertThat(link).isEqualTo("<link rel=\"icon\" type=\"image/jpeg\" href=\"/images/test.jpg\"/>");
    }

    @Test
    public void favIcon_jpeg() {
        final String link = new LinkHelper(new RenderingModel(mock(SaitoConfig.class)), Paths.get(".")).favicon("test.jpeg");
        assertThat(link).isEqualTo("<link rel=\"icon\" type=\"image/jpeg\" href=\"/images/test.jpeg\"/>");
    }


    @Test
    public void favIcon_correct_mimeType() {
        final String link = new LinkHelper(new RenderingModel(mock(SaitoConfig.class)), Paths.get(".")).favicon("test.png");
        assertThat(link).isEqualTo("<link rel=\"icon\" type=\"image/png\" href=\"/images/test.png\"/>");
    }
}
