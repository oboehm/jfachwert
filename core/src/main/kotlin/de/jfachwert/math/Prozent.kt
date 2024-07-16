/*
 * Copyright (c) 2019-2024 by Oliver Boehm
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
 * (c)reated 01.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.math

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.KFachwert
import java.math.BigDecimal
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.money.MonetaryAmount

/**
 * Die Klasse Prozent steht fuer den Hundersten Teil einer Zahl.
 * Sie kann wie jede andere Zahl fuer Berechnungen eingesetzt werden,
 * weswegen sie auch von [java.lang.Number] abgeleitet ist.
 *
 * Soweit moeglich und sinnvoll wurden die mathematischen Operationen
 * von BigDecimal uebernommen. So gibt es fuer die Multiplikation eine
 * [.multiply]-Methode. Auch gibt es Konstanten ZERO, ONE und TEN.
 *
 * @author oboehm
 * @since 3.0 (01.10.2019)
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Prozent(val wert: BigDecimal) : AbstractNumber(), KFachwert {

    /**
     * Legt ein Prozent-Objekt an.
     *
     * @param wert Prozentwert, z.B. "10" fuer 10 %
     */
    constructor(wert: String) : this(toNumber(wert)) {}

    /**
     * Legt ein Prozent-Objekt an.
     *
     * @param wert Prozentwert, z.B. 10 fuer 10 %
     */
    constructor(wert: Long) : this(BigDecimal.valueOf(wert)) {}

    /**
     * Diese Methode liefert den mathematischen Wert als BigDecimal zurueck,
     * mit dem dann weitergerechnet werden kann. D.h. 19% wird dann als '0.19'
     * zurueckgegeben.
     *
     * @return die Zahl als [BigDecimal]
     */
    override fun toBigDecimal(): BigDecimal {
        return wert.divide(BigDecimal.valueOf(100))
    }

    /**
     * Fuehrt eine einfache Prozent-Rechnung aus. D.h. '10% * 42 = 4.2'.
     *
     * @param x Multiplikant
     * @return x * Prozentwert / 100
     */
    fun multiply(x: BigDecimal): BigDecimal {
        return x.multiply(toBigDecimal())
    }

    /**
     * Fuehrt eine einfache Prozent-Rechnung aus. Dieses Mal aber mit
     * Geldbetraegen.
     *
     * @param geldbetrag z.B. "10 EUR"
     * @return Prozentwert des Geldbetrags
     * @since 4.0
     */
    fun multiply(geldbetrag: MonetaryAmount): MonetaryAmount {
        return geldbetrag.multiply(toBigDecimal())
    }

    /**
     * Fuehrt eine einfache Prozent-Rechnung aus. D.h. '10% * 42 = 4.2'.
     *
     * @param x Multiplikant
     * @return x * Prozentwert / 100
     */
    fun multiply(x: Long): BigDecimal {
        return multiply(BigDecimal.valueOf(x))
    }

    override fun toString(): String {
        return wert.toString() + "%"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Prozent) {
            return false
        }
        return wert == other.wert
    }

    override fun hashCode(): Int {
        return wert.hashCode()
    }



    companion object {

        private val log = Logger.getLogger(Prozent::class.java.name)
        private val WEAK_CACHE = WeakHashMap<BigDecimal, Prozent>()

        /** Konstante fuer "0%".  */
        @JvmField
        val ZERO = of(BigDecimal.ZERO)

        /** Konstante fuer "1%".  */
        @JvmField
        val ONE = of(BigDecimal.ONE)

        /** Konstante fuer "10%".  */
        @JvmField
        val TEN = of(BigDecimal.TEN)

        private fun toNumber(wert: String): BigDecimal {
            val number = wert.split("%").toTypedArray()[0].trim { it <= ' ' }
            return try {
                BigDecimal(number)
            } catch (ex: NumberFormatException) {
                log.log(Level.FINE, "$number ist keine normale Nummer.")
                log.log(Level.FINER, "Details:", ex)
                BigDecimal(number.replace(',', '.'))
            }
        }

        /**
         * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
         * Diese Methode lohnt sich daher, wenn man immer denselben Prozent-Wert
         * erzeugen will, um die Anzahl der Objekte gering zu halten.
         *
         * @param wert z.B. "19%"
         * @return "19%" als Prozent-Objekt
         */
        @JvmStatic
        fun of(wert: String): Prozent {
            return of(toNumber(wert))
        }

        /**
         * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
         * Diese Methode lohnt sich daher, wenn man immer denselben Prozent-Wert
         * erzeugen will, um die Anzahl der Objekte gering zu halten.
         *
         * @param wert z.B. "19%"
         * @return "19%" als Prozent-Objekt
         */
        @JvmStatic
        fun of(wert: Long): Prozent {
            return of(BigDecimal.valueOf(wert))
        }

        /**
         * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
         * Diese Methode lohnt sich daher, wenn man immer denselben Prozent-Wert
         * erzeugen will, um die Anzahl der Objekte gering zu halten.
         *
         * @param wert z.B. 19
         * @return "19%" als Prozent-Objekt
         */
        @JvmStatic
        fun of(wert: BigDecimal): Prozent {
            return WEAK_CACHE.computeIfAbsent(wert) { w: BigDecimal -> Prozent(w) }
        }

    }

}