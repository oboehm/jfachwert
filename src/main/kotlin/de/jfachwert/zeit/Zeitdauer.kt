/*
 * Copyright (c) 2023-2024 by Oli B.
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
 * (c)reated 10.07.23 by oboehm
 */
package de.jfachwert.zeit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.jfachwert.KFachwert
import de.jfachwert.Localized
import de.jfachwert.util.ToFachwertSerializer
import java.math.BigInteger
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Die Zeitdauer repraesentiert die Dauer zwischen zwei Zeitpunkten.
 * Fehlt der zweite Zeitpunkt, repraesentiert diese Klasse den Zeitraum
 * zwischen Start und aktuellem Zeitpunkt.
 *
 * Die Zeitdauer-Klasse ist angelehnt an die Duration-Klasse aus dem
 * JDK, nur dass sie ueber den Default-Constructor auch zur Zeitmessung
 * verwendet werden kann: einfach anlegen und ausgeben - schon hat man
 * die aktuelle Zeitdauer seit dem Start (Aufruf des Default-Constructors).
 *
 * @author oboehm
 * @since 5.0 (10.07.2023)
 */
@JsonSerialize(using = ToFachwertSerializer::class)
open class Zeitdauer(private val von: Zeitpunkt, private val bis : Zeitpunkt? = null) : KFachwert, Localized, TemporalAmount {

    constructor() : this(Zeitpunkt())

    constructor(code: Long, unit: TimeUnit) : this(Zeitpunkt.EPOCH, Zeitpunkt.of(BigInteger.valueOf(unit.toNanos(code))))

    constructor(code: BigInteger) : this(Zeitpunkt.EPOCH, Zeitpunkt.of(code))

    /**
     * Da Zeitdauer immutable ist, wird eine neue Zeitdauer zurueckgegeben.
     *
     * @return neue Zeitdauer mit jetzigem End-Zeitpunkt
     */
    fun stop() : Zeitdauer {
        return Zeitdauer(von, Zeitpunkt.now())
    }

    fun getZaehler() : BigInteger {
        return getZaehler(getTimeInNanos())
    }

    fun getZaehler(unit: TimeUnit) : BigInteger {
        return getZaehler(getTimeInNanos(), unit)
    }

    fun getZaehler(unit: TemporalUnit) : BigInteger {
        return getZaehler(getTimeInNanos(), unit)
    }

    private fun getZaehler(t: BigInteger) : BigInteger {
        return getZaehler(t, getEinheit(t))
    }

    private fun getZaehler(t: BigInteger, unit: TimeUnit) : BigInteger {
        return when(unit) {
            TimeUnit.NANOSECONDS -> t
            TimeUnit.MICROSECONDS -> t.divide(MICROSECOND_IN_NANOS)
            TimeUnit.MILLISECONDS -> t.divide(MILLISECOND_IN_NANOS)
            TimeUnit.SECONDS -> t.divide(SECOND_IN_NANOS)
            TimeUnit.MINUTES -> t.divide(MINUTE_IN_NANOS)
            TimeUnit.HOURS -> t.divide(HOUR_IN_NANOS)
            TimeUnit.DAYS -> t.divide(DAY_IN_NANOS)
        }
    }

    private fun getZaehler(t: BigInteger, unit: TemporalUnit) : BigInteger {
        val zeiteinheit = Zeiteinheit.of(unit)
        return t.divide(zeiteinheit.toNanos(1))
    }

    fun getEinheit(): TimeUnit {
        return getEinheit(getTimeInNanos())
    }

    private fun getEinheit(t: BigInteger) : TimeUnit {
        return if (t.compareTo(DAY_IN_NANOS.multiply(BigInteger.valueOf(4L))) > 0) {
            TimeUnit.DAYS
        } else if (t.compareTo(HOUR_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.HOURS
        } else if (t.compareTo(MINUTE_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.MINUTES
        } else if (t.compareTo(SECOND_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.SECONDS
        } else if (t.compareTo(MILLISECOND_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.MILLISECONDS
        } else if (t.compareTo(MICROSECOND_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.MICROSECONDS
        } else {
            TimeUnit.NANOSECONDS
        }
    }

    fun getTimeInNanos() : BigInteger {
        return (bis?:Zeitpunkt()).minus(von).getTimeInNanos()
    }

    fun getTimeInMillis() : Long {
        return getTimeInNanos().divide(MILLISECOND_IN_NANOS).toLong()
    }

    override fun get(unit: TemporalUnit): Long {
        return getZaehler(unit).toLong()
    }

    override fun getUnits(): MutableList<TemporalUnit> {
        TODO("Not yet implemented")
    }

    override fun addTo(temporal: Temporal?): Temporal {
        TODO("Not yet implemented")
    }

    override fun subtractFrom(temporal: Temporal?): Temporal {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        val t = getTimeInNanos()
        return "${getZaehler(t)} " + getLocalizedString(getEinheit(t).toString())
    }

    /**
     * Liefert den von- und bis-Zeitpunkt als Map.
     *
     * @return Attribute als Map
     */
    override fun toMap(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["von"] = von.getTimeInNanos()
        if (bis != null) {
            map["bis"] = bis.getTimeInNanos()
        }
        return map
    }



    companion object {

        private val WEAK_CACHE = WeakHashMap<BigInteger, Zeitdauer>()

        /**
         * Liefert eine Zeitdauer zurueck.
         *
         * @param code beliebige Zahl
         * @param unit Zeiteinheilt
         * @return die Zeitdauer
         */
        @JvmStatic
        fun of(code: Long, unit: TimeUnit): Zeitdauer {
            val nanos = BigInteger.valueOf(unit.toNanos(code))
            return WEAK_CACHE.computeIfAbsent(nanos) { n: BigInteger -> Zeitdauer(n) }
        }

        /**
         * Liefert eine Zeitdauer zurueck.
         *
         * @param von Zeitpunkt
         * @param bis Zeitpunkt
         * @return die Zeitdauer
         */
        @JvmStatic
        fun of(von: Zeitpunkt, bis: Zeitpunkt): Zeitdauer {
            val dauer = Zeitdauer(von, bis)
            val nanos = dauer.getTimeInNanos()
            return WEAK_CACHE.computeIfAbsent(nanos) { dauer }
        }

        /**
         * Liefert eine Zeitdauer zurueck.
         *
         * @param von Zeitpunkt in Nanosekunden
         * @param bis Zeitpunkt in Nanosekunden
         * @return die Zeitdauer
         */
        @JsonCreator
        @JvmStatic
        fun of(@JsonProperty("von") von: BigInteger, @JsonProperty("bis") bis: BigInteger): Zeitdauer {
            return of(Zeitpunkt.of(von), Zeitpunkt.of(bis))
        }

        /**
         * Startet die Zeitmessung.
         *
         * @return aktuelle Zeitdauer
         */
        @JvmStatic
        fun start() : Zeitdauer {
            return Zeitdauer()
        }

        val MICROSECOND_IN_NANOS = BigInteger.valueOf(1_000L)
        val MILLISECOND_IN_NANOS = BigInteger.valueOf(1_000_000L)
        val SECOND_IN_NANOS = BigInteger.valueOf(1_000_000_000L)
        val MINUTE_IN_NANOS = BigInteger.valueOf(60_000_000_000L)
        val HOUR_IN_NANOS = BigInteger.valueOf(3_600_000_000_000L)
        val DAY_IN_NANOS = BigInteger.valueOf(86_400_000_000_000L)
        val YEAR_IN_NANOS = BigInteger.valueOf(31_556_952_000_000_000L)    // 1 Jahr = 365,2425 Tage

    }

}