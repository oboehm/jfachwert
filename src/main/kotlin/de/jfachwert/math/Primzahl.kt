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
 * (c)reated 04.04.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math

import de.jfachwert.KFachwert
import java.lang.ref.SoftReference
import java.math.BigInteger
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Eine Primzahl ist eine natuerliche Zahl, die nur durch 1 und durch sich
 * selbst teilbar ist. Die kleinste Primzahl ist 2.
 *
 * Intern wird 'int' zur Speicherung der Primzahl verwendet, da dies fuer den
 * Standard-Fall ausreichend ist. So benoetigt bereits die Ermittlung einer
 * 8-stelligen Primzahl (&gt; 10 Mio.) ca. 3 Minuten. Die Emittlung einer
 * 10-stelligen Primzahl (&lt; 2 Mrd.) dürfte damit im Stunden, wenn nicht gar
 * Tage-Bereich liegen.
 *
 * Die groesste Primzahl, die mit einem long dargestellt werden kann, ist
 * 9223372036854775783.
 *
 * @author oboehm
 * @since 0.6.1 (04.04.2018)
 */
open class Primzahl private constructor(private val value: Int) : Number(), KFachwert, Comparable<Primzahl> {

    /**
     * Liefert den numerischen Wert der Primzahl. Der Name der Methode
     * orientiert sich dabei an die Number-Klasse aus Java.
     *
     * @return numerischer Wert
     */
    override fun toLong(): Long {
        return value.toLong()
    }

    /**
     * Liefert den numerischen Wert der Primzahl. Der Name der Methode
     * orientiert sich dabei an die Number-Klasse aus Java.
     *
     * @return numerischer Wert
     */
    override fun toInt(): Int {
        return value
    }

    /**
     * Liefert die Zahl als ein `float` zurueck.
     *
     * @return den numerischen Wert als `float`
     * @since 0.6.2
     */
    override fun toFloat(): Float {
        return toBigInteger().toFloat()
    }

    /**
     * Liefert die Zahl als ein `double` zurueck.
     *
     * @return den numerischen Wert als `double`
     * @since 0.6.2
     */
    override fun toDouble(): Double {
        return toBigInteger().toDouble()
    }

    override fun toShort(): Short {
        return toBigInteger().toShort()
    }

    override fun toByte(): Byte {
        return toBigInteger().toByte()
    }

    override fun toChar(): Char {
        return toInt().toChar()
    }

    /**
     * Liefert den numerischen Wert der Primzahl als [BigInteger]. Der
     * Name der Methode orientiert sich dabei an die BigDecimal-Klasse aus
     * Java.
     *
     * @return numerischer Wert
     */
    fun toBigInteger(): BigInteger {
        return BigInteger.valueOf(toLong())
    }

    /**
     * Liefert die naechste Primzahl.
     *
     * @return naechste Primzahl
     */
    operator fun next(): Primzahl {
        return after(toInt())
    }

    /**
     * Dient zum Vergleich zweier Primzahlen.
     *
     * @param other die andere Primzahl
     * @return Abstand zur anderen Primzahl
     * @since 3.0
     */
    override fun compareTo(other: Primzahl): Int {
        return value - other.value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val primzahl = other as Primzahl
        return value == primzahl.value
    }

    override fun hashCode(): Int {
        return value
    }

    /**
     * Als Ausgabe nehmen wir die Zahl selbst.
     *
     * @return die Zahl selbst
     */
    override fun toString(): String {
        return Integer.toString(value)
    }



    companion object {

        /** Zwei ist die kleinste Primzahl.  */
        @JvmField
        val ZWEI = Primzahl(2)

        /** Drei ist die naechste Primzahl.  */
        @JvmField
        val DREI = Primzahl(3)
        private var refPrimzahlen = SoftReference(initPrimzahlen())

        private fun initPrimzahlen(): MutableList<Primzahl> {
            val primzahlen: MutableList<Primzahl> = CopyOnWriteArrayList()
            primzahlen.add(DREI)
            return primzahlen
        }

        /**
         * Liefert die erste Primzahl.
         *
         * @return #ZWEI
         */
        fun first(): Primzahl {
            return ZWEI
        }

        /**
         * Liefert die naechste Primzahl nach der angegebenen Zahl.
         *
         * @param zahl Zahl
         * @return naechste Primzahl &gt; zahl
         */
        @JvmStatic
        fun after(zahl: Int): Primzahl {
            val primzahlen = primzahlen
            for (p in primzahlen) {
                if (zahl < p.toInt()) {
                    return p
                }
            }
            run {
                var n = primzahlen[primzahlen.size - 1].toInt() + 2
                while (n <= zahl) {
                    if (!hasTeiler(n)) {
                        primzahlen.add(Primzahl(n))
                    }
                    n += 2
                }
            }
            var n = primzahlen[primzahlen.size - 1].toInt() + 2
            while (hasTeiler(n)) {
                n += 2
            }
            val nextPrimzahl = Primzahl(n)
            primzahlen.add(nextPrimzahl)
            return nextPrimzahl
        }

        /**
         * Ermittelt, ob die uebergebene Zahl einen Teiler hat.
         * Alternative Implementierung mit Streams zeigten ein deutlich
         * schlechteres Zeitverhalten (ca. Faktor 10 langsamer). Eine weitere
         * Implementierung mit ParallelStreams war noch langsamer - vermutlich
         * ist der Overhaed einfach zu gross.
         *
         * @param n Zahl, die nach einem Teiler untersucht wird
         * @return true, falls Zahl einen Teiler hat (d.h. keine Primzahl ist)
         */
        private fun hasTeiler(n: Int): Boolean {
            for (p in primzahlen) {
                val teiler = p.toInt()
                if (n % teiler == 0) {
                    return true
                }
                if (teiler * teiler > n) {
                    break
                }
            }
            return false
        }

        private val primzahlen: MutableList<Primzahl>
            get() {
                var primzahlen = refPrimzahlen.get()
                if (primzahlen == null) {
                    primzahlen = initPrimzahlen()
                    refPrimzahlen = SoftReference(primzahlen)
                }
                return primzahlen
            }

    }

}