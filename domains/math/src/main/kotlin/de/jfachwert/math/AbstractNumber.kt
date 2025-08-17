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
 * (c)reated 12.04.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.math

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.jfachwert.math.internal.ToNumberSerializer

import java.math.BigDecimal
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale
import java.util.logging.Logger

/**
 * In dieser Klasse sind die gemeinsame Implementierung der abstrakten
 * Methoden der [Number]-Klasse zusammengefasst. Diese Klasse
 * wurde eingezogen, um Code-Duplikate zu vermeiden.
 *
 * @author ob@aosd.de
 * @since 0.7
 */
@JsonSerialize(using = ToNumberSerializer::class)
abstract class AbstractNumber : Number(), Comparable<AbstractNumber> {

    /**
     * Diese Methode liefert die Zahl als BigDecimal zurueck und wird fuer
     * die Default-Implementierung der Number-Methoden benoetigt.
     *
     * @return die Zahl als [BigDecimal]
     */
    abstract fun toBigDecimal(): BigDecimal

    /**
     * Liefert die Zahl als ein `int` (gerundet) zurueck.
     *
     * @return den numerischen Wert als `int`
     * @since 0.7
     */
    override fun toInt(): Int {
        return toBigDecimal().toInt()
    }

    /**
     * Liefert die Zahl als ein `long` (gerundet) zurueck.
     *
     * @return den numerischen Wert als `long`
     * @since 0.7
     */
    override fun toLong(): Long {
        return toBigDecimal().toLong()
    }

    /**
     * Liefert die Zahl als ein `float` zurueck.
     *
     * @return den numerischen Wert als `float`
     * @since 0.7
     */
    override fun toFloat(): Float {
        return toBigDecimal().toFloat()
    }

    /**
     * Liefert die Zahl als ein `double` zurueck.
     *
     * @return den numerischen Wert als `double`
     * @since 0.7
     */
    override fun toDouble(): Double {
        return toBigDecimal().toDouble()
    }

    /**
     * Liefert die Zahl als ein `Byte` zurueck.
     *
     * @return den numerischen Wert als `Byte`
     */
    override fun toByte(): Byte {
        return toBigDecimal().toByte()
    }

    /**
     * Liefert die Zahl als ein `Char` zurueck.
     *
     * @return den numerischen Wert als `Char`
     */
    override fun toChar(): Char {
        return toInt().toChar()
    }

    /**
     * Liefert die Zahl als ein `Short` zurueck.
     *
     * @return den numerischen Wert als `Short`
     */
    override fun toShort(): Short {
        return toBigDecimal().toShort()
    }

    /**
     * Dient zum Vergleich zweier Zahlen.
     *
     * @param other die andere Zahl
     * @return Abstand zur anderen Zahl
     * @since 3.0
     */
    override fun compareTo(other: AbstractNumber): Int {
        return this.toBigDecimal().compareTo(other.toBigDecimal())
    }



    companion object {

        private val log = Logger.getLogger(AbstractNumber::class.java.name)

        fun toBigDecimal(number: String): BigDecimal {
            try {
                return BigDecimal(number)
            } catch (nfe: NumberFormatException) {
                log.fine("'$number' is not a number, trying it with another locale: $nfe")
                if (number.contains(",")) {
                    return toBigDecimal(number.replace(',', '.'))
                }
                try {
                    val format = NumberFormat.getInstance(Locale.getDefault())
                    return BigDecimal(format.parse(number).toString())
                } catch (ex: ParseException) {
                    nfe.initCause(ex)
                    throw nfe
                }
            }
        }

    }

}