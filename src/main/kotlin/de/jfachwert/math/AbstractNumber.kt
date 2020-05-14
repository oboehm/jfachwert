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
     * Dient zum Vergleich zweier Zahlen.
     *
     * @param other die andere Zahl
     * @return Abstand zur anderen Zahl
     * @since 3.0
     */
    override fun compareTo(other: AbstractNumber): Int {
        return this.toBigDecimal().compareTo(other.toBigDecimal())
    }
}