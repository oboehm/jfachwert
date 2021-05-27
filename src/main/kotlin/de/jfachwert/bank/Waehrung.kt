/*
 * Copyright (c) 2018-2020 by Oliver Boehm
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
 * (c)reated 04.08.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.KFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.LocalizedUnknownCurrencyException
import org.apache.commons.collections4.map.ReferenceMap
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.money.CurrencyContext
import javax.money.CurrencyUnit
import javax.money.UnknownCurrencyException

/**
 * Die Klasse Waehrung wurde fuer die Implementierung fuer [Geldbetrag]
 * eingefuehrt und implementiert die [CurrencyUnit]. Diese ist
 * Bestandteil der Money-API.
 *
 * @author oliver (ob@aosd.de)
 * @since 1.0
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Waehrung protected constructor(code: Currency, validator: KSimpleValidator<Currency>) : KFachwert, Comparable<CurrencyUnit>, CurrencyUnit {

    companion object {

        private val LOG = Logger.getLogger(Waehrung::class.java.name)
        private val CACHE: MutableMap<String, Waehrung> = ReferenceMap()
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
            val key = currency.currencyCode
            return CACHE.computeIfAbsent(key) { t: String? -> Waehrung(currency) }
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
                LOG.log(Level.WARNING, "Cannot get symbol for '$cu':", ex)
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
         * at de.jfachwert.bank.Geldbetrag.&lt;clinit&gt;
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
                        LOG.log(Level.INFO,
                                "No currency for locale '$loc' available on this machine - will try next one.", iae)
                    }
                }
                return Currency.getAvailableCurrencies().iterator().next()
            }

        init {
            CACHE[DEFAULT_CURRENCY.currencyCode] = DEFAULT
        }
    }

    /**
     * Liefert die Waehrung als Currency zurueck.
     *
     * @return Waehrung als Currency
     */
    val code: Currency

    /**
     * Darueber kann eine Waehrung angelegt werden.
     *
     * @param code z.B. "EUR"
     */
    constructor(code: String) : this(toCurrency(code)) {}

    /**
     * Darueber kann eine Waehrung angelegt werden.
     *
     * @param code Waehrung
     */
    constructor(code: Currency) : this(code, NullValidator<Currency>()) {}

    /**
     * Liefert die Currency zurueck.
     *
     * @return die Currency aus java.util.
     */
    val currency: Currency
        get() = code

    /**
     * Liefert den Waehrungscode.
     *
     * @return z.B. "EUR"
     */
    override fun getCurrencyCode(): String {
        return code.currencyCode
    }

    /**
     * Liefert den numerischen Waehrungscode.
     *
     * @return z.B. 978 fuer EUro
     */
    override fun getNumericCode(): Int {
        return code.numericCode
    }

    /**
     * Liefert die Anzahl der Nachkommastellen einer Waehrung.
     *
     * @return meist 2, manchmal 0
     */
    override fun getDefaultFractionDigits(): Int {
        return code.defaultFractionDigits
    }

    override fun getContext(): CurrencyContext {
        throw UnsupportedOperationException("not yet implemented")
    }

    /**
     * Liefert das Waehrungssymbol.
     *
     * @return z.B. "$"
     */
    val symbol: String
        get() = code.symbol

    /**
     * Zum Vergleich wird der Waehrungscode herangezogen und alphabetisch
     * verglichen.
     *
     * @param other die andere Waerhung
     * @return eine negative Zahl wenn die ander Waehrung alphabetisch
     * danach kommt.
     */
    override fun compareTo(other: CurrencyUnit): Int {
        return currencyCode.compareTo(other.currencyCode)
    }

    /**
     * Zwei Waehrungen sind nur dann gleich, wenn sie vom gleichen Typ sind .
     *
     * @param other zu vergleichender Waehrung
     * @return true bei Gleichheit
     * @see java.lang.Object.equals
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Waehrung) {
            return false
        }
        return code == other.code
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    override fun hashCode(): Int {
        return code.hashCode()
    }

    /**
     * Als toString-Implementierung wird der Waehrungscode ausgegeben.
     *
     * @return z.B. "EUR"
     */
    override fun toString(): String {
        return currencyCode
    }

    init {
        this.code = validator.verify(code)
    }



    /**
     * Dieser Validator ist fuer die Ueberpruefung von Waehrungen vorgesehen.
     *
     * @since 3.0
     */
    class Validator : KSimpleValidator<String> {

        /**
         * Wenn der uebergebene Waehrungsstring gueltig ist, wird er
         * unveraendert zurueckgegeben, damit er anschliessend von der
         * aufrufenden Methode weiterverarbeitet werden kann. Ist der Wert
         * nicht gueltig, wird eine [javax.validation.ValidationException]
         * geworfen.
         *
         * @param value Waehrungs-String, der validiert wird
         * @return Wert selber, wenn er gueltig ist
         */
        override fun validate(value: String): String {
            try {
                toCurrency(value)
            } catch (ex: IllegalArgumentException) {
                throw InvalidValueException(value, "currency")
            } catch (ex: UnknownCurrencyException) {
                throw InvalidValueException(value, "currency")
            }
            return value
        }
    }

}