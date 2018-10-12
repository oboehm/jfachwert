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
 * (c)reated 11.10.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Der GeldbetragMoneyTest vergleicht die Implementierung der
 * {@link Geldbetrag}-Klasse mit der Referenz-Implementierung
 * ({@link org.javamoney.moneta.Money}-Klasse).
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 1.0.1 (11.10.18)
 */
public class GeldbetragMoneyTest {

    private Number number = new BigDecimal("123.45");
    private CurrencyUnit currency = Waehrung.of("DEM");
    private MonetaryContext mc = MonetaryContextBuilder.of().build();

    @Test
    public void testOfNumberString() {
        Money money = Money.of(number, "EUR");
        Geldbetrag betrag = Geldbetrag.of(number, "EUR");
        assertEqualsMonetaryAmount(money, betrag);
    }

    @Test
    public void testOfNumberCurrencyUnit() {
        Money money = Money.of(number, currency);
        Geldbetrag betrag = Geldbetrag.of(number, currency);
        assertEqualsMonetaryAmount(money, betrag);
    }

    @Test
    public void testOfNumberStringMonetaryContext() {
        Money money = Money.of(number, "EUR", mc);
        Geldbetrag betrag = Geldbetrag.of(number, "EUR", mc);
        assertEqualsMonetaryAmount(money, betrag);
    }

    @Test
    public void testOfNumberCurrencyUnitMonetaryContext() {
        Money money = Money.of(number, currency, mc);
        Geldbetrag betrag = Geldbetrag.of(number, currency, mc);
        assertEqualsMonetaryAmount(money, betrag);
    }

    @Test
    public void testFrom() {
        Money money = Money.of(number, "AUD");
        Geldbetrag betrag = Geldbetrag.of(number, "AUD");
        assertEquals(betrag, Geldbetrag.from(money));
        assertEquals(money, Money.from(betrag));
    }

    @Test
    public void testParse() {
        Money money = Money.of(number, "BRL");
        Geldbetrag betrag = Geldbetrag.parse(money.toString());
        assertEqualsMonetaryAmount(money, betrag);
    }

    private static void assertEqualsMonetaryAmount(Money money, Geldbetrag betrag) {
        assertEquals(0, betrag.compareTo(money));
        assertEquals(0, money.compareTo(betrag));
        assertEquals(Geldbetrag.of(money), betrag);
    }

}
