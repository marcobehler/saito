package com.marcobehler.saito.core.assets;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by marco on 17.09.2016.
 */
public class YuiWrapperTest {

    @Test
    public void compressJavascript() {
        byte[] js = new YuiWrapper().compressJavaScript(("function jo() {\n" +
                "    return 'hallo';\n" +
                "}").getBytes());

        assertThat(new String(js)).isEqualTo("function jo(){return\"hallo\"};");
    }

    @Test
    public void comprssCss() {
        byte[] css = new YuiWrapper().compressCSS(".color \n{ \ncolor: \nred;}".getBytes());

        assertThat(new String(css)).isEqualTo(".color{color:red}");
    }
}
