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
package de.jfachwert.bank.internal;

import de.jfachwert.bank.Waehrung;

import javax.money.CurrencyContext;
import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.spi.CurrencyProviderSpi;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Die Klasse WaehrungenProvider dient zum Registrieren von Waehrungen.
 *
 * @author oboehm
 * @since 3.0 (04.09.2019)
 */
public class WaehrungenProvider implements CurrencyProviderSpi {

    private static final Logger LOG = Logger.getLogger(WaehrungenProvider.class.getName());
    private static final Map<String, CurrencyUnit> AVAILABLE_CURRENCIES;

    static {
        Set<Currency> availableCurrencies = Currency.getAvailableCurrencies();
        Map<String, CurrencyUnit> currencyUnits = new HashMap<>(availableCurrencies.size());
        for (Currency currency : availableCurrencies) {
            CurrencyUnit cu = new Waehrung(currency);
            currencyUnits.put(cu.getCurrencyCode(), cu);
        }
        AVAILABLE_CURRENCIES = Collections.unmodifiableMap(currencyUnits);
    }

    /**
     * Return a {@link CurrencyUnit} instances matching the given
     * {@link CurrencyContext}.
     *
     * @param query the {@link CurrencyQuery} containing the parameters determining the query. not null.
     * @return the corresponding {@link CurrencyUnit}s matching, never null.
     */
    @Override
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery query) {
        Set<CurrencyUnit> currencies = new HashSet<>();
        for (String code : query.getCurrencyCodes()) {
            CurrencyUnit cu = AVAILABLE_CURRENCIES.get(code);
            if (cu != null) {
                currencies.add(cu);
            }
        }
        for (Locale country : query.getCountries()) {
            addCountryTo(currencies, country);
        }
        for (Integer numCode : query.getNumericCodes()) {
            for (Currency c: Currency.getAvailableCurrencies()){
                if (c.getNumericCode() == numCode){
                    currencies.add(AVAILABLE_CURRENCIES.get(c.getCurrencyCode()));
                }
            }
        }
        return currencies;
    }

    private void addCountryTo(Set<CurrencyUnit> currencies, Locale country) {
        try {
            Currency c = Currency.getInstance(country);
            CurrencyUnit cu = AVAILABLE_CURRENCIES.get(c.getCurrencyCode());
            if (cu != null) {
                currencies.add(cu);
            }
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.FINE, "Cannot add currency for " + country, ex);
        }
    }

    /**
     * The unique name of this currency provider instance.
     *
     * @return hte unique provider id, never null or empty.
     */
    @Override
    public String getProviderName() {
        return "jfachwert";
    }

}
