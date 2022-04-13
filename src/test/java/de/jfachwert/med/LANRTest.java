/*
 * Copyright (c) 2018 by Oliver Boehm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 12.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link LANR}-Klasse.
 *
 * @author oboehm
 */
public class LANRTest extends AbstractFachwertTest<Integer, LANR> {

    /**
     * Zum Testen verwenden wir die Pseudo-Nummer fuer Bundeswehraerzte,
     * Zahnaerzte und Hebammen.
     *
     * @param nr LA-Nummer
     * @return Test-Objekt zum Testen
     */
    @Override
    protected LANR createFachwert(String nr) {
        return LANR.of(nr);
    }

    /**
     * Erzeugt eine Code, der zum Erstellen eines Test-Objekts verwendet wird.
     *
     * @return "999999900"
     */
    @Override
    protected String getCode() {
        return LANR.PSEUDO_NUMMER.toString();
    }

    @Test
    public void testLANRmit0() {
        LANR nr = LANR.of(6789);
        assertEquals(67, nr.getArztnummer());
    }

    @Test
    public void test10stelligeLANR() {
        assertThrows(IllegalArgumentException.class, () -> LANR.of(1234567890));
    }

    @Test
    public void testGetArztnummer() {
        assertEquals(1234567, LANR.of(123456789).getArztnummer());
    }

    @Test
    public void testGetPruefziffer() {
        assertEquals(7, LANR.of(123456789).getPruefziffer());
    }

    @Test
    public void testIsValid() {
        assertFalse(LANR.of(345678975).isValid());
    }

    @Test
    public void testGetFachgruppe() {
        assertEquals(21, LANR.of(987654321).getFachgruppe());
    }

    @Test
    public void testToString000() {
        String nr = "000456789";
        assertEquals(nr, LANR.of(nr).toString());
    }

    @Test
    public void testIsPseudoNummer() {
        assertTrue(LANR.PSEUDO_NUMMER.isPseudoNummer());
    }

    @Test
    public void testIsNotPseudoNummer() {
        assertFalse(LANR.of(345678975).isPseudoNummer());
    }

    @Test
    public void testIsPseudoNummer999999999() {
        assertTrue(LANR.of("999999999").isPseudoNummer());
    }

    @Test
    public void testIsPseudoNummer3333333() {
        assertTrue(LANR.of("333333321").isPseudoNummer());
    }

    @Test
    public void testIsPseudoNummer4444444() {
        assertTrue(LANR.of("444444401").isPseudoNummer());
    }

    @Test
    public void testIsPseudoNummer555555() {
        assertTrue(LANR.of("555555102").isPseudoNummer());
    }

}
