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
import java.time.Period
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
open class Zeitdauer(private val von: Zeitpunkt, private val bis: Zeitpunkt? = null) : KFachwert, Localized,
    TemporalAmount, Comparable<Zeitdauer> {

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
        return getZaehler(t, Zeiteinheit.of(unit))
    }

    private fun getZaehler(t: BigInteger, unit: TemporalUnit) : BigInteger {
        val zeiteinheit = Zeiteinheit.of(unit)
        return t.divide(zeiteinheit.toNanos(1))
    }

    fun getEinheit(): Zeiteinheit {
        return getEinheit(getTimeInNanos())
    }

    private fun getEinheit(t: BigInteger) : Zeiteinheit {
        return if (t.compareTo(Zeiteinheit.ERAS.toNanos(5)) > 0) {
            return Zeiteinheit.ERAS
        } else if (t.compareTo(Zeiteinheit.MILLENNIA.toNanos(5)) > 0) {
            Zeiteinheit.MILLENNIA
        } else if (t.compareTo(Zeiteinheit.CENTURIES.toNanos(5)) > 0) {
            Zeiteinheit.CENTURIES
        } else if (t.compareTo(Zeiteinheit.DECADES.toNanos(5)) > 0) {
            Zeiteinheit.DECADES
        } else if (t.compareTo(Zeiteinheit.YEARS.toNanos(5)) > 0) {
            Zeiteinheit.YEARS
        } else if (t.compareTo(Zeiteinheit.MONTHS.toNanos(5)) > 0) {
            Zeiteinheit.MONTHS
        } else if (t.compareTo(Zeiteinheit.WEEKS.toNanos(5)) > 0) {
            Zeiteinheit.WEEKS
        } else if (t.compareTo(Zeiteinheit.DAYS.toNanos(3)) > 0) {
            Zeiteinheit.DAYS
        } else if (t.compareTo(Zeiteinheit.HALF_DAYS.toNanos(5)) > 0) {
            Zeiteinheit.HALF_DAYS
        } else if (t.compareTo(Zeiteinheit.HOURS.toNanos(3)) > 0) {
            Zeiteinheit.HOURS
        } else if (t.compareTo(Zeiteinheit.MINUTES.toNanos(3)) > 0) {
            Zeiteinheit.MINUTES
        } else if (t.compareTo(Zeiteinheit.SECONDS.toNanos(5)) > 0) {
            Zeiteinheit.SECONDS
        } else if (t.compareTo(Zeiteinheit.MILLISECONDS.toNanos(5)) > 0) {
            Zeiteinheit.MILLISECONDS
        } else if (t.compareTo(Zeiteinheit.MICROSECONDS.toNanos(5)) > 0) {
            Zeiteinheit.MICROSECONDS
        } else {
            Zeiteinheit.NANOSECONDS
        }
    }

    fun toPeriod() : Period {
        val einheit = getEinheit()
        return when (einheit) {
            Zeiteinheit.WEEKS -> Period.ofWeeks(getZaehler(einheit).toInt())
            Zeiteinheit.MONTHS -> Period.ofMonths(getZaehler(einheit).toInt())
            Zeiteinheit.YEARS,
            Zeiteinheit.DECADES,
            Zeiteinheit.CENTURIES,
            Zeiteinheit.MILLENNIA,
            Zeiteinheit.ERAS -> Period.ofYears(getZaehler(Zeiteinheit.YEARS).toInt())
            else -> Period.ofDays(getZaehler(Zeiteinheit.DAYS).toInt())
        }
    }

    fun getTimeInNanos() : BigInteger {
        return (bis?:Zeitpunkt()).minus(von).getTimeInNanos()
    }

    fun getTimeInMillis() : Long {
        return getTimeInNanos().divide(Zeiteinheit.MILLISECONDS.toNanos(1)).toLong()
    }

    override fun get(unit: TemporalUnit): Long {
        return getZaehler(unit).toLong()
    }

    override fun getUnits(): List<TemporalUnit> {
        val einheit = getEinheit()
        val units: MutableList<TemporalUnit> = mutableListOf()
        for (e in Zeiteinheit.entries) {
            if ((e != Zeiteinheit.UNBEKANNT) && (einheit.compareTo(e) >= 0)) {
                units.add(e)
            }
        }
        return units.toList()
    }

    override fun addTo(temporal: Temporal): Temporal {
        val einheit = getEinheit()
        return temporal.plus(getZaehler().toLong(), einheit.toChronoUnit())
    }

    override fun subtractFrom(temporal: Temporal): Temporal {
        val einheit = getEinheit()
        return temporal.minus(getZaehler().toLong(), einheit.toChronoUnit())
    }

    override fun toString(): String {
        val t = getTimeInNanos()
        return "${getZaehler(t)} " + getLocalizedString(getEinheit(t).toString())
    }

    override fun compareTo(other: Zeitdauer): Int {
        return getTimeInNanos().compareTo(other.getTimeInNanos())
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Zeitdauer
        return von.equals(other.von) && Objects.equals(bis, other.bis)
    }

    override fun hashCode(): Int {
        return von.hashCode()
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
            return of(code, Zeiteinheit.of(unit))
        }

        /**
         * Liefert eine Zeitdauer zurueck.
         *
         * @param code beliebige Zahl
         * @param unit Zeiteinheilt
         * @return die Zeitdauer
         */
        @JvmStatic
        fun of(code: Long, unit: TemporalUnit): Zeitdauer {
            val zeiteinheit = Zeiteinheit.of(unit)
            return of(zeiteinheit.toNanos(code))
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
            return of(dauer.getTimeInNanos())
        }

        /**
         * Liefert eine Zeitdauer zurueck.
         *
         * @param nanos Zeit in Nanosekunden
         * @return die Zeitdauer
         */
        @JvmStatic
        fun of(nanos: BigInteger): Zeitdauer {
            return WEAK_CACHE.computeIfAbsent(nanos) { n: BigInteger -> Zeitdauer(n) }
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

    }

}