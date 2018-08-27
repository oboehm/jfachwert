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
 * (c)reated 07.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank.internal;

import de.jfachwert.bank.Waehrung;

import javax.money.CurrencyQuery;
import javax.money.CurrencyQueryBuilder;
import javax.money.CurrencyUnit;
import javax.money.spi.Bootstrap;
import javax.money.spi.CurrencyProviderSpi;
import javax.money.spi.MonetaryCurrenciesSingletonSpi;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Die Klasse WaehrungenSingletonSpi wird benoetigt, umn die entsprechende
 * Waehrung zu erzeugen und das TCK zu bestehen.
 *
 * @author oboehm
 * @since 1.0 (07.08.2018)
 */
public final class WaehrungenSingletonSpi implements MonetaryCurrenciesSingletonSpi {

    private static final Logger LOG = Logger.getLogger(WaehrungenSingletonSpi.class.getName());

    /**
     * Access a list of the currently registered default providers. The default providers are used, when
     * no provider names are passed by the caller.
     *
     * @return the currencies returned by the given provider chain. If not provider names are provided
     * the default provider chain configured in {@code javamoney.properties} is used.
     * @see #getCurrencies(String...)
     * @see CurrencyQueryBuilder
     */
    @Override
    public List<String> getDefaultProviderChain() {
        List<String> list = new ArrayList<>(getProviderNames());
        return list;
    }

    /**
     * Access a list of the currently registered providers. Th names can be used to
     * access subsets of the overall currency range by calling {@link #getCurrencies(String...)}.
     *
     * @return the currencies returned by the given provider chain. If not provider names are provided
     * the default provider chain configured in {@code javamoney.properties} is used.
     */
    @Override
    public Set<String> getProviderNames() {
        Set<String> result = new HashSet<>();
        for (CurrencyProviderSpi spi : Bootstrap.getServices(CurrencyProviderSpi.class)) {
            result.add(spi.getProviderName());
        }
        return result;
    }

    /**
     * Access all currencies matching the given query.
     *
     * @param query The currency query, not null.
     * @return a set of all currencies found, never null.
     */
    @Override
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery query) {
        Set<CurrencyUnit> result = new HashSet<>();
        for (Locale locale : query.getCountries()) {
            try {
                result.add(Waehrung.of(Currency.getInstance(locale)));
            } catch (IllegalArgumentException ex) {
                LOG.log(Level.WARNING, "Cannot get currency for locale '" + locale + "':", ex);
            }
        }
        for (String currencyCode : query.getCurrencyCodes()) {
            try {
                result.add(Waehrung.of(currencyCode));
            } catch (IllegalArgumentException ex) {
                LOG.log(Level.WARNING, "Cannot get currency '" + currencyCode + "':", ex);
            }
        }
        for (CurrencyProviderSpi spi : Bootstrap.getServices(CurrencyProviderSpi.class)) {
            result.addAll(spi.getCurrencies(query));
        }
        return result;
    }

}
