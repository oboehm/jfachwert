/*
 * Copyright (c) 2019, 2020 by Oliver Boehm
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
 * (c)reated 02.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.math

import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.util.*

/**
 * Die Klasse Prozent steht fuer den Tausendsten Teil einer Zahl.
 * Sie kann wie die [Prozent]-Klasse fuer Berechnungen eingesetzt
 * werden.
 *
 * @author oboehm
 * @since 3.0 (02.10.2019)
 */
open class Promille : Prozent {

    /**
     * Legt ein Promille-Objekt an.
     *
     * @param wert Promille-Wert, z.B. "10" fuer 10 °/oo
     */
    constructor(wert: String) : this(toNumber(wert)) {}

    /**
     * Legt ein Promille-Objekt an.
     *
     * @param wert Promille-Wert, z.B. 10 fuer 10 °/oo
     */
    constructor(wert: Long) : super(wert) {}

    /**
     * Legt ein Promille-Objekt an.
     *
     * @param wert Promille-Wert, z.B. 10 fuer 10 °/oo
     */
    constructor(wert: BigDecimal) : super(wert) {}

    /**
     * Diese Methode liefert den mathematischen Wert als BigDecimal zurueck,
     * mit dem dann weitergerechnet werden kann. D.h. 1 Promille wird dann als
     * '0.001' zurueckgegeben.
     *
     * @return die Zahl als [BigDecimal]
     */
    override fun toBigDecimal(): BigDecimal {
        return wert.divide(BigDecimal.valueOf(1000))
    }

    override fun toString(): String {
        return wert.toString() + Character.toString(PROMILLE_ZEICHEN)
    }



    companion object {

        private val WEAK_CACHE = WeakHashMap<BigDecimal, Promille>()
        private const val PROMILLE_ZEICHEN = '\u2030'

        /** Konstante fuer "0 Promille".  */
        @JvmField
        val ZERO = of(BigDecimal.ZERO)

        /** Konstante fuer "1 Promille".  */
        @JvmField
        val ONE = of(BigDecimal.ONE)

        /** Konstante fuer "10 Promillle".  */
        @JvmField
        val TEN = of(BigDecimal.TEN)

        /**
         * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
         * Diese Methode lohnt sich daher, wenn man immer denselben Promille-Wert
         * erzeugen will, um die Anzahl der Objekte gering zu halten.
         *
         * @param wert z.B. "0.8"
         * @return "0.8 °/oo" als Promille-Objekt
         */
        @JvmStatic
        fun of(wert: String): Promille {
            return of(toNumber(wert))
        }

        private fun toNumber(s: String): BigDecimal {
            val number = StringUtils.replaceChars(s, "°/o$PROMILLE_ZEICHEN", "").trim { it <= ' ' }
            return BigDecimal(number)
        }

        /**
         * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
         * Diese Methode lohnt sich daher, wenn man immer denselben Promille-Wert
         * erzeugen will, um die Anzahl der Objekte gering zu halten.
         *
         * @param wert z.B. 2
         * @return "2 °/oo" als Promille-Objekt
         */
        @JvmStatic
        fun of(wert: Long): Promille {
            return of(BigDecimal.valueOf(wert))
        }

        /**
         * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
         * Diese Methode lohnt sich daher, wenn man immer denselben Promille-Wert
         * erzeugen will, um die Anzahl der Objekte gering zu halten.
         *
         * @param wert z.B. 0.8
         * @return "0.8 °/oo" als Promille-Objekt
         */
        @JvmStatic
        fun of(wert: BigDecimal): Promille {
            val copy = BigDecimal(wert.toString())
            return WEAK_CACHE.computeIfAbsent(copy) { w: BigDecimal -> Promille(BigDecimal(w.toString())) }
        }

    }

}