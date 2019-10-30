/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 10.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank;

import de.jfachwert.FachwertTest;
import de.jfachwert.math.Prozent;
import org.junit.Test;

import java.time.Period;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

/**
 * Unit-Test fuer {@link Zinssatz}-Klasse.
 *
 * @author oboehm
 */
public final class ZinssatzTest extends FachwertTest {

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden. Und zwar muss jedesmal
     * der gleiche Fachwert erzeugt werden, weil sonst der equals-Test nicht
     * funktioniert.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Zinssatz createFachwert() {
        return new Zinssatz(Prozent.ONE);
    }

    @Test
    public void testGetProzent() {
        Zinssatz zinssatz = new Zinssatz(Prozent.TEN);
        assertEquals(Prozent.TEN, zinssatz.getProzent());
    }

    @Test
    @Override
    public void testToString() {
        Prozent p = Prozent.TEN;
        assertEquals(p.toString(), new Zinssatz(p).toString());
    }

    /**
     * Die statische of-Methode sollte keine Duplikate zurueckliefern.
     */
    @Test
    public void testOf() {
        assertSame(Zinssatz.of(Prozent.ZERO), Zinssatz.of(Prozent.ZERO));
    }

    @Test
    public void testCompareTo() {
        assertThat(Zinssatz.TEN.compareTo(Zinssatz.ONE), greaterThan(0));
        assertThat(Zinssatz.ONE.compareTo(Zinssatz.TEN), lessThan(0));
    }

    @Test
    public void testGetJahrezins() {
        assertEquals(Geldbetrag.of(60), Zinssatz.of("3%").getJahresszins(Geldbetrag.of(2000)));
    }

    @Test
    public void testGetMonatzins() {
        assertEquals(Geldbetrag.of(5), Zinssatz.of("3%").getMonatszins(Geldbetrag.of(2000)));
    }

    @Test
    public void testGetTageszins() {
        assertEquals(Geldbetrag.of(0.1), Zinssatz.of("2%").getTageszins(Geldbetrag.of(1800)));
    }

    @Test
    public void testGetZinsenMonate() {
        assertEquals(Geldbetrag.of(30), Zinssatz.of("3%").getZinsen(Geldbetrag.of(2000), Period.ofMonths(6)));
    }

    @Test
    public void testGetZinsenTage() {
        assertEquals(Geldbetrag.of(2.5), Zinssatz.of("3%").getZinsen(Geldbetrag.of(2000), Period.ofDays(15)));
    }

    /**
     * Der Testfall stammt aus
     * https://www.postbank.de/themenwelten/artikel_zinsen-und-zinsberechnung-ein-ueberblick.html.
     */
    @Test
    public void testGetZinsesZinsen() {
        assertEquals(Geldbetrag.of(219), Zinssatz.of("2%").getZinsen(Geldbetrag.of(1000), Period.ofYears(10)));
    }

    @Test
    public void testOfZinssatz() {
        Zinssatz zinssatz = Zinssatz.of(Geldbetrag.of(2000), Geldbetrag.of(60), 12);
        assertEquals(Zinssatz.of("3.00%"), zinssatz);
    }

}