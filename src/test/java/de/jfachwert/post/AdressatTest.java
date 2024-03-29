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
 * (c)reated 18.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Text;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link Adressat}-Klasse.
 *
 * @author oboehm
 */
public final class AdressatTest extends AbstractFachwertTest<String, Text> {

    private final Adressat mustermann = new Adressat("Mustermann, Max");

    /**
     * Zum Testen erstellen wir hierueber ein Test-Adressat.
     *
     * @param code den Code zum Erstellen des Test-Objekts
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Adressat createFachwert(String code) {
        return Adressat.of(code);
    }

    /**
     * Null-Adressat soll nicht erzeugt werden koennen. Normalerweise
     * wuerden wir hier eine IllegalArgumentException erwarten, aber Kotlin 1.4
     * macht hier schone eine Nullpointerexception. Deswegen akzeptieren wir
     * jetzt beides.
     */
    @Test
    public void testAdressatNull() {
        assertThrows(RuntimeException.class, () -> new Adressat(null));
    }

    /**
     * Test-Methode fuer {@link Adressat#of(String)}.
     */
    @Test
    public void testOf() {
        assertEquals(mustermann, Adressat.of(mustermann.toString()));
    }

    /**
     * Test-Methode fuer {@link Adressat#getName()}.
     */
    @Test
    public void testGetName() {
        assertEquals("Mustermann", mustermann.getName());
    }

    /**
     * Test-Methode fuer {@link Adressat#getVorname()}.
     */
    @Test
    public void testGetVorname() {
        assertEquals("Max", mustermann.getVorname());
    }

    /**
     * Firmen haben keinen Vornamen. Von daher sollte das mit einer Exception
     * zurueckgewiesen werden.
     */
    @Test
    public void testGetVornameFromFirma() {
        Adressat ichAG = new Adressat("Ich AG");
        assertThrows(RuntimeException.class, ichAG::getVorname);
    }

}
