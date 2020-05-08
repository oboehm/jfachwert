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
 * (c)reated 02.04.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.math

import de.jfachwert.Fachwert
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Die Bruch-Klasse repraesentiert eine mathematischen Bruch mit Zaehler und
 * Nenner. Als Zaehler und Nenner werden dabei nur ganzzahlige Werte
 * akzeptiert, da sich Gleitkommazahlen auch immer als Brueche darstellen
 * lassen.
 *
 * Die Namen der Methoden orientieren sich teilweise an den Methodennamen von
 * BigInteger und BigDecimal und sind daher auf englisch. Andere Namen wie
 * [.kuerzen] sind dagegen auf deutsch.
 *
 * @author ob@aosd.de
 * @since 0.6
 */
open class Bruch(val zaehler: BigInteger, val nenner: BigInteger) : AbstractNumber(), Fachwert {

    /**
     * Legt einen Bruch mit dem angegeben Zaehler und Nenner an. Brueche
     * koennen dabei mit Bruchstrich ("1/2") oder als Dezimalzahl ("0.5")
     * angegeben werden.
     *
     * @param bruch Zeichenkette, z.B. "1/2" ocer "0.5"
     */
    constructor(bruch: String) : this(toNumbers(bruch)) {}

    /**
     * Legt die uebergebene Gleitkommazahl als Bruch an.
     *
     * @param number Dezimalzahl, z.B. 0.5
     */
    constructor(number: Double) : this(BigDecimal.valueOf(number)) {}

    /**
     * Legt die uebergebene Dezimalzahl als Bruch an.
     *
     * @param decimal Dezimalzahl, z.B. 0.5
     */
    constructor(decimal: BigDecimal) : this(toNumbers(decimal)) {}

    private constructor(number: Array<BigInteger>) : this(number[0], number[1]) {}

    /**
     * Legt einen Bruch mit dem angegeben Zaehler und Nenner an.
     *
     * @param zaehler Zaehler
     * @param nenner Nenner
     */
    constructor(zaehler: Long, nenner: Long) : this(BigInteger.valueOf(zaehler), BigInteger.valueOf(nenner)) {}

    /**
     * Liefert einen gekuerzten Bruch zurueck. So wird z.B. der Bruch "2/4" als
     * "1/2" zurueckgegeben.
     *
     * @return gekuerzter Bruch
     */
    fun kuerzen(): Bruch {
        var z = zaehler
        var n = nenner
        var p = Primzahl.first()
        while (p.toBigInteger().compareTo(n) < 0) {
            val teiler = p.toBigInteger()
            while (z.mod(teiler) == BigInteger.ZERO && n.mod(teiler) == BigInteger.ZERO) {
                z = z.divide(teiler)
                n = n.divide(teiler)
            }
            p = p.next()
        }
        return of(z, n)
    }

    /**
     * Liefert den (ungekuerzten) Kehrwert des Bruches.
     *
     * @return Kehrwert
     */
    fun kehrwert(): Bruch {
        return of(nenner, zaehler)
    }

    /**
     * Liefert einen (ungekuerzten) negierten Kehrwert zurueck.
     *
     * @return negierter Bruch
     */
    fun negate(): Bruch {
        return of(zaehler.negate(), nenner)
    }

    /**
     * Multiplikation zweier Brueche.
     *
     * @param operand der zweite Bruch, mit dem multipliziert wird.
     * @return mulitiplizierter Bruch, evtl. gekuerzt
     */
    fun multiply(operand: Bruch): AbstractNumber {
        val z = zaehler.multiply(operand.zaehler)
        val n = nenner.multiply(operand.nenner)
        return of(z, n).kuerzen()
    }

    /**
     * Die Division zweier Brueche laesst sich bekanntlich auf die
     * Multiplikation des Bruches mit dem Kehrwert zurueckfuehren.
     * Dies wird hier fuer die Division ausgenutzt.
     *
     * @param operand der zweite Bruch, durch den geteilt wird
     * @return dividierter Bruch, evtl. gekuerzt
     */
    fun divide(operand: Bruch): AbstractNumber {
        return multiply(operand.kehrwert())
    }

    /**
     * Addition zweier Brueche.
     *
     * @param operand der zweite Bruch, der addiert wird.
     * @return addierter Bruch, evtl. gekuerzt
     */
    fun add(operand: Bruch): AbstractNumber {
        val n = nenner.multiply(operand.nenner)
        val z1 = zaehler.multiply(operand.nenner)
        val z2 = operand.zaehler.multiply(nenner)
        return of(z1.add(z2), n).kuerzen()
    }

