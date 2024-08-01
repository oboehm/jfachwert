/*
 * Copyright (c) 2024 by Oli B.
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
 * (c)reated 29.07.24 by oboehm
 */
package de.jfachwert.bank

import de.jfachwert.KSimpleValidator
import de.jfachwert.money.Waehrung
import de.jfachwert.money.pruefung.exception.LocalizedUnknownCurrencyException
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.money.CurrencyUnit

/**
 * Diese Klasse wurde ins money-Package verschoben. Sie ist nur noch aus
 * Kompatibiltaetsgruenden fuer eine Uebergangszeit im bank-Package.
 *
 * @deprecated: durch de.jfachwert.money.Waehrung ersetzt
 */
class Waehrung(code: Currency, validator: KSimpleValidator<Currency>) :
    de.jfachwert.money.Waehrung(code, validator) {

    companion object {
        private val log = Logger.getLogger(Waehrung::class.java.name)
        private val VALIDATOR: KSimpleValidator<String> = Validator()

        /** Default-Waehrung, die durch die Landeseinstellung (Locale) vorgegeben wird.  */
        @JvmField
        val DEFAULT_CURRENCY = defaultCurrency

        /** Default-Waehrung, die durch die Landeseinstellung (Locale) vorgegeben wird.  */
        @JvmField
        val DEFAULT = Waehrung(DEFAULT_CURRENCY)

        /** Die Euro-Waehrung als Konstante.  */
        @JvmField
        val EUR = of("EUR")

        /** Null-Konstante fuer Initialiserung.  */
        @JvmField
        val NULL = Waehrung("XXX")

        /**
         * Gibt die entsprechende Currency als Waehrung zurueck. Da die Anzahl der
         * Waehrungen ueberschaubar ist, werden sie in einem dauerhaften Cache
         * vorgehalten.
         *
         * @param currency Currency
         * @return Waehrung
         */
        @JvmStatic
        fun of(currency: Currency): Waehrung {
            return Waehrung(currency)
        }

        /**
         * Gibt die entsprechende Currency als Waehrung zurueck.
         *
         * @param currencyUnit CurrencyUnit
         * @return Waehrung
         */
        @JvmStatic
        fun of(currencyUnit: CurrencyUnit): Waehrung {
            return if (currencyUnit is Waehrung) {
                currencyUnit
            } else {
                of(currencyUnit.currencyCode)
            }
        }

        /**
         * Gibt die entsprechende Currency als Waehrung zurueck.
         *
         * @param currency Waehrung, z.B. "EUR"
         * @return Waehrung
         */
        @JvmStatic
        fun of(currency: String): Waehrung {
            return of(toCurrency(currency))
        }

        /**
         * Ermittelt aus dem uebergebenen String die entsprechende
         * [Currency].
         *
         * @param name z.B. "EUR" oder auch ein einzelnes Symbol
         * @return die entsprechende Waehrung
         */
        @JvmStatic
        fun toCurrency(name: String): Currency {
            return try {
                Currency.getInstance(name)
            } catch (iae: IllegalArgumentException) {
                if (name.length <= 3) {
                    for (c in Currency.getAvailableCurrencies()) {
                        if (matchesCurrency(name, c)) {
                            return c
                        }
                    }
                    toFallbackCurrency(name, iae)
                } else {
                    try {
                        toCurrency(name.substring(0, 3))
                    } catch (ex: LocalizedUnknownCurrencyException) {
                        throw LocalizedUnknownCurrencyException(name, ex)
                    }
                }
            }
        }

        private fun matchesCurrency(name: String, c: Currency): Boolean {
            return name.equals(c.currencyCode, ignoreCase = true) || name.equals(c.symbol, ignoreCase = true)
        }

        private fun toFallbackCurrency(name: String, iae: IllegalArgumentException): Currency {
            return if (name == "\u20ac") {
                Currency.getInstance("EUR")
            } else {
                throw LocalizedUnknownCurrencyException(name, iae)
            }
        }

        /**
         * Validiert den uebergebenen Waehrungscode.
         *
         * @param code Waehrungscode als String
         * @return Waehrungscode zur Weiterverarbeitung
         */
        @JvmStatic
        fun validate(code: String): String {
            return VALIDATOR.validate(code)
        }

        /**
         * Lieft das Waehrungssymbol der uebergebenen Waehrungseinheit.
         *
         * @param cu Waehrungseinheit
         * @return z.B. das Euro-Zeichen
         */
        @JvmStatic
        fun getSymbol(cu: CurrencyUnit): String {
            return try {
                of(cu).symbol
            } catch (ex: IllegalArgumentException) {
                log.log(Level.FINE, "Kann das Symbol fuer '$cu' nicht ermitteln:", ex)
                cu.currencyCode
            }
        }

        /**
         * Ermittelt die Waehrung. Urspruenglich wurde die Default-Currency ueber
         * <pre>
         * Currency.getInstance(Locale.getDefault())
         * </pre>
         * ermittelt. Dies fuehrte aber auf der Sun zu Problemen, da dort
         * die Currency fuer die Default-Locale folgende Exception hervorrief:
         * <pre>
         * java.lang.IllegalArgumentException
         * at java.util.Currency.getInstance(Currency.java:384)
         * at de.jfachwert.money.Geldbetrag.&lt;clinit&gt;
         * ...
         * </pre>
         *
         * @return normalerweise die deutsche Currency
         */
        private val defaultCurrency: Currency
            get() {
                val locales = arrayOf(Locale.getDefault(), Locale.GERMANY, Locale.GERMAN)
                for (loc in locales) {
                    try {
                        return Currency.getInstance(loc)
                    } catch (iae: IllegalArgumentException) {
                        log.log(Level.FINE,"Keine Waehrung fuer Locale '$loc' verhanden - versuche es mit einer anderen.")
                        log.log(Level.FINER,"Details:", iae)
                    }
                }
                return Currency.getAvailableCurrencies().iterator().next()
            }

    }

}