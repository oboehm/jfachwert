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
import de.jfachwert.Text;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer die PLZ-Klasse.
 *
 * @author oboehm
 */
public class PLZTest extends AbstractFachwertTest<String, Text> {

    /**
     * Zum Testen brauchen wird die Postleitzahl von Gerlingen verwendet.
     *
     * @param plz PLZ gueltige PLZ
     * @return PLZ von Gerlingen
     */
    @Override
    protected PLZ createFachwert(String plz) {
        return PLZ.of(plz);
    }

    /**
     * Zum Testen brauchen wird die Postleitzahl von Gerlingen verwendet.
     *
     * @return "D-70839"
     */
    @Override
    protected String getCode() {
        return super.getCode();
    }

    /**
     * Postleitzahlen in Oesterreich sind 4-stellig. D.h. eine 5-stellige
     * oesterreichische PLZ sollte nicht erstellt werden koennen.
     */
    @Test
    public void testInvalidPLZausOesterreich() {
        assertThrows(IllegalArgumentException.class, () -> new PLZ(new Locale("de", "AT"), "12345"));
    }

    /**
     * In der DACH-Region bestehen die Postleitzahlen nur aus Ziffern.
     */
    @Test
    public void testInvalidPostleitZahl() {
        assertThrows(IllegalArgumentException.class, () -> new PLZ("CH-0x23"));
    }

    /**
     * Weltweit sind die Postleitzahlen zwischen 3 und 10 Zeichen lang.
     * Quelle: https://de.wikipedia.org/wiki/Postleitzahl
     */
    @Test
    public void testInvalidPLZ() {
        assertThrows(IllegalArgumentException.class, () -> new PLZ("12"));
    }

    /**
     * Test-Methode fuer {@link PLZ#getLandeskennung()}.
     */
    @Test
    public void testGetLandeskennung() {
        PLZ dresden = new PLZ("D-99998");
        assertEquals("D", dresden.getLandeskennung());
    }

    /**
     * Es gibt verschiedene Moeglichkeiten, den Prefix vor der eigentlichen
     * PLZ zu schreiben. Allerdings sollte es dennoch die gleiche PLZ sein.
     */
    @Test
    public void testEqualsForDE() {
        PLZ one = new PLZ("D01067");
        PLZ anotherOne = new PLZ("d-01067");
        assertEquals(one, anotherOne);
    }

    /**
     * Aus Lesbarkeitgruenden sollte zwischen Landeskennung und PLZ ein
     * Trennzeichen sein.
     */
    @Test
    public void testToString() {
        PLZ dresden = new PLZ("D01069");
        assertEquals("D-01069", dresden.toString());
    }

    /**
     * Normale Postleitzahlen sollten in kompakter Schreibweise ausgegeben
     * werden.
     */
    @Test
    public void testToStringOhneLandeskennung() {
        assertEquals("70178", new PLZ("70 178").toString());
    }

    /**
     * Test-Methode fuer {@link PLZ#getLand()}.
     */
    @Test
    public void testGetLand() {
        PLZ stuttgart = new PLZ("D-70435");
        assertEquals(new Locale("de", "DE"), stuttgart.getLand());
    }

    /**
     * Test-Methode fuer {@link PLZ#getLand()}.
     */
    @Test
    public void testGetLandCH() {
        PLZ vaduz = new PLZ(new Locale("de", "CH"), "9490");
        assertEquals("CH", vaduz.getLand().getCountry());
    }

    /**
     * Test-Methode fuer {@link PLZ#getLand()}.
     */
    @Test
    public void testGetLandAT() {
        PLZ weyer = new PLZ("A-3335");
        assertEquals(new Locale("de", "AT"), weyer.getLand());
    }

    /**
     * Fuenfstelligen Postleitzahlen kommen aus Deutschland. Dort duerfen
     * Postleitzahlen nicht mit '00...' anfangen.
     */
    @Test
    public void testFuenfstellig() {
        assertThrows(IllegalArgumentException.class, () -> PLZ.of("00123"));
    }

    /**
     * Postleitzahlen duerfen in Deutschland nicht mit '00...' anfangen.
     */
    @Test
    public void testFuenfstelligDE() {
        assertThrows(IllegalArgumentException.class, () -> PLZ.of("D-00999"));
    }

    /**
     * Nach <a href="http://api.zippopotam.us/">Zippotam</a> ist 99999 keine
     * gueltige PLZ in Deutschland.
     */
    @Test
    public void testD99999() {
        assertThrows(IllegalArgumentException.class, () -> PLZ.of("D-99999"));
    }

}
