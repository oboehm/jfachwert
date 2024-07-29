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
 * (c)reated 03.09.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.money.internal;

import de.jfachwert.money.Geldbetrag;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;

import java.util.Currency;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Unit-Test fuer {@link GeldbetragFactoryProvider}-Klasse.
 *
 * @author oboehm
 */
public final class GeldbetragFactoryProviderTest {

    private static final Logger log = Logger.getLogger(GeldbetragFactoryProviderTest.class.getName());

    private final GeldbetragFactoryProvider provider = new GeldbetragFactoryProvider();

    @Test
    public void getMaximalMonetaryContext() {
        MonetaryContext context = provider.getMaximalMonetaryContext();
        assertNotNull(context);
    }

    @Test
    public void testGetAmountType() {
        Class<Geldbetrag> amountType = provider.getAmountType();
        assertEquals(Geldbetrag.class, amountType);
    }

    @Test
    public void testCreateMonetaryAmountFactory() {
        MonetaryAmountFactory<Geldbetrag> factory = provider.createMonetaryAmountFactory();
        Locale locale = Locale.getDefault();
        assumeTrue(hasCurrency(locale));
        Currency currency = Currency.getInstance(locale);
        String concurrency = currency.getSymbol();
        assertEquals(Geldbetrag.ZERO, factory.setCurrency(concurrency).create());
    }

    private static boolean hasCurrency(Locale locale) {
        try {
            Currency.getInstance(locale);
            return true;
        } catch(IllegalArgumentException ex) {
            log.log(Level.INFO, "System kennt keine Waehrung fuer " + locale, ex);
            return false;
        }
    }

    @Test
    public void getDefaultMonetaryContext() {
        MonetaryContext context = provider.getDefaultMonetaryContext();
        assertNotNull(context);
    }

}