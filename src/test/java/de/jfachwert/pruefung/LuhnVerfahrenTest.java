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
 * (c)reated 11.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import de.jfachwert.PruefzifferVerfahren;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link LuhnVerfahren}-Klasse. Die Ergebnisse wurden dabei
 * mit http://www.ee.unb.ca/cgi-bin/tervo/luhn.pl ueberprueft.
 *
 * @author oboehm
 */
public final class LuhnVerfahrenTest extends AbstractPruefzifferVerfahrenTest<String> {

    private static final PruefzifferVerfahren<String> MOD10 = new LuhnVerfahren();

    /**
     * Hierueber wird das Pruefziffer-Verfahren fuer den Test erwartet.
     *
     * @return Pruefzifferverfahren zum Testen
     */
    protected PruefzifferVerfahren<String> getPruefzifferVerfahren() {
        return MOD10;
    }

    /**
     * Zum Testen des Pruefzifferverfahrens brauchen wir einen Wert, der
     * gueltig sein sollte.
     *
     * @return ein gueltiger Wert
     */
    @Override
    protected String getValidWert() {
        return "260326822";
    }

    @Test
    public void testIsInvalid() {
        assertFalse(MOD10.isValid("500"));
    }

    @Test
    public void testPruefzifferOnly() {
        assertFalse(MOD10.isValid("5"));
    }

    /**
     * Dieser Testfall stammt aus der Pruefung der KassenkIK mit der IK-Nummer
     * "108018132" (AOK Baden Wuerttemberg).
     */
    @Test
    public void testGetPruefziffer() {
        String nummer = "8018132";
        assertEquals("2", MOD10.getPruefziffer(nummer));
        assertTrue(MOD10.isValid(nummer));
    }

    /**
     * Das Beispiel fuer Versicherungsnummer stammt aus <a href=
     * "https://de.wikipedia.org/wiki/Krankenversichertennummer"
     * >Wikipedia</a>.
     */
    @Test
    public void testVersicherungsnummer() {
        String versNr = "A123456780";
        assertEquals("0", MOD10.getPruefziffer(versNr));
        assertTrue(MOD10.isValid(versNr));
    }

    @Test
    public void testOtherVersicherungsnummer() {
        String versNr = "X234567891";
        assertTrue(MOD10.isValid(versNr));
    }

}
