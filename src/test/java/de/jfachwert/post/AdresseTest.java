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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.FachwertTest;
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

/**
 * Unit-Teests fuer de.jfachwert.post.Adresse.
 *
 * @author oboehm
 * @since 0.2 (03.05.2017)
 */
public final class AdresseTest extends FachwertTest {

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
     * Eine Adresse besteht mindestens aus Ort und Strasse.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAdresseInvalid() {
        new Adresse("Nirwana");
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

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAdresse() {
        Adresse.of(entenhausen, "", "");
    }

    /**
     * Hier testen wir, ob der Konstruktor die uebergebene Adresse richtig
     * zerlegt.
     */
    @Test
    public void testAdresse() {
        String musterdorf = "Alter Weg 110 a\n12345 Musterdorf";
        Adresse adresse = Adresse.of(musterdorf);
        assertEquals("Alter Weg", adresse.getStrasse());
        assertEquals("110 a", adresse.getHausnummer());
        assertEquals(adresse, Adresse.of(adresse.getOrt(), adresse.getStrasse(), adresse.getHausnummer()));
    }

    /**
     * Es soll auch Adressen ohne Hausnummer geben.
     */
    @Test
    public void testAdresseOhneHausnummer() {
        Adresse adr = Adresse.of("12345 Nirwana, am Ende der Welt");
        assertEquals("am Ende der Welt", adr.getStrasse());
    }

    @Test
    public void testAdresseKurz() {
        Adresse adresse = Adresse.of("11111 K., Str.2-4");
        assertEquals("Str.", adresse.getStrasse());
        assertEquals("2-4", adresse.getHausnummer());
    }

    @Test
    public void testGetStrasseKurz() {
        Adresse hauptstrasse = Adresse.of(entenhausen, "Hauptstrasse", 1);
        assertEquals("Hauptstr.", hauptstrasse.getStrasseKurz());
    }

    /**
     * Fuer den Vergleich sollte es keine Rolle spielen, ob die Strasse
     * ausgeschrieben ("Badstrasse") oder abgekuerzt ("Badstr.") wird.
     */
    @Test
    public void testEqualsStrasse() {
        Ort ort = Ort.of("12345 Monopoly");
        Adresse badstr = Adresse.of(ort, "Badstr.", 1);
        Adresse badstrasse = Adresse.of(ort, "Badstrasse", 1);
        Adresse badstrasze = Adresse.of(ort, "Badstra\u00dfe", 1);
        ObjectTester.assertEquals(badstr, badstrasse);
        ObjectTester.assertEquals(badstr, badstrasze);
        ObjectTester.assertEquals(badstrasse, badstrasze);
    }

    /**
     * Beim Vergleich sollen unterschiedliche Schreibweisen (mit oder ohne)
     * Bindestrich ignoriert werden.
     */
    @Test
    public void testEqualsStrasseMitBindestrich() {
        Adresse mitBindestrich = Adresse.of(entenhausen, "Nord-West-Str.", 7);
        Adresse ohneBindestrich = Adresse.of(entenhausen, "Nordwest Str.", 7);
        ObjectTester.assertEquals(mitBindestrich, ohneBindestrich);
    }

    @Test
    public void testEqualsUmlaute() {
        Adresse mitUmlaute = Adresse.of(entenhausen, "H\u00fchnerg\u00e4\u00dfle", 2);
        Adresse ohneUmlaute = Adresse.of(entenhausen, "huehnergaessle", 2);
        ObjectTester.assertEquals(ohneUmlaute, mitUmlaute);
    }

    @Test
    public void testEqualsExact() {
        Adresse a = Adresse.of("12345 Monopoly, Badstrasse 1");
        Adresse b = Adresse.of("12345 Monopoly, Badstrasse 1");
        Adresse kurz = Adresse.of("12345 M., Badstr. 1");
        assertEquals(a, b);
        assertEquals(a, kurz);
        assertTrue(a.equalsExact(b));
        assertFalse(a.equalsExact(kurz));
    }

    @Test
    public void testToShortString() {
        Adresse adresse = Adresse.of(entenhausen, "Hauptstrasse", 1);
        String shortString = adresse.toShortString();
        assertThat(shortString, shortString.length(), lessThan(adresse.toString().length()));
    }

    @Test
    public void testNotEquals() {
        Adresse eins = Adresse.of(entenhausen, "Duckstr.", 1);
        Adresse zwei = Adresse.of(entenhausen, "Druckstr.", 1);
        assertNotEquals(eins, zwei);
    }

    /**
     * Leerzeichen sollten beim Vergleich der Hausnummern keine Rolle spielen.
     */
    @Test
    public void testEqualsHausnummer() {
        compareHausnummer("2 - 4", "2-4");
    }

    /**
     * Hausnummern mit oder ohne zusaetzliche Angaben sollen als gleich
     * angesehen werden.
     */
    @Test
    public void testEqualsHausnummerEinsA() {
        compareHausnummer("1", "1a");
    }

    @Test
    public void testEqualsHausnummerVon() {
        compareHausnummer("7-9", "7");
    }

    @Test
    public void testEqualsHausnummerBis() {
        compareHausnummer("7-9", "9");
    }

    private void compareHausnummer(String n1, String n2) {
        Adresse duckstr = Adresse.of(entenhausen, "Duckstr.", n1);
        Adresse einsa = Adresse.of(entenhausen, "Duckstr.", n2);
        ObjectTester.assertEquals(duckstr, einsa);
    }

    @Test
    public void testDifferentHausnummern() {
        Adresse eins = Adresse.of(entenhausen, "Duckstr.", 1);
        Adresse zwei = Adresse.of(entenhausen, "Duckstr.", 2);
        assertNotEquals(eins, zwei);
    }

    @Test
    public void testOf() {
        Ort ort = Ort.of("23456 Monopoly");
        Adresse adresse = Adresse.of(ort, "Badstr.2");
        assertEquals(ort, adresse.getOrt());
        assertEquals("Badstr.", adresse.getStrasse());
        assertEquals("2", adresse.getHausnummer());
    }
    
}
