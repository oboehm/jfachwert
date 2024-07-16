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
 * (c)reated 13.08.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank.internal

import de.jfachwert.bank.internal.GeldbetragFormatter.Companion.of
import java.util.*
import javax.money.format.AmountFormatContextBuilder
import javax.money.format.AmountFormatQuery
import javax.money.format.MonetaryAmountFormat
import javax.money.spi.Bootstrap
import javax.money.spi.MonetaryAmountFormatProviderSpi
import javax.money.spi.MonetaryFormatsSingletonSpi

/**
 * Die Klasse WaehrungsformatSingletonSpi wird benoetigt, um die entsprechenden
 * Waehrungsformate zu erzeugen und das TCK unter Java 11 zu bestehen.
 *
 * @author oboehm
 * @since 3.0 (13.08.2019)
 */
class WaehrungsformatSingleton : MonetaryFormatsSingletonSpi {

    companion object {

        private val MONETARY_FORMATS_SINGLETON_SPIS: MutableCollection<MonetaryFormatsSingletonSpi> = ArrayList()

        init {
            for (spi in Bootstrap.getServices(MonetaryFormatsSingletonSpi::class.java)) {
                if (spi !is WaehrungsformatSingleton) {
                    MONETARY_FORMATS_SINGLETON_SPIS.add(spi)
                }
            }
        }
    }

    /**
     * Ermittelt alle verfuegbaren Loacales.
     *
     * @param providers zusaetzliche Provider (werden aber ignoriert)
     * @return verfuegbare Locales, nie `null`.
     */
    override fun getAvailableLocales(vararg providers: String): Set<Locale> {
        return WaehrungsformatProvider.INSTANCE.availableLocales
    }

    /**
     * Ermittelt alle Formate, die auf die uebergebene Query uebereinstimmen.
     *
     * @param formatQuery The format query defining the requirements of the formatter.
     * @return the corresponding [MonetaryAmountFormat] instances, never null
     */
    override fun getAmountFormats(formatQuery: AmountFormatQuery): Collection<MonetaryAmountFormat> {
        val result: MutableCollection<MonetaryAmountFormat> = ArrayList()
        val locale = formatQuery.locale
        val amountFactory = formatQuery.monetaryAmountFactory
        val formatName = formatQuery.formatName
        val context = AmountFormatContextBuilder.of(formatName)
                .setMonetaryAmountFactory(amountFactory)
                .setLocale(locale ?: Locale.getDefault())
                .build()
        result.add(of(context))
        for (spi in Bootstrap.getServices(MonetaryAmountFormatProviderSpi::class.java)) {
            result.addAll(spi.getAmountFormats(formatQuery))
        }
        return result
    }

    /**
     * Ermittelt die Namen der aktuell registrierten Format-Provider.
     *
     * @return Providernamen
     */
    override fun getProviderNames(): Set<String> {
        val providerNames: MutableSet<String> = HashSet()
        for (spi in MONETARY_FORMATS_SINGLETON_SPIS) {
            providerNames.addAll(spi.providerNames)
        }
        return providerNames
    }

    /**
     * Liefert die Default-Provider-Kette
     *
     * @return Default-Provider-Kette.
     */
    override fun getDefaultProviderChain(): List<String> {
        val defaultProviderChain: MutableList<String> = ArrayList()
        for (spi in MONETARY_FORMATS_SINGLETON_SPIS) {
            defaultProviderChain.addAll(spi.defaultProviderChain)
        }
        return defaultProviderChain
    }

}