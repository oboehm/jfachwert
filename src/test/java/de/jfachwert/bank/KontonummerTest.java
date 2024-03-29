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
 * (c)reated 16.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwertTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link Kontonummer}-Klasse.
 *
 * @author oboehm
 */
public final class KontonummerTest extends AbstractFachwertTest<Long, Kontonummer> {

    /**
     * Ereugt eine Kontonummer.
     *
     * @param nr Kontonummer
     * @return "0006605605" als Kontonummer
     */
    @Override
    protected Kontonummer createFachwert(String nr) {
        return Kontonummer.of(nr);
    }

    /**
     * Erzeugt einen Code, der zum Erstellen eines Test-Objekts verwendet wird.
     * Die Kontonummer stammt aus dem IBANTest.
     *
     * @return "0006605605"
     */
    @Override
    protected String getCode() {
        return "0006605605";
    }

    /**
     * Eine Kontonnummer darf maximal 10-stellig seiin.
     *
     * @return "12345678900"
     */
    @Override
    protected String getInvalidCode() {
        return "12345678900";
    }

    /**
     * Fuehrende Nullen sollten beim Vergleich keine Rollen spielen.
     */
    @Test
    public void testEqualsWithLeadingZeros() {
        assertEquals(Kontonummer.of("6605605"), Kontonummer.of("0006605605"));
    }

    /**
     * Die Ausgabe sollte mit fuehrenden Nullen erfolgen.
     */
    @Override
    @Test
    public void testToString() {
        assertEquals("0006605605", Kontonummer.of("6605605").toString());
    }

    /**
     * Dieser Test ueberprueft das Fehlerhandling bei fehlerhafter Erzeugung.
     */
    @Test
    public void testKontonummerFailed() {
        assertThrows(IllegalArgumentException.class, () -> new Kontonummer("falsch"));
    }

    @Test
    public void testNegativeKontonummer() {
        assertThrows(IllegalArgumentException.class, () -> new Kontonummer(-1));
    }

}
