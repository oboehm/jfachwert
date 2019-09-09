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
 * (c)reated 27.08.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank.internal;

import de.jfachwert.bank.GeldbetragFactory;
import org.javamoney.moneta.internal.FastMoneyAmountFactory;
import org.junit.Test;

import javax.money.MonetaryAmountFactory;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatQuery;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * Unit-Test fuer {@link WaehrungsformatProvider}-Klasse.
 *
 * @author oboehm
 */
public final class WaehrungsformatProviderTest {

    private final WaehrungsformatProvider providerSpi = new WaehrungsformatProvider();

    @Test
    public void testGetAmountFormatsGeldbetrag() {
        Collection<MonetaryAmountFormat> formats = getMonetaryAmountFormats(new GeldbetragFactory());
        assertEquals(1, formats.size());
    }

    @Test
    public void testGetAmountFormatsFastMoney() {
        Collection<MonetaryAmountFormat> formats = getMonetaryAmountFormats(new FastMoneyAmountFactory());
        assertTrue(formats.isEmpty());
    }

    private Collection<MonetaryAmountFormat> getMonetaryAmountFormats(MonetaryAmountFactory factory) {
        AmountFormatQueryBuilder builder = AmountFormatQueryBuilder.of("jachwert").setLocale(Locale.GERMAN);
        builder.setMonetaryAmountFactory(factory);
        AmountFormatQuery query = builder.build();
        return providerSpi.getAmountFormats(query);
    }

    @Test
    public void testGetAmountFormatsContext() {
        AmountFormatQueryBuilder builder = AmountFormatQueryBuilder.of("jachwert").setLocale(Locale.GERMAN);
        builder.setMonetaryAmountFactory(new GeldbetragFactory());
        builder.set("hello", "world");
        AmountFormatQuery query = builder.build();
        Collection<MonetaryAmountFormat> amountFormats = providerSpi.getAmountFormats(query);
        assertThat(amountFormats, not(empty()));
        for (MonetaryAmountFormat format : amountFormats) {
            AmountFormatContext context = format.getContext();
            String hello = context.get("hello", String.class);
            assertEquals("world", hello);
        }
    }

    @Test
    public void testGetProviderName() {
        String name = providerSpi.getProviderName();
        assertNotNull(name);
    }

    @Test
    public void testGetAvailableLocales() {
        Set<Locale> availableLocales = providerSpi.getAvailableLocales();
        assertNotNull(availableLocales);
    }

    @Test
    public void testGetAvailableFormatNames() {
        Set<String> availableFormatNames = providerSpi.getAvailableFormatNames();
        assertNotNull(availableFormatNames);
    }

}