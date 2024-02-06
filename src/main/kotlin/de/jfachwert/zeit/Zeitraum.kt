/*
 * Copyright (c) 2024 by Oli B.
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
 * (c)reated 15.01.24 by oboehm
 */
package de.jfachwert.zeit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.jfachwert.KFachwert
import de.jfachwert.util.ToFachwertSerializer
import java.util.*

/**
 * Die Klasse repraesentiert einen Zeitraum zwischen 2 Zeitpunkten.
 *
 * @author oboehm
 * @since 5.2 (15.01.24)
 */
@JsonSerialize(using = ToFachwertSerializer::class)
open class Zeitraum
constructor(val von: Zeitpunkt, val bis: Zeitpunkt) : KFachwert {

    /**
     * Zerlegt den uebergebenen String in zwei Zeitpunkte "von" und "bis".
     *
     * @param vonbis z.B. "2024-01-01 - 2024-12-34"
     */
    constructor(vonbis: String) : this(split(vonbis))

    /**
     * Erzeugt einen neuen Zeitruam aus der uebergebenen Map.
     *
     * @param map mit den einzelnen Elementen "von" und "bis"
     * "bic".
     */
    @JsonCreator
    constructor(map: Map<String, Zeitpunkt>) :
            this(map["von"]!!, map["bis"]!!)

    /**
     * Liefert die Zeitdauer des Zeitraums zurueck.
     *
     * @return Zeitdauer
     */
    fun getZeitdauer() : Zeitdauer {
        return Zeitdauer.of(von, bis)
    }

    override fun toString(): String {
        return "$von - $bis"
    }

    /**
     * Liefert die einzelnen Attribute des Zeitraums als Map.
     *
     * @return Attribute als Map
     */
    override fun toMap(): Map<String, Zeitpunkt> {
        val map: MutableMap<String, Zeitpunkt> = HashMap()
        map["von"] = von
        map["bis"] = bis
        return map
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Zeitraum) {
            return false
        }
        return von == other.von && bis == other.bis
    }

    override fun hashCode(): Int {
        return bis.hashCode()
    }



    companion object {

        private val WEAK_CACHE = WeakHashMap<Pair<Zeitpunkt, Zeitpunkt>, Zeitraum>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = Zeitraum(Zeitpunkt.EPOCH, Zeitpunkt.EPOCH)

        /**
         * Interpretiert den eingegebenen String als Zeitraum
         *
         * @param s z.B. "2024-01-01 - 2025-01-01"
         */
        @JvmStatic
        fun of(s: String): Zeitraum {
            val map = split(s)
            return of(map["von"]!!, map["bis"]!!)
        }

        /**
         * Liefert einen Zeitraum.
         *
         * @param von Start-Zeitpunkt
         * @param bis Ende.Zeitpunkt
         */
        @JvmStatic
        fun of(von: Date, bis: Date): Zeitraum {
            return of(Zeitpunkt.of(von), Zeitpunkt.of(bis))
        }

        /**
         * Liefert einen Zeitraum.
         *
         * @param von Start-Zeitpunkt
         * @param bis Ende.Zeitpunkt
         */
        @JvmStatic
        fun of(von: Zeitpunkt, bis: Zeitpunkt): Zeitraum {
            val pair = Pair(von, bis)
            return WEAK_CACHE.computeIfAbsent(pair) { Zeitraum(von, bis) }
        }

        private fun split(vonbis: String) : Map<String, Zeitpunkt> {
            val map: MutableMap<String, Zeitpunkt> = HashMap()
            val von = vonbis.substring(0, vonbis.length/2).trim()
            val bis = vonbis.substring(vonbis.length/2+1, vonbis.length).trim()
            map["von"] = Zeitpunkt.of(von)
            map["bis"] = Zeitpunkt.of(bis)
            return map
        }

    }

}