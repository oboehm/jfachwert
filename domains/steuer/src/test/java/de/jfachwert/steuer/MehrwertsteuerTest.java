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
import de.jfachwert.math.Prozent;
import org.hamcrest.MatcherAssert;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Test fuer [Mehrwertsteuer]-Klasse.
 *
 * @author oboehm
 */
public final class MehrwertsteuerTest extends FachwertTest {

    private static final Logger LOG = Logger.getLogger(MehrwertsteuerTest.class.getName());
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

    @Test
    public void testOfCaching() {
        Prozent p = Prozent.of("7%");
        Mehrwertsteuer m1 = Mehrwertsteuer.of(p);
        Mehrwertsteuer m2 = Mehrwertsteuer.of(p);
        assertSame(m1, m2);
        if (forceGC()) {
            Mehrwertsteuer m3 = Mehrwertsteuer.of(p);
            assertNotSame(m1, m3);
            assertEquals(m1, m3);
        } else {
            LOG.info("GC wurde nicht durchgefuehrt.");
        }
    }

    /**
     * Null-Werte werden bereits von Kotlin zurueckgewiesen, waehrend wir in
     * der Java-Version hier eine IllegalArgumentException erwartet haben.
     * Jetzt akzeptieren wir beides.
     */
    @Test
    public void testMehrwertsteuerNull() {
        Prozent nullProzent = null;
        assertThrows(RuntimeException.class, () -> new Mehrwertsteuer(nullProzent));
    }

    @Test
    public void testNettoZuBrutto() {
        Money netto = Money.of(10, "EUR");
        assertEquals(Money.of(11.90, "EUR"), mehrwertsteuer.nettoZuBrutto(netto));
    }

    @Test
    public void testBruttoZuNetto() {
        Money brutto = Money.of(11.90, "EUR");
        assertEquals(Money.of(10, "EUR"), mehrwertsteuer.bruttoZuNetto(brutto));
    }

    @Test
    public void testBetragVonNetto() {
        Money netto = Money.of(10, "EUR");
        assertEquals(Money.of(1.90, "EUR"), mehrwertsteuer.betragVonNetto(netto));
    }

    @Test
    public void testBetragVonBrutto() {
        Money brutto = Money.of(11.90, "EUR");
        assertEquals(Money.of(1.90, "EUR"), mehrwertsteuer.betragVonBrutto(brutto));
    }

    @Test
    public void testCompareTo() {
        MatcherAssert.assertThat(Mehrwertsteuer.CH_NORMAL.compareTo(Mehrwertsteuer.CH_REDUZIERT), greaterThan(0));
        MatcherAssert.assertThat(Mehrwertsteuer.CH_SONDER.compareTo(Mehrwertsteuer.CH_NORMAL), lessThan(0));
    }

}