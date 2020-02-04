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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 30.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank

import de.jfachwert.bank.Geldbetrag
import de.jfachwert.pruefung.exception.LocalizedMonetaryException
import java.math.BigDecimal
import java.math.RoundingMode
import javax.money.*

/**
 * Analog zu den anderen [Monetary]-Datentype kann mit dieser Factory
 * ein [Geldbetrag] erzeugt und vorblegt werden.
 *
 * @author oboehm
 * @since 1.0 (30.07.2018)
 */
class GeldbetragFactory : MonetaryAmountFactory<Geldbetrag> {

    private var number: Number = BigDecimal.ZERO
    private var currency: CurrencyUnit? = null
    private var context = MonetaryContextBuilder.of(Geldbetrag::class.java).setAmountType(Geldbetrag::class.java).setPrecision(41).setMaxScale(4)
            .set(RoundingMode.HALF_UP).build()

    /**
     * Liefert den [MonetaryAmount] Implementierungstyp.
     *
     * @return die Klasse [Geldbetrag]
     */
    override fun getAmountType(): Class<out MonetaryAmount?> {
        return Geldbetrag::class.java
    }

    /**
     * Setzt die [CurrencyUnit].
     *
     * @param currency [CurrencyUnit], nicht `null`
     * @return die Factory selber
     */
    override fun setCurrency(currency: CurrencyUnit): GeldbetragFactory {
        this.currency = currency
        return this
    }

    /**
     * Setzt die Nummer fuer den Geldbetrag.
     *
     * @param number Betrag, darf nicht `null` sein.
     * @return die Factory selber
     */
    override fun setNumber(number: Double): GeldbetragFactory {
        return this.setNumber(BigDecimal.valueOf(number))
    }

    /**
     * Setzt die Nummer fuer den Geldbetrag.
     *
     * @param number Betrag, darf nicht `null` sein.
     * @return die Factory selber
     */
    override fun setNumber(number: Long): GeldbetragFactory {
        return setNumber(BigDecimal.valueOf(number))
    }

    /**
     * Setzt die Nummer fuer den Geldbetrag.
     *
     * @param number Betrag, darf nicht `null` sein.
     * @return die Factory selber
     */
    override fun setNumber(number: Number): GeldbetragFactory {
        this.number = number
        context = getMonetaryContextOf(number)
        return this
    }

    /**
     * Ermittelt den [MonetaryContext] der uebergebenen Nummer. Laesst er
     * sich nicht ermitteln, wird der voreigestellte [MonetaryContext]
     * zurueckgeliefert.
     *
     * @param number eine Zahl, z.B. 8.15
     * @return den Kontext, der mit dieser Zahl verbunden ist (wie z.B.
     * 2 Nachkommastellen, ...)
     */
    fun getMonetaryContextOf(number: Number?): MonetaryContext {
        if (number is BigDecimal) {
            val value = number
            if (value.scale() > context.maxScale) {
                return MonetaryContextBuilder.of(Geldbetrag::class.java)
                        .setAmountType(Geldbetrag::class.java)
                        .setPrecision(context.precision)
                        .setMaxScale(value.scale())
                        .set(RoundingMode.HALF_UP).build()
            }
        }
        return context
    }

    /**
     * Liefert die Maximal-Nummer, die der [Geldbetrag] darstellen kann.
     *
     * @return Maximal-Betrag
     */
    override fun getMaxNumber(): NumberValue {
        return Geldbetrag.MAX_VALUE.number
    }

    /**
     * Liefert die Minimal-Nummer, die der [Geldbetrag] darstellen kann.
     *
     * @return Minimal-Betrag
     */
    override fun getMinNumber(): NumberValue {
        return Geldbetrag.MIN_VALUE.number
    }

    /**
     * Sets the [MonetaryContext] to be used.
     *
     * @param monetaryContext the [MonetaryContext] to be used, not `null`.
     * @return This factory instance, for chaining.
     * @throws MonetaryException when the [MonetaryContext] given exceeds the capabilities supported by this
     * factory type.
     * @see MonetaryAmountFactory.getMaximalMonetaryContext
     */
    override fun setContext(monetaryContext: MonetaryContext): GeldbetragFactory {
        context = monetaryContext
        return this
    }

    /**
     * Erzeugt einen neuen [Geldbetrag] anhand der eingestellten Daten.
     *
     * @return den entsprechenden [Geldbetrag].
     * @see MonetaryAmountFactory.getAmountType
     */
    override fun create(): Geldbetrag {
        if (currency == null) {
            throw LocalizedMonetaryException("currency missing", number)
        }
        return Geldbetrag.valueOf(number, currency, context)
    }

    /**
     * In der Standardeinstellung liefert der [MonetaryContext] einen
     * Wertbereich fuer den Geldbetrag von [Geldbetrag.MIN_VALUE] bis
     * [Geldbetrag.MAX_VALUE].
     *
     * @return den Default-[MonetaryContext].
     * @see MonetaryAmountFactory.getMaximalMonetaryContext
     */
    override fun getDefaultMonetaryContext(): MonetaryContext {
        return context
    }

    /**
     * Der maximale [MonetaryContext] schraenkt den Wertebereich eines
     * Geldbetrags nicth ein. D.h. es gibt keine obere und untere Grenze.
     *
     * @return maximaler [MonetaryContext].
     */
    override fun getMaximalMonetaryContext(): MonetaryContext {
        return MAX_CONTEXT
    }

    companion object {
        @JvmField
        val MAX_CONTEXT = MonetaryContextBuilder.of(Geldbetrag::class.java).setAmountType(Geldbetrag::class.java).setPrecision(0).setMaxScale(-1)
                .set(RoundingMode.HALF_UP).build()
    }

}