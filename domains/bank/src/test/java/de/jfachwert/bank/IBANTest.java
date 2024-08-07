/*
 * Copyright (c) 2017-2024 by Oliver Boehm
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
 * (c)reated 10.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.pruefung.exception.InvalidLengthException;
import org.junit.jupiter.api.Test;

import de.jfachwert.pruefung.exception.ValidationException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Test fuer {@link IBAN}-Klasse.
 *
 * @author oboehm
 */
public final class IBANTest extends AbstractFachwertTest<String, IBAN> {

    private final IBAN iban = new IBAN("DE41300606010006605605");

    /**
     * Hierueber stellen wir fuer die Oberklasse eine Test-IBAN zur Verfuegung.
     *
     * @param code die IBAN zum Erstellen des Test-Objekts
     * @return Test-IBAN
     */
    protected IBAN createFachwert(String code) {
        return IBAN.of(code);
    }

    /**
     * Liefert eine Test-IBAN als String.
     *
     * @return "DE41300606010006605605"
     */
    @Override
    protected String getCode() {
        return "DE41300606010006605605";
    }

    /**
     * Ungueltige IBANs sollten nicht erzeugt werden koennen.
     */
    @Test
    public void testIbanInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new IBAN("DE99300606010006605605"));
    }

    /**
     * Leere IBANs sind ebenfalls ungueltige IBANs.
     */
    @Test
    public void testValidate() {
        assertThrows(ValidationException.class, () -> new IBAN.Validator().validate("                                  "));
    }

    /**
     * Eine leere IBAN sollte nicht moeglich sein.
     */
    @Test
    public void testIbanEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new IBAN(""));
    }

    /**
     * Test method for {@link IBAN#getFormatted()}.
     */
    @Test
    public void testGetFormatted() {
        assertEquals("DE41 3006 0601 0006 6056 05", iban.getFormatted());
    }

    /**
     * Test method for {@link IBAN#getFormatted()}.
     */
    @Test
    public void testGetFormattedFormatted() {
        assertEquals("DE41 3006 0601 0006 6056 05", new IBAN("DE41 3006 0601 0006 6056 05").getFormatted());
    }

    /**
     * Test method for {@link IBAN#getFormatted()}.
     */
    @Test
    public void testGetFormattedUppercase() {
        assertEquals("DE41 3006 0601 0006 6056 05", new IBAN("de41300606010006605605").getFormatted());
    }

    /**
     * Test-Methode fuer {@link IBAN#getLand()}-
     */
    @Test
    public void testGetLand() {
        assertEquals(new Locale("de", "DE"), iban.getLand());
    }

    /**
     * Test-Methode fuer {@link IBAN#getPruefziffer()}.
     */
    @Test
    public void testGetPruefziffer() {
        assertEquals("41", iban.getPruefziffer());
    }

    /**
     * Test-Methode fuer {@link IBAN#getBLZ()}.
     */
    @Test
    public void testGetBLZ() {
        assertEquals(new BLZ("30060601"), iban.getBLZ());
    }

    /**
     * Test-Methode fuer {@link IBAN#getKontonummer()}.
     */
    @Test
    public void testGetKontonummer() {
        assertEquals(new Kontonummer("0006605605"), iban.getKontonummer());
    }

    /**
     * Beispiel stammt aus <a href="https://ibanvalidieren.de/beispiele.html"
     * >beispiele</a>.
     */
    @Test
    public void testIbanAT() {
        IBAN at = IBAN.of("AT026000000001349870");
        assertNotNull(at);
    }

    /**
     * Beispiel stammt aus <a href="https://ibanvalidieren.de/beispiele.html"
     * >beispiele</a>.
     */
    @Test
    public void testIbanCH() {
        IBAN ch = IBAN.of("CH0209000000100013997");
        assertNotNull(ch);
    }

    /**
     * Eine deutsche IBAN ist genau 22 Zeichen lang (und nicht 21).
     */
    @Test
    public void validateInvalidLength() {
        IBAN.Validator v = new IBAN.Validator();
        assertThrows(InvalidLengthException.class, () -> v.validate("DE196000000001349870"));
    }

}
