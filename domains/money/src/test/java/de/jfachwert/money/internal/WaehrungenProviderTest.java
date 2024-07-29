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
 * (c)reated 04.09.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.money.internal;

import org.junit.jupiter.api.Test;

import javax.money.CurrencyQuery;
import javax.money.CurrencyQueryBuilder;
import javax.money.CurrencyUnit;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Test fuer {@link WaehrungenProvider}-Klasse.
 *
 * @author oboehm
 */
public final class WaehrungenProviderTest {

    private final WaehrungenProvider provider = new WaehrungenProvider();

    @Test
    public void testIsCurrencyAvailable() {
        CurrencyQuery query = CurrencyQueryBuilder.of().setCurrencyCodes("EUR").build();
        assertTrue(provider.isCurrencyAvailable(query));
    }

    @Test
    public void getCurrencies() {
        CurrencyQuery query = CurrencyQueryBuilder.of().setCurrencyCodes("CHF").build();
        Set<CurrencyUnit> currencies = provider.getCurrencies(query);
        assertThat(currencies, not(empty()));
    }

}