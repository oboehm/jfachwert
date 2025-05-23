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
 * (c)reated 12.10.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.money.internal

import de.jfachwert.Text
import de.jfachwert.money.Geldbetrag
import de.jfachwert.money.Geldbetrag.Companion.of
import de.jfachwert.money.Waehrung
import de.jfachwert.money.Waehrung.Companion.toCurrency
import de.jfachwert.money.pruefung.exception.LocalizedMonetaryParseException
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.NumberValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.ValidationException
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*
import javax.money.CurrencyUnit
import javax.money.MonetaryAmount
import javax.money.MonetaryAmountFactory
import javax.money.format.AmountFormatContext
import javax.money.format.AmountFormatContextBuilder
import javax.money.format.MonetaryAmountFormat
import javax.money.format.MonetaryParseException

/**
 * Der GeldbetragFormatter ist fuer die Formattierung und Parsen von
 * Geldbetraegen zustaendig.
 *
 * @author oliver (ob@aosd.de)
 * @since 1.0.1 (12.10.18)
 */
class GeldbetragFormatter private constructor(
    private val context: AmountFormatContext,
    private val pattern: String = "#.##"  + '\u00A0' + "$$$") : MonetaryAmountFormat {

    companion object {

        private val MAPPED_LOCALES: MutableMap<Locale, Locale> = HashMap()
        private val DEFAULT_CONTEXT = AmountFormatContextBuilder.of("jfachwert")
            .setLocale(Locale.getDefault()).build()

        @JvmStatic
        fun of(locale: Locale): GeldbetragFormatter {
            val mapped: Locale = MAPPED_LOCALES.getOrDefault(locale, locale)
            val context = AmountFormatContextBuilder.of("jfachwert").setLocale(mapped).build()
            return of(context)
        }

        @JvmStatic
        fun of(context: AmountFormatContext): GeldbetragFormatter {
            return GeldbetragFormatter(context)
        }

        @JvmStatic
        fun of(pattern: String): GeldbetragFormatter {
            return GeldbetragFormatter(DEFAULT_CONTEXT, pattern)
        }

        private fun findCurrencyString(parts: Array<String>): String {
            parts[0].matches(Regex("[0-9]+"))
            if (!StringUtils.isNumericSpace(parts[0]) && !parts[0].matches(Regex("[+-]+"))) {
                return parts[0]
            }
            return if (!StringUtils.isNumericSpace(parts[parts.size - 1])) {
                parts[parts.size - 1]
            } else ""
        }

        init {
            MAPPED_LOCALES[Locale("de_DE")] = Locale.GERMANY
        }

    }

    constructor() : this(Locale.getDefault()) {}
    private constructor(locale: Locale) : this(AmountFormatContextBuilder.of("jfachwert").setLocale(locale).build()) {}

    /**
     * Der [AmountFormatContext], der normalerweise fuer die
     * Formattierung verwendet wird. Wird aber von dieser Klasse
     * (noch) nicht intern verwendet.
     *
     * @return Context
     */
    override fun getContext(): AmountFormatContext {
        return context
    }

    /**
     * Gibt lediglich den Geldbetrag aus.
     *
     * Beispiele fuer `Appendable` sind `StringBuilder`, `StringBuffer`
     * oder `Writer`, wobei nur letzteres eine `IOException` verursachen kann.
     *
     * @param appendable z.B. einen StringBuilder
     * @param amount     Geldbetrag
     * @throws IOException kann bei einem `Writer` auftreten
     */
    @Throws(IOException::class)
    override fun print(appendable: Appendable, amount: MonetaryAmount) {
        val currency = amount.currency
        val fractionDigits = getFractionDigits(currency)
        val currencyString = getCurrencyString(currency)
        synchronized(context) {
            val formatter = getFormatter(context.locale)
            formatter.minimumFractionDigits = fractionDigits
            formatter.maximumFractionDigits = fractionDigits
            var s = formatter.format(amount.number)
            s = pattern.replace("#\\.?#*".toRegex(), s)
            s = s.replace("$$$", "$").replace("$", currencyString)
            appendable.append(s)
        }
    }

    private fun getFractionDigits(currency: CurrencyUnit) : Int {
        val numberPattern = pattern.replace("[^#\\.]".toRegex(), "")
        if (numberPattern.contains('.')) {
            val fractionDigits = numberPattern.substringAfter('.').length
            return fractionDigits
        } else {
            return 0
        }
    }

    private fun getCurrencyString(cu: CurrencyUnit): String {
        if (pattern.contains("$$$")) {
            return cu.currencyCode
        } else {
            val currency = Currency.getInstance(cu.currencyCode)
            return currency.getSymbol(context.locale)
        }
    }

    private fun getFormatter(locale: Locale): NumberFormat {
        var formatter = NumberFormat.getInstance(locale)
        val s = formatter.format(0)
        if ("0" != s) {
            formatter = NumberFormat.getInstance()
        }
        return formatter
    }

    /**
     * Wandelt den Text in einen [Geldbetrag] um.
     *
     * @param text Text, z.B. "2,50 EUR"
     * @return Geldbetrag (niemals `null`)
     * @throws MonetaryParseException falls der Text kein Geldbetrag darstellt
     */
    @Throws(MonetaryParseException::class)
    override fun parse(text: CharSequence): MonetaryAmount {
        return parse(Objects.toString(text))
    }

    @Throws(MonetaryParseException::class)
    private fun parse(text: String): MonetaryAmount {
        var trimmed = NullValidator<String>().validate(text).trim { it <= ' ' }
        val parts = StringUtils.splitByCharacterType(StringUtils.upperCase(trimmed))
        if (parts.size == 0) {
            throw InvalidValueException(text, "money amount")
        }
        var cry = Waehrung.DEFAULT_CURRENCY
        val currencyString = findCurrencyString(parts)
        return try {
            trimmed = Text.trim(StringUtils.remove(trimmed, currencyString))
            val n = BigDecimal(NumberValidator().validate(trimmed))
            if (StringUtils.isNotEmpty(currencyString)) {
                cry = toCurrency(currencyString)
            }
            getMonetaryAmount(cry, n)
        } catch (ex: IllegalArgumentException) {
            throw LocalizedMonetaryParseException(text, ex)
        } catch (ex: ValidationException) {
            throw LocalizedMonetaryParseException(text, ex)
        }
    }

    private fun getMonetaryAmount(cry: Currency, n: BigDecimal): MonetaryAmount {
        val amountFactory = context.get(MonetaryAmountFactory::class.java)
        return if (amountFactory == null) {
            of(n, cry)
        } else {
            amountFactory.setNumber(n).setCurrency(cry.toString()).create()
        }
    }

    /**
     * Wandelt im Wesentlichen den uebergebenen Geldbetrag in seine String-
     * Darstellung um.
     *
     * @param amount Geldbetrag
     * @return Geldbetrag als String
     */
    override fun queryFrom(amount: MonetaryAmount): String {
        return Objects.toString(amount)
    }

    override fun toString(): String {
        return this.javaClass.simpleName + "(" + context.locale + ")"
    }

}