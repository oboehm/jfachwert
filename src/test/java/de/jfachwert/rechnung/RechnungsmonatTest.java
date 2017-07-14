package de.jfachwert.rechnung;/*
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
 * (c)reated 12.07.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.*;
import org.junit.*;

import javax.validation.*;
import java.time.*;

import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link Rechnungsmonat}-Klasse.
 *
 * @author oboehm
 */
public final class RechnungsmonatTest extends AbstractFachwertTest {

    private static final Rechnungsmonat JAN_2016 = new Rechnungsmonat("1/2016");
    private static final Rechnungsmonat DEZ_2016 = new Rechnungsmonat("12/2016");
    private static final Rechnungsmonat JAN_2017 = new Rechnungsmonat("1/2017");
    private static final Rechnungsmonat FEB_2017 = new Rechnungsmonat("2/2017");

    /**
     * Zum Testen nehmen wir den Juli 2017.
     *
     * @return Juli 2017
     */
    @Override
    protected Fachwert createFachwert() {
        return new Rechnungsmonat(7, 2017);
    }

    /**
     * Sollte der Konstruktur mit fehlerhaften Werten aufgerufen werden,
     * erwarten wir eine {@link IllegalArgumentException}.
     */
    @Test(expected = ValidationException.class)
    public void testRechnungsmonatWrongMonth() {
        new Rechnungsmonat(13, 2017);
    }

    /**
     * Hier testen wir den Default-Konstruktor.
     */
    @Test
    public void testRechnungsmonat() {
        Rechnungsmonat aktuellerMonat = new Rechnungsmonat();
        assertEquals(LocalDate.now().getYear(), aktuellerMonat.getJahr());
    }

    /**
     * Schoen waere es, wenn der String-Konstruktor nicht nur "2/2017" als
     * Argument akzeptiert, sondern auch "Feb-2017" oder normale Datumsformate
     * wie "2017-02-14".
     */
    @Test
    public void testRechnungsmonatString() {
        assertEquals(FEB_2017, new Rechnungsmonat("Feb-2017"));
        assertEquals(FEB_2017, new Rechnungsmonat("2017-02-14"));
    }

    /**
     * Testmethode fuer {@link Rechnungsmonat#getVormonat()}.
     */
    @Test
    public void testGetVormonat() {
        assertEquals(JAN_2017, FEB_2017.getVormonat());
        assertEquals(DEZ_2016, JAN_2017.getVormonat());
    }

    /**
     * Testmethode fuer {@link Rechnungsmonat#getFolgemonat()}.
     */
    @Test
    public void testGetFolgemonat() {
        assertEquals(FEB_2017, JAN_2017.getFolgemonat());
        assertEquals(JAN_2017, DEZ_2016.getFolgemonat());
    }

    /**
     * Testmethode fuer {@link Rechnungsmonat#getVorjahr()}.
     */
    @Test
    public void testGetVorjahr() {
        assertEquals(JAN_2016, JAN_2017.getVorjahr());
    }

    /**
     * Testmethode fuer {@link Rechnungsmonat#getFolgejahr()}.
     */
    @Test
    public void testGetFolgejahr() {
        assertEquals(JAN_2017, JAN_2016.getFolgejahr());
    }

    /**
     * Test-Methode fuer {@link Rechnungsmonat#format(String)}.
     */
    @Test
    public void testFormat() {
        assertEquals("06/2017", new Rechnungsmonat(6, 2017).format("MM/yyyy"));
    }

}
