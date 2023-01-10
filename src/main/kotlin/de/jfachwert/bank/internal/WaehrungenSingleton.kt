/*
 * Copyright (c) 2018-2023 by Oliver Boehm
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
package de.jfachwert.bank.internal

import de.jfachwert.bank.Waehrung.Companion.of
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.money.CurrencyQuery
import javax.money.CurrencyQueryBuilder
import javax.money.CurrencyUnit
import javax.money.UnknownCurrencyException
import javax.money.spi.Bootstrap
import javax.money.spi.CurrencyProviderSpi
import javax.money.spi.MonetaryCurrenciesSingletonSpi

/**
 * Die Klasse WaehrungenSingletonSpi wird benoetigt, um die entsprechende
 * Waehrung zu erzeugen und das TCK zu bestehen.
 *
 * @author oboehm
 * @since 1.0 (07.08.2018)
 */
class WaehrungenSingleton : MonetaryCurrenciesSingletonSpi {

    private val log = Logger.getLogger(WaehrungenSingleton::class.java.name)

    /**
     * Access a list of the currently registered default providers. The default providers are used, when
     * no provider names are passed by the caller.
     *
     * @return the currencies returned by the given provider chain. If not provider names are provided
     * the default provider chain configured in `javamoney.properties` is used.
     * @see .getCurrencies
     */
    override fun getDefaultProviderChain(): List<String> {
        return ArrayList(providerNames)
    }

    /**
     * Access a list of the currently registered providers. Th names can be used to
     * access subsets of the overall currency range by calling [.getCurrencies].
     *
     * @return the currencies returned by the given provider chain. If not provider names are provided
     * the default provider chain configured in `javamoney.properties` is used.
     */
    override fun getProviderNames(): Set<String> {
        val result: MutableSet<String> = HashSet()
        for (spi in Bootstrap.getServices(CurrencyProviderSpi::class.java)) {
            result.add(spi.providerName)
        }
        return result
    }

    /**
     * Access all currencies matching the given query.
     *
     * @param query The currency query, not null.
     * @return a set of all currencies found, never null.
     */
    override fun getCurrencies(query: CurrencyQuery): Set<CurrencyUnit> {
        val result: MutableSet<CurrencyUnit> = HashSet()
        for (locale in query.countries) {
            try {
                result.add(of(Currency.getInstance(locale)))
            } catch (ex: IllegalArgumentException) {
                LOG.log(Level.WARNING, "Cannot get currency for locale '$locale':", ex)
            } catch (ex: UnknownCurrencyException) {
                LOG.log(Level.WARNING, "Cannot get currency for locale '$locale':", ex)
            }
        }
        for (currencyCode in query.currencyCodes) {
            try {
                result.add(of(currencyCode))
            } catch (ex: IllegalArgumentException) {
                LOG.log(Level.WARNING, "Cannot get currency '$currencyCode':", ex)
            } catch (ex: UnknownCurrencyException) {
                LOG.log(Level.WARNING, "Cannot get currency '$currencyCode':", ex)
            }
        }
        for (spi in Bootstrap.getServices(CurrencyProviderSpi::class.java)) {
            result.addAll(spi.getCurrencies(query))
        }
        return result
    }

    override fun getCurrency(currencyCode: String, vararg providers: String?): CurrencyUnit {
        val found: Collection<CurrencyUnit> =
            getCurrencies(CurrencyQueryBuilder.of().setCurrencyCodes(currencyCode).setProviderNames(*providers).build())
        if (found.isEmpty()) {
            throw UnknownCurrencyException(currencyCode)
        }
        if (found.size > 1) {
            log.fine("${found} found for $currencyCode - using first one.")
        }
        return found.iterator().next()
    }

    companion object {
        private val LOG = Logger.getLogger(WaehrungenSingleton::class.java.name)
    }

}