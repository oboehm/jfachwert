/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 10.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.Fachwert
import de.jfachwert.math.Prozent
import java.util.*
import java.util.function.Function

/**
 * Der Zinssatz (auch: Zinsfuss) wird in Prozent ausgedrueckt, mit dem der Zins
 * fuer verliehenes Kapital berechnet wird.
 *
 * @author oboehm
 * @since 4.0
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Zinssatz(val prozent: Prozent) : Fachwert, Comparable<Zinssatz> {

    constructor(satz: String) : this(Prozent.of(satz))

    companion object {

        private val WEAK_CACHE = WeakHashMap<Prozent, Zinssatz>()

        /** Zinssatz von 0%. */
        @JvmField
        val ZERO = of(Prozent.ZERO)

        /** Zinssatz von 1%. */
        @JvmField
        val ONE = of(Prozent.ONE)

        /** Zinssatz von 10%. */
        @JvmField
        val TEN = of(Prozent.TEN)

        /**
         * Die of-Methode liefert fuer denselben Prozentwert immer dasselbe
         * Objekt zurueck. Bevorzugt sollte man diese Methode verwenden, um
         * die Anzahl der Objekte gering zu halten.
         *
         * @param satz z.B. "5%"
         * @return 5%" als Zinssatz-Objekt
         */
        @JvmStatic
        fun of(satz: String): Zinssatz {
            return of(Prozent.of(satz))
        }

        /**
         * Die of-Methode liefert fuer denselben Prozentwert immer dasselbe
         * Objekt zurueck. Bevorzugt sollte man diese Methode verwenden, um
         * die Anzahl der Objekte gering zu halten.
         *
         * @param satz als Prozentwert, z.B. "5%"
         * @return "5%" als Zinssatz-Objekt
         */
        @JvmStatic
        fun of(satz: Prozent): Zinssatz {
            return WEAK_CACHE.computeIfAbsent(satz, Function(::Zinssatz))
        }

    }

    /**
     * Vergleicht zwei Zinssaetze. Wenn [other] ein kleinerer Zinssatz ist,
     * wird eine positive Zahl zurueckgegeben.
     * Bei Gleichheit wird 0 zurueckgegeben, ansonsten eine negative Zahl.
     */
    override fun compareTo(other: Zinssatz): Int {
        return prozent.compareTo(other.prozent)
    }

    override fun toString(): String {
        return prozent.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Zinssatz) return false
        return prozent == other.prozent
    }

    override fun hashCode(): Int {
        return prozent.hashCode()
    }

}