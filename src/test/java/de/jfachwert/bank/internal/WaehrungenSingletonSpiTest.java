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
package de.jfachwert.bank.internal;

import org.junit.Test;

import javax.money.CurrencyQuery;
import javax.money.CurrencyQueryBuilder;
import javax.money.CurrencyUnit;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link WaehrungenSingletonSpi}-Klasse.
 *
 * @author oboehm
 */
public final class WaehrungenSingletonSpiTest {
    
    private final WaehrungenSingletonSpi singletonSpi = new WaehrungenSingletonSpi();

    @Test
    public void testGetDefaultProviderChain() {
        List<String> providerChain = singletonSpi.getDefaultProviderChain();
        assertThat(providerChain, not(empty()));
    }

    @Test
    public void getProviderNames() {
        Set<String> providerNames = singletonSpi.getProviderNames();
        assertThat(providerNames, not(empty()));
    }

    @Test
    public void getCurrencies() {
        CurrencyQuery query = CurrencyQueryBuilder.of().setCountries(Locale.GERMANY).build();
        Set<CurrencyUnit> currencies = singletonSpi.getCurrencies(query);
        assertThat(currencies, not(empty()));
    }

}
