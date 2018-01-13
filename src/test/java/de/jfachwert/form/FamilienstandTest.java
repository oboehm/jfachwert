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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 28.11.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.form;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link Familienstand}-Klasse.
 *
 * @author oboehm
 */
public final class FamilienstandTest {

    /**
     * Testmethode fuer {@link Familienstand#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("ledig", Familienstand.LEDIG.toString());
        assertEquals("durch Todeserkl\u00e4rung aufgel\u00f6ste Lebenspartnerschaft",
                Familienstand.DURCH_TODESERKLAERUNG_AUFGELOESTE_LEBENSPARTNERSCHAFT.toString());
    }

    /**
     * Der Schluessel fuer den Familienstand im Meldewesen ist zweistellig
     * und grossgeschrieben.
     */
    @Test
    public void testGetSchluessel() {
        for (Familienstand familienstand : Familienstand.values()) {
            String schluessel = familienstand.getSchluessel();
            assertEquals(2, schluessel.length());
            assertEquals(schluessel.toUpperCase(), schluessel);
        }
    }

    /**
     * Test-Methode fuer {@link Familienstand#of(String)}.
     */
    @Test
    public void testOfSchluessel() {
        assertEquals(Familienstand.GESCHIEDEN, Familienstand.of("GS"));
    }

    /**
     * Wenn nicht ein Kuerzel, sondern ein Begriff uebergeben wird, soll bei
     * {@link Familienstand#of(String)} der am passenste Wert zuruecgegeben
     * werden.
     */
    @Test
    public void testOfBegriff() {
        assertEquals(Familienstand.LEDIG, Familienstand.of("ledig"));
        assertEquals(Familienstand.VERHEIRATET, Familienstand.of("verheiratet"));
        assertEquals(Familienstand.GESCHIEDEN, Familienstand.of("geschieden"));
        assertEquals(Familienstand.VERWITWET, Familienstand.of("verwitwet"));
        assertEquals(Familienstand.AUFGEHOBENE_LEBENSPARTNERSCHAFT, Familienstand.of("getrennt lebend"));
        assertEquals(Familienstand.EHEAEHNLICHE_GEMEINSCHAFT, Familienstand.of("ehe\u00e4hnliche Gemeinschaft"));
        assertEquals(Familienstand.EINGETRAGENE_LEBENSPARTNERSCHAFT,
                Familienstand.of("eingetragene Lebenspartnerschaft"));
    }

}
