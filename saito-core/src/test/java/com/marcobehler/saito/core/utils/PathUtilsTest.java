package com.marcobehler.saito.core.utils;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class PathUtilsTest {

    @Test
    public void ble() {

        Path a = Paths.get("/build/mydir/myFile/moho/index.html");
        Path b = Paths.get("/build/assets/javascript/jquery.js");

        System.out.println(a.relativize(b));
    }

}
