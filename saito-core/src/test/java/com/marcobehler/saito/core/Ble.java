package com.marcobehler.saito.core;

import org.junit.Test;


/**
 *
 */
public class Ble {

    @Test
    public void ble() {
        final Saito$$ saito$$ = DaggerSaito$$.builder().build();
        final Saito saito = saito$$.saito();
        final Saito saito2 = saito$$.saito();
        final Saito saito3 = saito$$.saito();
        System.out.println(saito);
        System.out.println("=====================> jaaaaa" + saito);
    }

}