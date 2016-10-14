package com.marcobehler.saito.api;

import com.marcobehler.saito.core.Saito;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class SaitoApiTest {

    @Test
    public void newInstance() {
        Saito saito = SaitoApi.newInstance(Paths.get("c:\\Users\\marco\\Code\\Progenica\\progenica-website\\src\\site\\"));
    saito.build();
        assertNotNull(saito);
    }
}
