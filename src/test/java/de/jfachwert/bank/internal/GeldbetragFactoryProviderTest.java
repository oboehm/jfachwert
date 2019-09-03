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
package de.jfachwert.bank.internal;

import de.jfachwert.bank.Geldbetrag;
import org.junit.Test;

import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit-Test fuer {@link GeldbetragFactoryProvider}-Klasse.
 *
 * @author oboehm
 */
public final class GeldbetragFactoryProviderTest {

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
        assertEquals(Geldbetrag.ZERO, factory.setCurrency("EUR").create());
    }

    @Test
    public void getDefaultMonetaryContext() {
        MonetaryContext context = provider.getDefaultMonetaryContext();
        assertNotNull(context);
    }

}