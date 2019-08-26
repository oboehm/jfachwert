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
 * (c)reated 13.08.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank.internal;

import org.javamoney.moneta.internal.FastMoneyAmountFactory;
import org.junit.Test;

import javax.money.format.AmountFormatQuery;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Unit-Test fuer {@link WaehrungsformatSingletonSpi}-Klasse.
 *
 * @author oboehm
 */
public final class WaehrungsformatSingletonSpiTest {

    private final WaehrungsformatSingletonSpi singletonSpi = new WaehrungsformatSingletonSpi();

    @Test
    public void getAvailableLocales() {
        Set<Locale> availableLocales = singletonSpi.getAvailableLocales();
        assertThat(availableLocales, not(empty()));
        assertThat(availableLocales, containsInAnyOrder(DecimalFormat.getAvailableLocales()));
    }

    @Test
    public void testGetAmountFormats() {
        Collection<MonetaryAmountFormat> amountFormats = singletonSpi
                .getAmountFormats(AmountFormatQuery.of(Locale.GERMAN));
        assertThat(amountFormats, not(empty()));
    }

    /**
     * Bei gegebener MonetaryAmountFactory sollte auch nur die entsprechenden
     * Formate für diese Factory generiert werden.
     */
    @Test
    public void testGetAmountFormatsQuery() {
        AmountFormatQueryBuilder builder = AmountFormatQueryBuilder.of("default").setLocale(Locale.GERMAN);
        builder.setMonetaryAmountFactory(new FastMoneyAmountFactory());
        AmountFormatQuery query = builder.build();
        Collection<MonetaryAmountFormat> amountFormats = singletonSpi.getAmountFormats(query);
        assertEquals(1, amountFormats.size());
    }

    @Test
    public void testGetAmountFormatsWithoutLocale() {
        AmountFormatQueryBuilder builder = AmountFormatQueryBuilder.of("default");
        Collection<MonetaryAmountFormat> amountFormats = singletonSpi.getAmountFormats(builder.build());
        assertEquals(1, amountFormats.size());
        assertThat(amountFormats, not(empty()));
    }

}