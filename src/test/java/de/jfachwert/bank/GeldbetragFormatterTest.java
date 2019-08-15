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
 * (c)reated 12.10.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryParseException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer @link{de.jfachwert.bank.GeldbetragFormatter}.
 * {@link GeldbetragFormatter}
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since (12.10.18)
 */
public final class GeldbetragFormatterTest {

    private final GeldbetragFormatter formatter = new GeldbetragFormatter();

    @Test
    public void testParse() {
        Geldbetrag parsed = formatter.parse("100 CHF");
        assertEquals(Geldbetrag.of(100, "CHF"), parsed);
    }

    @Test
    public void testParseEurozeichen() {
        assertEquals(formatter.parse("11 EUR"), formatter.parse("11\u20ac"));
    }

    @Test
    public void testParseCurrencyNumber() {
        Geldbetrag parsed = formatter.parse("BRL 123.45");
        assertEquals(Geldbetrag.of(new BigDecimal("123.45"), "BRL"), parsed);
    }

    @Test(expected = MonetaryParseException.class)
    public void testParseInvalid() {
        formatter.parse("1-2-3 Polizei");
    }

    @Test
    public void testQueryFrom() {
        String s = formatter.queryFrom(Geldbetrag.of(20000, "DKK"));
        assertThat(s, containsString("DKK"));
    }

    @Test
    public void testPrint() throws IOException {
        Appendable appendable = new StringBuilder();
        formatter.print(appendable, Geldbetrag.of(100, "GBP"));
        assertThat(appendable.toString(), containsString("GBP"));
    }

    @Test
    public void testGetContext() {
        AmountFormatContext context = formatter.getContext();
        assertNotNull(context);
        assertNotNull(context.getLocale());
    }

    @Test
    public void testFormat() {
        Number einsfuffzig = BigDecimal.valueOf(1.5);
        MonetaryAmount betrag = Geldbetrag.of(einsfuffzig, "EUR");
        MonetaryAmount money = Money.of(einsfuffzig, "EUR");
        assertEquals(formatter.format(betrag), formatter.format(money));
    }

}