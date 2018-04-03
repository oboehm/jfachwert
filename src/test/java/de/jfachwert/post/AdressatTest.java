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

import org.junit.Test;

import javax.validation.ValidationException;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link Adressat}-Klasse.
 *
 * @author oboehm
 */
public final class AdressatTest {

    private final Adressat mustermann = new Adressat("Mustermann, Max");

    /**
     * Null-Adressat soll nicht erzeugt werden koennen.
     */
    @Test(expected = ValidationException.class)
    public void testAdressatNull() {
        new Adressat(null);
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
    @Test(expected = RuntimeException.class)
    public void testGetVornameFromFirma() {
        Adressat ichAG = new Adressat("Ich AG");
        ichAG.getVorname();
    }

}
