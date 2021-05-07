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

import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import java.math.BigDecimal
import java.math.BigInteger
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
     * Liefert den Zahlenwert als [Byte] (evtl. gerundet).
     */
    override fun toByte(): Byte {
        return toBigDecimal().toByte()
    }

    /**
     * Liefert den Zahlenwert als [Char] (evtl. gerundet).
     */
    override fun toChar(): Char {
        return toBigDecimal().toChar()
    }

    /**
     * Liefert den Zahlenwert als [Double] (evtl. gerundet).
     */
    override fun toDouble(): Double {
        return this.number.toDouble()
    }

    /**
     * Liefert den Zahlenwert als [Float] (evtl. gerundet).
     */
    override fun toFloat(): Float {
        return toBigDecimal().toFloat()
    }

    /**
     * Liefert den Zahlenwert als [Int] (evtl. gerundet oder abgeschnitten).
     */
    override fun toInt(): Int {
        return toBigDecimal().toInt()
    }

    /**
     * Liefert den Zahlenwert als [Long] (evtl. gerundet oder abgeschnitten).
     */
    override fun toLong(): Long {
        return toBigDecimal().toLong()
    }

    /**
     * Liefert den Zahlenwert als [Short] (evtl. gerundet oder abgeschnitten).
     */
    override fun toShort(): Short {
        return toBigDecimal().toShort()
    }

    fun toBigDecimal(): BigDecimal {
        when (this.number) {
            is BigDecimal -> return this.number
        }
        return BigDecimal(this.number.toString())
    }

    /**
     * Liefert den Typ des Zahlenwerts.
     *
     * @return Typ des Zahlenwerts, nicht `null`.
     */
    override fun getNumberType(): Class<*> {
        return number.javaClass
    }

    /**
     * Liefert die *Praezision* eines `MonetaryAmount` (Anzahl Ziffern).
     *
     * @return *Praezision* eines `MonetaryAmount`.
     */
    override fun getPrecision(): Int {
        return toBigDecimal().precision()
    }

    /**
     * Liefert die *Skala* des Zahlenwerts. Bei positiven Zahlen (oder 0) ist dies
     * die Anzahl der Nachkommastellen. Bei negativen Zahlen ist dies der Exponent,
     * mit dem der Wert multipliziert wird. Dh.h eine Skala von `-3` bedeuted eine
     * Multiplikation mit 1000.
     *
     * @return Skala von `MonetaryAmount`.
     */
    override fun getScale(): Int {
        return toBigDecimal().scale()
    }

    /**
     * Liefert einen Zahlenwert als `int` so wie er ist.
     *
     * @return Zahlenwert als `int`.
     * @throws ArithmeticException falls der Zahlenwert nicht in ein `int` passt.
     */
    override fun intValueExact(): Int {
        return toBigDecimal().intValueExact()
    }

    /**
     * Liefert einen Zahlenwert als `long` so wie er ist.
     *
     * @return Zahlenwert als `long`.
     * @throws ArithmeticException falls der Zahlenwert nicht in ein `long` passt.
     */
    override fun longValueExact(): Long {
        return toBigDecimal().longValueExact()
    }

    /**
     * Liefert einen Zahlenwert als `double` so wie er ist.
     *
     * @return Zahlenwert als `double`.
     * @throws ArithmeticException falls der Zahlenwert nicht in ein `double` passt.
     */
    override fun doubleValueExact(): Double {
        return toBigDecimal().toDouble()
    }

    /**
     * Liefert den numerischen Wert als `Number`. Evtl. wird der Wert dabei
     * abgeschnitten, um in den Zieltyp zu passen.
     *
     * @param numberType Konkrete Number-Klasse, die zurueckgelieft wird.
     * Es werden folgende Number-Typen unterstuezt:
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
        when (numberType) {
            java.lang.Short::class.java -> return number.toShort() as T
            java.lang.Integer::class.java -> return number.toInt() as T
            java.lang.Long::class.java -> return number.toLong() as T
            java.lang.Float::class.java -> return number.toFloat() as T
            java.lang.Double::class.java -> return number.toDouble() as T
            java.lang.Byte::class.java -> return number.toByte() as T
            java.math.BigInteger::class.java -> return BigInteger(number.toString()) as T;
        }
        return this.number as T
    }

    /**
     * Liefert den aktuellen Zahlenwert gemaess [java.math.MathContext]
     * gerundet zurueck.
     *
     * @param mathContext [java.math.MathContext] mit Rundungs-Mode
     * @return gerunderter Zahlenwert (nicht null).
     * @see java.math.BigDecimal.round
     */
    override fun round(mathContext: MathContext?): NumberValue {
        return Zahlenwert(toBigDecimal().round(mathContext))
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
    override fun <T : Number> numberValueExact(numberType: Class<T>): T {
        val valueExact = this.numberValue(numberType)
        if (toBigDecimal().toString().equals(valueExact.toString())) {
            return valueExact
        } else {
            throw LocalizedIllegalArgumentException(number, "data_type $numberType")
        }
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