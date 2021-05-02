/*
 * Copyright (c) 2021 by Oliver Boehm
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
 * (c)reated 01.05.21 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank.internal

import java.math.BigDecimal
import java.math.MathContext
import javax.money.NumberValue

/**
 * Diese Klasse wurde als Alternative zur DefaultNumberValue eingefuehrt.
 * Damit entfaellt die Abhaengigkeit zum 'org.javamoney.moneta.spi'-Paket
 * im moneta-bp-Modul.
 *
 * @since 4.0
 */
class Zahlenwert(val number: Number) : NumberValue() {

    /**
     * Returns the value of this number as a [Byte], which may involve rounding or truncation.
     */
    override fun toByte(): Byte {
        TODO("Not yet implemented")
    }

    /**
     * Returns the [Char] with the numeric value equal to this number, truncated to 16 bits if appropriate.
     */
    override fun toChar(): Char {
        TODO("Not yet implemented")
    }

    /**
     * Liefert den Zahlenwert als [Double] (evtl. gerundet).
     */
    override fun toDouble(): Double {
        return this.number.toDouble()
    }

    /**
     * Returns the value of this number as a [Float], which may involve rounding.
     */
    override fun toFloat(): Float {
        TODO("Not yet implemented")
    }

    /**
     * Returns the value of this number as an [Int], which may involve rounding or truncation.
     */
    override fun toInt(): Int {
        TODO("Not yet implemented")
    }

    /**
     * Returns the value of this number as a [Long], which may involve rounding or truncation.
     */
    override fun toLong(): Long {
        TODO("Not yet implemented")
    }

    /**
     * Returns the value of this number as a [Short], which may involve rounding or truncation.
     */
    override fun toShort(): Short {
        TODO("Not yet implemented")
    }

    /**
     * Get the numeric implementation type, that is the base of this number.
     *
     * @return the numeric implementation type, not `null`.
     */
    override fun getNumberType(): Class<*> {
        TODO("Not yet implemented")
    }

    /**
     * Returns the *precision* of this `MonetaryAmount`. (The precision is the number of
     * digits in the unscaled value.)
     *
     *
     *
     * The precision of a zero value is 1.
     *
     * @return the precision of this `MonetaryAmount`.
     */
    override fun getPrecision(): Int {
        TODO("Not yet implemented")
    }

    /**
     * Returns the *scale* of this `MonetaryAmount`. If zero or positive, the scale is
     * the number of digits to the right of the decimal point. If negative, the unscaled value of
     * the number is multiplied by ten to the power of the negation of the scale. For example, a
     * scale of `-3` means the unscaled value is multiplied by 1000.
     *
     * @return the scale of this `MonetaryAmount`.
     */
    override fun getScale(): Int {
        TODO("Not yet implemented")
    }

    /**
     * Liefert einen Zahlenwert als `int` so wie er ist.
     * Access the numeric value as `int`. Hereby no truncation will be performed to fit the
     * value into the target data type.
     *
     * @return Zahlenwert als `int`.
     * @throws ArithmeticException falls der Zahlenwert nicht in ein `int` passt.
     */
    override fun intValueExact(): Int {
        when (this.number) {
            is BigDecimal -> return this.number.intValueExact()
        }
        return this.number.toInt()
    }

    /**
     * Access the numeric value as `long`. Hereby no truncation will be performed to fit the
     * value into the target data type.
     *
     * @return the (possibly) truncated value of the [MonetaryAmount].
     * @throws ArithmeticException If the value must be truncated to fit the target datatype.
     */
    override fun longValueExact(): Long {
        TODO("Not yet implemented")
    }

    /**
     * Access the numeric value as `double`. Hereby no truncation will be performed to fit the
     * value into the target data type.
     *
     * @return the (possibly) truncated value of the [MonetaryAmount].
     * @throws ArithmeticException If the value must be truncated to fit the target datatype.
     */
    override fun doubleValueExact(): Double {
        TODO("Not yet implemented")
    }

    /**
     * Liefert den numerischen Wert als `Number`. Evtl. wird der Wert dabei
     * abgeschnitten, um in den Zieltyp zu passen.
     *
     * @param numberType Konkrete Number-Klasse, die zurueckgelieft wird.
     * Es werden folgende Number-Type unterstuezt:
     *
     *  * `java.lang.Long`
     *  * `java.lang.Double`
     *  * `java.lang.Number`
     *  * `java.math.BigInteger`
     *  * `java.math.BigDecimal`
     *
     * @return Zahlenwert eines [MonetaryAmount].
     */
    override fun <T : Number> numberValue(numberType: Class<T>): T {
        return this.number as T
    }

    /**
     * Access the current NumberValue rounded using the given [java.math.MathContext].
     *
     * @param mathContext the [java.math.MathContext] to be applied.
     * @return the new NumberValue, never null.
     * @see java.math.BigDecimal.round
     */
    override fun round(mathContext: MathContext?): NumberValue {
        TODO("Not yet implemented")
    }

    /**
     * Access the numeric value as `Number`. Hereby no truncation will be performed to fit the
     * value into the target data type.
     *
     * @param numberType The concrete number class to be returned. Basically the following Number types,
     * must be supported if available on the corresponding runtime platform:
     *
     *  * `java.lang.Long`
     *  * `java.lang.Double`
     *  * `java.lang.Number`
     *  * `java.math.BigInteger`, currently not available on all platforms.
     *  * `java.math.BigDecimal`, currently not available on all platforms.
     *
     * @return the (possibly) truncated value of the [MonetaryAmount].
     * @throws ArithmeticException If the value must be truncated to fit the target datatype.
     */
    override fun <T : Number?> numberValueExact(numberType: Class<T>?): T {
        TODO("Not yet implemented")
    }

    /**
     * This method allows to extract the numerator part of the current fraction, hereby given
     * <pre>`
     * w = longValue()
     * n = getFractionNominator()
     * d = getFractionDenominator()
    `</pre> *
     *
     * the following must be always true:
     *
     * <pre>`
     * !(w<0 && n>0)  and
     * !(w>0 && n<0)  and
     * d>0            and
     * |n| < d        // || = absolute value
    `</pre> * .
     *
     * @return the amount's fraction numerator..
     */
    override fun getAmountFractionNumerator(): Long {
        TODO("Not yet implemented")
    }

    /**
     * This method allows to extract the denominator part of the current fraction, hereby given
     * <pre>`
     * w = longValue()
     * n = getFractionNominator()
     * d = getFractionDenominator()
    `</pre> *
     *
     * the following must be always true:
     *
     * <pre>`
     * !(w<0 && n>0)  and
     * !(w>0 && n<0)  and
     * d>0            and
     * |n| < d        // || = absolute value
    `</pre> * .
     *
     * @return the amount's fraction denominator.
     */
    override fun getAmountFractionDenominator(): Long {
        TODO("Not yet implemented")
    }

}