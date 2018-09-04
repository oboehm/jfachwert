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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Test fuer {@link BLZ}-Klasse.
 *
 * @author oboehm
 */
public final class BLZTest extends AbstractFachwertTest {

    /**
     * Zum Testen verwenden wir die Volksbank Reutlingen, die auch in
     * Wikipedia erwaehnt ist.
     *
     * @return BLZ von Volksbank Reutlingen
     */
    @Override
    protected BLZ createFachwert() {
        return new BLZ("64090100");
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
        assertEquals(new BLZ("64090100"), new BLZ("640 901 00"));
    }

    /**
     * Test-Methode fuer {@link BLZ#getUnformatted()} und
     * {@link BLZ#getFormatted()}.
     */
    @Test
    public void testGetFormatted() {
        BLZ blz = new BLZ(64090100);
        assertEquals("64090100", blz.getUnformatted());
        assertEquals("640 901 00", blz.getFormatted());
    }

    /**
     * Test-Methode fuer {@link BLZ#validate(int)}.
     */
    @Test
    public void testValidate() {
        BLZ.validate(64090100);
    }

}
