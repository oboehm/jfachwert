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
package de.jfachwert.steuer;

import de.jfachwert.FachwertTest;
import de.jfachwert.bank.Geldbetrag;
import de.jfachwert.math.Prozent;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

/**
 * Unit-Test fuer [Mehrwertsteuer]-Klasse.
 *
 * @author oboehm
 */
public final class MehrwertsteuerTest extends FachwertTest {

    private final Mehrwertsteuer mehrwertsteuer = Mehrwertsteuer.DE_NORMAL;

    /**
     * Zum Testen nehmen wir den deutschen Mehrwertsteuersatz von 19%.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Mehrwertsteuer createFachwert() {
        return new Mehrwertsteuer(Prozent.of("19%"));
    }

    @Test
    public void testGetProzent() {
        Mehrwertsteuer mwst = new Mehrwertsteuer(Prozent.TEN);
        assertEquals(Prozent.TEN, mwst.getProzent());
    }

    @Test
    @Override
    public void testToString() {
        Prozent p = Prozent.of("7%");
        assertEquals(p.toString(), new Mehrwertsteuer(p).toString());
    }

    /**
     * Die statische of-Methode sollte keine Dupliakte zurueckliefern.
     */
    @Test
    public void testOf() {
        Prozent p = Prozent.of("19%");
        assertSame(Mehrwertsteuer.of(p), Mehrwertsteuer.of(p));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMehrwertsteuerNull() {
        Prozent nullProzent = null;
        new Mehrwertsteuer(nullProzent);
    }

    @Test
    public void testNettoZuBrutto() {
        Geldbetrag netto = Geldbetrag.of(10);
        assertEquals(Geldbetrag.of(11.90), mehrwertsteuer.nettoZuBrutto(netto));
    }

    @Test
    public void testBruttoZuNetto() {
        Geldbetrag brutto = Geldbetrag.of(11.90);
        assertEquals(Geldbetrag.of(10), mehrwertsteuer.bruttoZuNetto(brutto));
    }

    @Test
    public void testBetragVonNetto() {
        Geldbetrag netto = Geldbetrag.of(10);
        assertEquals(Geldbetrag.of(1.90), mehrwertsteuer.betragVonNetto(netto));
    }

    @Test
    public void testBetragVonBrutto() {
        Geldbetrag brutto = Geldbetrag.of(11.90);
        assertEquals(Geldbetrag.of(1.90), mehrwertsteuer.betragVonBrutto(brutto));
    }

    @Test
    public void testCompareTo() {
        assertThat(Mehrwertsteuer.CH_NORMAL.compareTo(Mehrwertsteuer.CH_REDUZIERT), greaterThan(0));
        assertThat(Mehrwertsteuer.CH_SONDER.compareTo(Mehrwertsteuer.CH_NORMAL), lessThan(0));
    }

}