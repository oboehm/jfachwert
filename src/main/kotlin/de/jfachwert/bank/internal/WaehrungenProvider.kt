/*
 * Copyright (c) 2019, 2020 by Oliver Boehm
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
package de.jfachwert.bank.internal

import de.jfachwert.bank.Waehrung
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.money.CurrencyQuery
import javax.money.CurrencyUnit
import javax.money.spi.CurrencyProviderSpi

/**
 * Die Klasse WaehrungenProvider dient zum Registrieren von Waehrungen.
 *
 * @author oboehm
 * @since 3.0 (04.09.2019)
 */
class WaehrungenProvider : CurrencyProviderSpi {

    companion object {

        private val LOG = Logger.getLogger(WaehrungenProvider::class.java.name)
        private val AVAILABLE_CURRENCIES: Map<String, CurrencyUnit>

        init {
            val availableCurrencies = Currency.getAvailableCurrencies()
            val currencyUnits: MutableMap<String, CurrencyUnit> = HashMap<String, CurrencyUnit>(availableCurrencies.size)
            for (currency in availableCurrencies) {
                val cu: CurrencyUnit = Waehrung(currency)
                currencyUnits[cu.currencyCode] = cu
            }
            AVAILABLE_CURRENCIES = Collections.unmodifiableMap(currencyUnits)
        }
    }

    /**
     * Liefert eine [CurrencyUnit] die auf den uebergebenen Query-Parameter
     * passt.
     *
     * @param query die [CurrencyQuery] mit den Query-Parametern
     * @return die entsprechende [CurrencyUnit]s matching
     */
    override fun getCurrencies(query: CurrencyQuery): Set<CurrencyUnit> {
        val currencies: MutableSet<CurrencyUnit> = HashSet()
        for (code in query.currencyCodes) {
            val cu = AVAILABLE_CURRENCIES[code]
            if (cu != null) {
                currencies.add(cu)
            }
        }
        for (country in query.countries) {
            addCountryTo(currencies, country)
        }
        for (numCode in query.numericCodes) {
            for (c in Currency.getAvailableCurrencies()) {
                if (c.numericCode == numCode) {
                    currencies.add(AVAILABLE_CURRENCIES[c.currencyCode]!!)
                }
            }
        }
        return currencies
    }

    private fun addCountryTo(currencies: MutableSet<CurrencyUnit>, country: Locale) {
        try {
            val c = Currency.getInstance(country)
            val cu = AVAILABLE_CURRENCIES[c.currencyCode]
            if (cu != null) {
                currencies.add(cu)
            }
        } catch (ex: IllegalArgumentException) {
            LOG.log(Level.FINE, "Cannot add currency for $country", ex)
        }
    }

    /**
     * Liefert die eindeutige Provider-ID dieses Providers.
     *
     * @return eindeutige Provider-ID
     */
    override fun getProviderName(): String {
        return "jfachwert"
    }

}