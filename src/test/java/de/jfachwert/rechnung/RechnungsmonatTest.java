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
 * (c)reated 12.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.rechnung;

import de.jfachwert.*;
import org.junit.*;

import javax.validation.*;
import java.time.*;

import static org.hamcrest.Matchers.not;
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
     * Hier testen wir den Konstruktur mit Month und Jahr.
     */
    @Test
    public void testRechnungsmonatMonth() {
        assertEquals(FEB_2017, new Rechnungsmonat(Month.FEBRUARY, 2017));
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
     * Testmethode fuer {@link Rechnungsmonat#plusYears(int)} und damit
     * indirekt auch fuer {@link Rechnungsmonat#plusMonths(int)}.
     */
    @Test
    public void testPlus() {
        assertEquals(JAN_2017, JAN_2016.plusYears(1));
    }

    /**
     * Testmethode fuer {@link Rechnungsmonat#minusYears(int)} und
     * fuer {@link Rechnungsmonat#minusYears(int)}.
     */
    @Test
    public void testMinus() {
        assertEquals(JAN_2016, JAN_2017.minusYears(1));
        assertEquals(JAN_2016, JAN_2017.minusMonths(12));
    }

    /**
     * Test-Methode fuer {@link Rechnungsmonat#format(String)}.
     */
    @Test
    public void testFormat() {
        assertEquals("06/2017", new Rechnungsmonat(6, 2017).format("MM/yyyy"));
    }

    /**
     * Test-Methode fuer {@link Rechnungsmonat#ersterTag()}.
     */
    @Test
    public void testErsterTag() {
        assertEquals(LocalDate.of(2017, 2, 1), FEB_2017.ersterTag());
    }

    /**
     * Test-Methode fuer {@link Rechnungsmonat#ersterTag(DayOfWeek)}.
     */
    @Test
    public void testErsterMontag() {
        LocalDate ersterArbeitstag = FEB_2017.ersterTag(DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.MONDAY, ersterArbeitstag.getDayOfWeek());
    }

    /**
     * Test-Methode fuer {@link Rechnungsmonat#ersterArbeitstag()}.
     */
    @Test
    public void testErsterArbeitstag() {
        for (int monat = 1; monat <= 12; monat++) {
            Rechnungsmonat rechnungsmonat = new Rechnungsmonat(monat, 2018);
            LocalDate localDate = rechnungsmonat.ersterArbeitstag();
            assertNotWeekend(localDate);
            assertEquals(monat, localDate.getMonthValue());
        }
    }

    /**
     * Test-Methode fuer {@link Rechnungsmonat#letzterTag()}.
     */
    @Test
    public void testLetzerTag() {
        assertEquals(LocalDate.of(2017, 2, 28), FEB_2017.letzterTag());
    }

    /**
     * Test-Methode fuer {@link Rechnungsmonat#letzterTag(DayOfWeek)}.
     */
    @Test
    public void testLetzterFreitag() {
        LocalDate ersterArbeitstag = FEB_2017.letzterTag(DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.FRIDAY, ersterArbeitstag.getDayOfWeek());
    }

    /**
     * Test-Methode fuer {@link Rechnungsmonat#letzterArbeitstag()}.
     */
    @Test
    public void testLetzterArbeitstag() {
        for (int monat = 1; monat <= 12; monat++) {
            Rechnungsmonat rechnungsmonat = new Rechnungsmonat(monat, 2018);
            LocalDate localDate = rechnungsmonat.letzterArbeitstag();
            assertNotWeekend(localDate);
            assertEquals(monat, localDate.getMonthValue());
        }
    }

    private static void assertNotWeekend(LocalDate localDate) {
        assertThat(localDate.getDayOfWeek(), not(DayOfWeek.SATURDAY));
        assertThat(localDate.getDayOfWeek(), not(DayOfWeek.SUNDAY));
    }

    /**
     * Testmethode fuer {@link Rechnungsmonat#of(int, int)}.
     */
    @Test
    public void testOf() {
        Rechnungsmonat m1 = Rechnungsmonat.of(6, 2018);
        Rechnungsmonat m2 = Rechnungsmonat.of(LocalDate.of(2018, 6, 15));
        Rechnungsmonat m3 = Rechnungsmonat.of("6/2018");
        Rechnungsmonat m4 = Rechnungsmonat.of(Month.JUNE, 2018);
        assertSame(m1, m2);
        assertSame(m2, m3);
        assertSame(m3, m4);
    }

}
