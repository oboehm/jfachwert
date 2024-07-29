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
 * (c)reated 08.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.money.internal;

import org.junit.jupiter.api.Test;
import patterntesting.runtime.junit.CollectionTester;

import javax.money.CurrencyQuery;
import javax.money.CurrencyQueryBuilder;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.spi.Bootstrap;
import javax.money.spi.CurrencyProviderSpi;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit-Tests fuer {@link WaehrungenSingleton}-Klasse.
 *
 * @author oboehm
 */
public final class WaehrungenSingletonTest {
    
    private final WaehrungenSingleton singleton = new WaehrungenSingleton();

    @Test
    public void testGetDefaultProviderChain() {
        List<String> providerChain = singleton.getDefaultProviderChain();
        assertThat(providerChain, not(empty()));
    }

    @Test
    public void getProviderNames() {
        Set<String> providerNames = singleton.getProviderNames();
        assertThat(providerNames, not(empty()));
    }

    @Test
    public void getCurrencies() {
        CurrencyQuery query = CurrencyQueryBuilder.of().setCountries(Locale.GERMANY).build();
        Set<CurrencyUnit> currencies = singleton.getCurrencies(query);
        assertThat(currencies, not(empty()));
    }

    @Test
    public void getCurrenciesUnknown() {
        CurrencyQuery query = CurrencyQueryBuilder.of().setCurrencyCodes("SDR").build();
        Set<CurrencyUnit> expected = new HashSet<>();
        for (CurrencyProviderSpi spi : Bootstrap.getServices(CurrencyProviderSpi.class)) {
            if (!(spi instanceof WaehrungenProvider)) {
                expected.addAll(spi.getCurrencies(query));
            }
        }
        Set<CurrencyUnit> currencies = singleton.getCurrencies(query);
        CollectionTester.assertEquals(expected, currencies);
    }

    @Test
    public void getCurrencyEuro() {
        CurrencyUnit cu = Monetary.getCurrency("EUR");
        assertNotNull(cu);
    }

}