    /**
     * Subtraktion zweier Brueche.
     *
     * @param operand der zweite Bruch, der subtrahiert wird.
     * @return subtrahierter Bruch, evtl. gekuerzt
     */
    fun subtract(operand: Bruch): AbstractNumber {
        return add(operand.negate())
    }

    override fun toString(): String {
        return zaehler.toString() + "/" + nenner
    }

    /**
     * Fuer die equals-Implementierung gilt, dass der Wert verglichen wird.
     * D.h. bei "1/2" und "2/4" handelt es sich um die gleichen Brueche.
     *
     * @param other der andere Bruch
     * @return true oder false
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Bruch) {
            return false
        }
        val bruch = other.kuerzen()
        val gekuerzt = kuerzen()
        return gekuerzt.zaehler == bruch.zaehler && gekuerzt.nenner == bruch.nenner
    }

    /**
     * Eine einfache Hashcode-Implementierung, die sich auf toString() abstuetzt.
     *
     * @return hashcode
     */
    override fun hashCode(): Int {
        return kuerzen().toString().hashCode()
    }

    /**
     * Vergleicht eine andere Zahl mit dem aktuellen Bruch.
     *
     * @param other Zahl, mit der verglichen wird
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     */
    override fun compareTo(other: AbstractNumber): Int {
        return if (other is Bruch) {
            compareTo(other)
        } else {
            super.compareTo(other)
        }
    }

    /**
     * Vergleicht den anderen Bruch mit dem aktuellen Bruch.
     *
     * @param other der andere Bruch, der verglichen wird.
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     */
    operator fun compareTo(other: Bruch): Int {
        val thisZaehlerErweitert = zaehler.multiply(other.nenner)
        val otherZaehlerErweitert = other.zaehler.multiply(nenner)
        return thisZaehlerErweitert.compareTo(otherZaehlerErweitert)
    }

    /**
     * Liefert den Bruch als [BigDecimal] zurueck.
     *
     * @return Bruch als [BigDecimal]
     * @since 0.7
     */
    override fun toBigDecimal(): BigDecimal {
        return BigDecimal(zaehler).divide(BigDecimal(nenner))
    }

    override fun toByte(): Byte {
        return toBigDecimal().toByte()
    }

    override fun toChar(): Char {
        return toBigDecimal().toChar()
    }

    override fun toShort(): Short {
        return toBigDecimal().toShort()
    }

    companion object {

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmStatic
        val NULL = Bruch(0, 1)

        private fun toNumbers(bruch: String): Array<BigInteger> {
            val parts = StringUtils.split(bruch, "/")
            return try {
                when (parts.size) {
                    1 -> {
                        val dezimalBruch = toBruch(BigDecimal(parts[0]))
                        return arrayOf(dezimalBruch.zaehler, dezimalBruch.nenner)
                    }
                    2 -> {
                        return arrayOf(BigInteger(parts[0]), BigInteger(parts[1]))
                    }
                    else -> throw LocalizedIllegalArgumentException(bruch, "fraction")
                }
            } catch (ex: IllegalArgumentException) {
                throw LocalizedIllegalArgumentException(bruch, "fraction", ex)
            }
        }

        private fun toNumbers(decimal: BigDecimal): Array<BigInteger> {
            val dezimalBruch = toBruch(decimal)
            return arrayOf(dezimalBruch.zaehler, dezimalBruch.nenner)
        }

        private fun toBruch(decimal: BigDecimal): Bruch {
            val scale = decimal.scale()
            val z = decimal.movePointRight(scale).toBigInteger()
            val n = BigDecimal.ONE.movePointRight(scale).toBigInteger()
            return of(z, n).kuerzen()
        }

        /**
         * Liefert einen Bruch mit dem angegeben Zaehler und Nenner an.
         *
         * @param bruch Zeichenkette, z.B. "1/2"
         * @return Bruch
         */
        @JvmStatic
        fun of(bruch: String): Bruch {
            return Bruch(bruch)
        }

        /**
         * Liefert einen Bruch mit dem angegeben Zaehler und Nenner an.
         *
         * @param zaehler Zaehler
         * @param nenner Nenner
         * @return Bruch
         */
        @JvmStatic
        fun of(zaehler: Long, nenner: Long): Bruch {
            return Bruch(zaehler, nenner)
        }

        /**
         * Liefert einen Bruch mit dem angegeben Zaehler und Nenner an.
         *
         * @param zaehler Zaehler
         * @param nenner Nenner
         * @return Bruch
         */
        @JvmStatic
        fun of(zaehler: BigInteger, nenner: BigInteger): Bruch {
            return Bruch(zaehler, nenner)
        }

    }

}