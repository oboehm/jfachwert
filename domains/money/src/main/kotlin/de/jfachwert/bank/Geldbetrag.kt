/*
 * Copyright (c) 2024 by Oliver Boehm
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
 * (c)reated 29.07.2024 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank

import de.jfachwert.KSimpleValidator
import de.jfachwert.money.internal.GeldbetragFormatter
import de.jfachwert.money.internal.Zahlenwert
import de.jfachwert.pruefung.NumberValidator
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.money.CurrencyUnit
import javax.money.MonetaryAmount
import javax.money.MonetaryContext
import javax.money.format.MonetaryAmountFormat
import javax.money.format.MonetaryParseException

/**
 * Diese Klasse wurde ins money-Package verschoben. Sie ist nur noch aus
 * Kompatibiltaetsgruenden fuer eine Uebergangszeit im bank-Package.
 *
 * @deprecated: durch de.jfachwert.money.Geldbetrag ersetzt
 */
open class Geldbetrag(betrag: Number, currency: CurrencyUnit) :
    de.jfachwert.money.Geldbetrag(betrag, currency) {

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1
     */
    constructor(betrag: Long) : this(BigDecimal.valueOf(betrag))

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1.00
     */
    constructor(betrag: Double) : this(BigDecimal.valueOf(betrag))

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. "1"
     */
    constructor(betrag: String) : this(de.jfachwert.money.Geldbetrag.valueOf(betrag))

    /**
     * Dies ist zum einen der CopyConstructor als Ersatz fuer eine
     * clone-Methode, zum anderen wandelt es einen [MonetaryAmount]
     * in ein GeldBetrag-Objekt.
     *
     * @param other der andere Geldbetrag
     */
    constructor(other: MonetaryAmount) : this(other.number, Currency.getInstance(other.currency.currencyCode))

    /**
     * Erzeugt einen Geldbetrag in der angegebenen Waehrung.
     *
     * @param betrag   Geldbetrag, z.B. 1.00
     * @param currency Waehrung, z.B. Euro
     */
    @JvmOverloads
    constructor(betrag: Number, currency: Currency? = de.jfachwert.money.Waehrung.DEFAULT_CURRENCY) : this(betrag, de.jfachwert.money.Waehrung.of(currency!!))



    companion object {

        private val FACTORY = GeldbetragFactory()
        private val DEFAULT_FORMATTER = GeldbetragFormatter()
        private val NUMBER_VALIDATOR = NumberValidator()
        private val VALIDATOR: KSimpleValidator<String> = Validator()

        /** Da 0-Betraege relativ haeufig vorkommen, spendieren wir dafuer eine eigene Konstante.  */
        @JvmField
        val ZERO = Geldbetrag(BigDecimal.ZERO)

        /** Der minimale Betrag, den wir unterstuetzen.  */
        @JvmField
        val MIN_VALUE = Geldbetrag(BigDecimal.valueOf(Long.MIN_VALUE))

        /** Der maximale Betrag, den wir unterstuetzen.  */
        @JvmField
        val MAX_VALUE = Geldbetrag(BigDecimal.valueOf(Long.MAX_VALUE))

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = ZERO

        /**
         * Hierueber kann eine Geldbetrag ueber die Anzahl an Cents angelegt
         * werden.
         *
         * @param cents Cent-Betrag, z.B. 42
         * @return Geldbetrag, z.B. 0.42$
         */
        @JvmStatic
        fun fromCent(cents: Long): Geldbetrag {
            return ofMinor(Waehrung.of("EUR"), cents)
        }

        /**
         * Legt einen Geldbetrag unter Angabe der Unter-Einheit an. So liefert
         * `ofMinor(EUR, 12345)` die Instanz fuer '123,45 EUR' zurueck.
         *
         * Die Methode wurde aus Kompatibitaetsgrunden zur Money-Klasse
         * hinzugefuegt.
         *
         * @param currency Waehrung
         * @param amountMinor Betrag der Unter-Einzeit (z.B. 12345 Cents)
         * @param fractionDigits Anzahl der Nachkommastellen
         * @return Geldbetrag
         */
        @JvmOverloads
        @JvmStatic
        fun ofMinor(currency: CurrencyUnit, amountMinor: Long, fractionDigits: Int = currency.defaultFractionDigits): Geldbetrag {
            return of(BigDecimal.valueOf(amountMinor, fractionDigits), currency)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(other: String): Geldbetrag {
            return valueOf(other)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(other: String): Geldbetrag {
            try {
                val value = DEFAULT_FORMATTER.parse(other)
                return valueOf(value)
            } catch (ex: MonetaryParseException) {
                throw IllegalArgumentException(other, ex)
            }
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Long): Geldbetrag {
            return valueOf(Geldbetrag(value))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Long): Geldbetrag {
            return valueOf(Geldbetrag(value))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Double): Geldbetrag {
            return valueOf(Geldbetrag(value))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Double): Geldbetrag {
            return valueOf(Geldbetrag(value))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: String): Geldbetrag {
            return valueOf(value, currency)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: String): Geldbetrag {
            return valueOf(value, Waehrung.toCurrency(currency))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: Currency): Geldbetrag {
            return valueOf(value, currency)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: Currency): Geldbetrag {
            return valueOf(Geldbetrag(value, currency))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: CurrencyUnit): Geldbetrag {
            return valueOf(value, currency)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: CurrencyUnit): Geldbetrag {
            return valueOf(Geldbetrag(value, currency))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: String, monetaryContext: MonetaryContext): Geldbetrag {
            return valueOf(value, currency, monetaryContext)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: String, monetaryContext: MonetaryContext): Geldbetrag {
            return valueOf(value, Waehrung.of(currency), monetaryContext)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: CurrencyUnit, monetaryContext: MonetaryContext): Geldbetrag {
            return valueOf(value, currency, monetaryContext)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: CurrencyUnit, monetaryContext: MonetaryContext): Geldbetrag {
            return valueOf(de.jfachwert.money.Geldbetrag(value, currency, monetaryContext))
        }

        /**
         * Im Gegensatz zu valueOf wird hier keine [ArithmeticException]
         * geworfen, wenn Genauigkeit verloren geht. Stattdessen wird der
         * Wert gerundet.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         * @since 4.0
         */
        @JvmStatic
        fun roundedValueOf(value: Number, currency: CurrencyUnit, monetaryContext: MonetaryContext): Geldbetrag {
            val roundedValue = toBigDecimalRounded(value, monetaryContext)
            return valueOf(de.jfachwert.money.Geldbetrag(roundedValue, currency, monetaryContext))
        }

        /**
         * Erzeugt einen Geldbetrag anhand des uebergebenen Textes und mittels
         * des uebergebenen Formatters.
         *
         * @param text z.B. "12,25 EUR"
         * @param formatter Formatter
         * @return Geldbetrag
         */
        @JvmOverloads
        @JvmStatic
        fun parse(text: CharSequence?, formatter: MonetaryAmountFormat = DEFAULT_FORMATTER): Geldbetrag {
            return from(formatter.parse(text))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden of(..)-Methode und
         * wurde eingefuehrt, um mit der Money-Klasse aus "org.javamoney.moneta"
         * kompatibel zu sein.
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun from(other: MonetaryAmount): Geldbetrag {
            return of(other)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(other: MonetaryAmount): Geldbetrag {
            return valueOf(other)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf" .
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(other: MonetaryAmount): Geldbetrag {
            return valueOf(de.jfachwert.money.Geldbetrag.valueOf(other))
        }

        /**
         * Dient zur Konvertierung in einen Geldbetrag aus dem money-Package.
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(other: de.jfachwert.money.Geldbetrag): Geldbetrag {
            return Geldbetrag(other.number, other.currency)
        }

        /**
         * Validiert die uebergebene Zahl, ob sie sich als Geldbetrag eignet.
         *
         * @param zahl als String
         * @return die Zahl zur Weitervarabeitung
         */
        @JvmStatic
        fun validate(zahl: String): String {
            return VALIDATOR.validate(zahl)
        }

        private fun toBigDecimalRounded(value: Number, monetaryContext: MonetaryContext): BigDecimal {
            val n: BigDecimal = toBigDecimal(value)
            var roundingMode = monetaryContext.get(RoundingMode::class.java)
            if (roundingMode == null) {
                roundingMode = RoundingMode.HALF_UP
            }
            val scale = monetaryContext.maxScale
            return if (scale <= 0) {
                n
            } else {
                val scaled = n.setScale(scale, roundingMode)
                scaled
            }
        }

        private fun toBigDecimal(value: Number): BigDecimal {
            if (value is BigDecimal) {
                return value
            } else if (value is Zahlenwert) {
                return value.numberValue(BigDecimal::class.java)
            }
            return BigDecimal.valueOf(value.toDouble())
        }

    }

}
