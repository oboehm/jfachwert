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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 04.08.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwertTest;
import org.javamoney.tck.tests.internal.TestCurrencyUnit;
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import javax.money.CurrencyUnit;
import java.util.Currency;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link Waehrung}-Klasse.
 */
public final class WaehrungTest extends AbstractFachwertTest<Currency> {

    /**
     * Zum Testen nehmen wir die uebergebene Waehrung.
     *
     * @param code gueltige Waehrung
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Waehrung createFachwert(String code) {
        return Waehrung.of(code);
    }

    /**
     * Zum Testen nehmen wir die Euro-Waehrung.
     *
     * @return "EUR"
     */
    @Override
    protected String getCode() {
        return "EUR";
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWaehrungInvalid() {
        new Waehrung("Taler");
    }

    /**
     * Hier wird {@link Waehrung#compareTo(CurrencyUnit)} ueberprueft.
     */
    @Test
    public void testCompareTo() {
        Waehrung one = Waehrung.of("CHF");
        Waehrung anotherOne = Waehrung.of("CHF");
        ObjectTester.assertEquals(one, anotherOne);
        Waehrung two = Waehrung.of("EUR");
        ObjectTester.assertNotEquals(one, two);
    }

    @Test
    public void testOf() {
        CurrencyUnit cu = new TestCurrencyUnit("AUD");
        Waehrung aud = Waehrung.of(cu);
        assertEquals(0, aud.compareTo(cu));
    }

    @Test
    public void testToCurrency() {
        Currency eur = Currency.getInstance("GBP");
        assertEquals(eur, Waehrung.toCurrency(eur.getSymbol()));
    }

    @Test
    public void testToCurrencyEurozeichen() {
        Currency eur = Currency.getInstance("EUR");
        assertEquals(eur, Waehrung.toCurrency("\u20ac"));
    }

    @Test
    public void testValidate() {
        String validated = Waehrung.validate("EUR");
        assertEquals("EUR", validated);
    }

    @Test
    public void testGetCurrency() {
        Waehrung euro = Waehrung.of("EUR");
        assertEquals(euro.getCurrencyCode(), euro.getCurrency().getCurrencyCode());
    }

}
