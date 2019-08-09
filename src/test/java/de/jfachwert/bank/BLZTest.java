/*
 * Copyright (c) 2017 by Oliver Boehm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 16.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.math.PackedDecimal;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Test fuer {@link BLZ}-Klasse.
 *
 * @author oboehm
 */
public final class BLZTest extends AbstractFachwertTest<PackedDecimal, BLZ> {

    /**
     * Erzeugt eine BLZ mit dem uebergebenen Code.
     *
     * @param code zum Testen
     * @return BLZ
     */
    @Override
    protected BLZ createFachwert(String code) {
        return BLZ.of(code);
    }

    /**
     * Zum Testen verwenden wir die Volksbank Reutlingen, die auch in
     * Wikipedia erwaehnt ist.
     *
     * @return "64090100"
     */
    @Override
    protected String getCode() {
        return "64090100";
    }

    /**
     * Bei Bankleitzahlen sind nur Ziffern erlaubt.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBLZ() {
        new BLZ("0x10");
    }

    /**
     * Bankleitzahlen koennen Leerzeichen enthalten, die beim Vergleich keine
     * Rolle spielen duerfen.
     */
    @Test
    public void testEqualsFormatted() {
        assertEquals(BLZ.of("64090100"), BLZ.of("640 901 00"));
    }

    /**
     * Test-Methode fuer {@link BLZ#getUnformatted()} und
     * {@link BLZ#getFormatted()}.
     */
    @Test
    public void testGetFormatted() {
        BLZ blz = BLZ.of(64090100);
        assertEquals("64090100", blz.getUnformatted());
        assertEquals("640 901 00", blz.getFormatted());
    }

}
