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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 03.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.money.internal;

import de.jfachwert.money.Geldbetrag;
import de.jfachwert.money.GeldbetragFactory;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import java.util.Collection;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit-Tests fuer {@link GeldbetragSingleton}-Klasse.
 *
 * @author oboehm
 */
public final class GeldbetragSingletonTest {
    
    private final GeldbetragSingleton singletonSpi = new GeldbetragSingleton();

    @Test
    public void testGetAmountTypes() {
        Set<Class<? extends MonetaryAmount>> amountTypes = singletonSpi.getAmountTypes();
        assertThat(amountTypes, hasItem(Geldbetrag.class));
    }
    
    @Test
    public void testGetDefaultAmountType() {
        assertEquals(Geldbetrag.class, singletonSpi.getDefaultAmountType());
    }
    
    @Test
    public void testGetAmountFactories() {
        Collection<MonetaryAmountFactory<?>> amountFactories = singletonSpi.getAmountFactories();
        long n = amountFactories.stream().filter(f -> GeldbetragFactory.class.equals(f.getClass())).count();
        assertEquals(1L, n);
    }

}
