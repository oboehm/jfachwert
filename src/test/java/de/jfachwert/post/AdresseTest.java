package de.jfachwert.post;/*
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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Teests fuer de.jfachwert.post.Adresse.
 *
 * @author oboehm
 * @since 0.2 (03.05.2017)
 */
public final class AdresseTest extends AbstractFachwertTest {

    private final Ort entenhausen = new Ort(new PLZ("12345"), "Entenhausen");
    private final Adresse adresse = new Adresse(entenhausen,"Duckgasse", "1a");

    /**
     * Zum Testen genererien wir eine einfache Adresse.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Adresse createFachwert() {
        return new Adresse(entenhausen,"Blumenweg", "20");
    }

    /**
     * Test-Methode fuer {@link Adresse#getOrt()}.
     */
    @Test
    public void testGetOrt() {
        assertEquals(entenhausen, adresse.getOrt());
    }

    /**
     * Test-Methode fuer {@link Adresse#getPLZ()}.
     */
    @Test
    public void testGetPLZ() {
        assertEquals(entenhausen.getPLZ().get(), adresse.getPLZ());
    }

    /**
     * Test-Methode fuer {@link Adresse#validate(String)}.
     */
    @Test
    public void testValidateOrtStrasse() {
        Adresse.validate("D12345 Entenhausen, Gansstr. 23");
    }

    /**
     * Die verwendete Adresse stammt von der Uni Erfurt und ist eine gueltige
     * Adresse.
     */
    @Test
    public void testValidateStrasseOrt() {
        String adresse = "Nordh\u00e4user Stra\u00dfe 63\n99089 Erfurt";
        Adresse.validate(adresse);
    }

    /**
     * Hier testen wir, ob der Konstruktor die uebergebene Adresse richtig
     * zerlegt.
     */
    @Test
    public void testAdresse() {
        String musterdorf = "Alter Weg 110 a\n12345 Musterdorf";
        Adresse adresse = new Adresse(musterdorf);
        assertEquals("Alter Weg", adresse.getStrasse());
        assertEquals("110 a", adresse.getHausnummer());
    }
    
}
