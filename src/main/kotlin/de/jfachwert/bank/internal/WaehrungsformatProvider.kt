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
 * (c)reated 27.08.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank.internal

import de.jfachwert.bank.GeldbetragFactory
import de.jfachwert.bank.internal.GeldbetragFormatter.Companion.of
import java.text.DecimalFormat
import java.util.*
import javax.money.format.AmountFormatContextBuilder
import javax.money.format.AmountFormatQuery
import javax.money.format.MonetaryAmountFormat
import javax.money.spi.Bootstrap
import javax.money.spi.MonetaryAmountFormatProviderSpi

/**
 * Klasse WaehrungsformatProviderSpi.
 *
 * @author oboehm
 * @since 3.0 (27.08.2019)
 */
class WaehrungsformatProvider : MonetaryAmountFormatProviderSpi {

    private val availableLocales: Set<Locale>
    private val availableFormatNames = Collections.unmodifiableSet(setOf("jfachwert"))

    /**
     * Liefert eine Liste mit dem [GeldbetragFormatter] zurueck.
     *
     * @param formatQuery enthaelt den Context, der verwendet werden soll
     * @return Liste mit [GeldbetragFormatter] (oder leere Liste)
     */
    override fun getAmountFormats(formatQuery: AmountFormatQuery): Collection<MonetaryAmountFormat> {
        val amountFormats: MutableCollection<MonetaryAmountFormat> = ArrayList()
        if (formatQuery.providerNames.contains(providerName) || formatQuery
                        .monetaryAmountFactory is GeldbetragFactory) {
            val locale = formatQuery.locale
            val builder = AmountFormatContextBuilder.of("jfachwert")
            builder.setLocale(locale ?: Locale.getDefault())
            builder.importContext(formatQuery, false)
            builder.setMonetaryAmountFactory(formatQuery.monetaryAmountFactory)
            amountFormats.add(of(builder.build()))
        } else {
            for (spi in Bootstrap.getServices(MonetaryAmountFormatProviderSpi::class.java)) {
                if (spi !is WaehrungsformatProvider) {
                    amountFormats.addAll(spi.getAmountFormats(formatQuery))
                }
            }
        }
        return amountFormats
    }

    /**
     * Liefert eine Liste der unterstuetzten Locales.
     *
     * @return verfuegbare Locales, nie `null`.
     */
    override fun getAvailableLocales(): Set<Locale> {
        return availableLocales
    }

    /**
     * Als Formatname wird lediglich eine Liste mit "jfachwert" zurueckgegeben.
     *
     * @return Set mit "jfachwert"
     */
    override fun getAvailableFormatNames(): Set<String> {
        return availableFormatNames
    }

    /**
     * Access the provider's name.
     *
     * @return this provider's name, not null.
     */
    override fun getProviderName(): String {
        return "jfachwert"
    }

    companion object {
        @JvmField
        val INSTANCE = WaehrungsformatProvider()
    }

    init {
        val locales: MutableSet<Locale> = HashSet()
        locales.add(Locale.GERMANY)
        locales.addAll(Arrays.asList(*DecimalFormat.getAvailableLocales()))
        availableLocales = Collections.unmodifiableSet(locales)
    }

}