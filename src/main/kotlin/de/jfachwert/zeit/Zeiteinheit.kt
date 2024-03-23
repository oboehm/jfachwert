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
 * (c)reated 29.02.24 by oboehm
 */
package de.jfachwert.zeit

import de.jfachwert.KFachwert
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import java.math.BigInteger
import java.time.Duration
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Im Gegensatz zur TimeUnit-Klasse aus dem JDK deckt Zeiteinheit auch
 * groessere Einheiten wie Wochen, Monate oder Jahre ab. Die Benennung
 * der Konstanten orientiert sich dabei in TimeUnit, damit es als Ersatz
 * dafuer verwendet werden kann.
 *
 * Da u.U. mit grossen Zeitraeumen hantiert wird, liefern to toXxx()-Methoden
 * wie z.B. toNanos() keinen Long sondern BigInteger zurueck.
 *
 * @author oboehm
 * @since 5.4 (29.02.2024)
 */
enum class Zeiteinheit(private val duration: Duration) : KFachwert, TemporalUnit {

    /** Zeiteinheit fuer Nano-Sekunden .*/
    NANOSECONDS(Duration.ofNanos(1)),

    /** Zeiteinheit fuer Micro-Sekunden. */
    MICROSECONDS(Duration.ofNanos(1_000)),

    /** Zeiteinheit fuer Milli-Sekunden. */
    MILLISECONDS(Duration.ofMillis(1)),

    /** Zeiteinheit fuer Sekunden. */
    SECONDS(Duration.ofSeconds(1)),

    /** Zeiteinheit fuer Minuten. */
    MINUTES(Duration.ofMinutes(1)),

    /** Zeiteinheit fuer Stunden. */
    HOURS(Duration.ofHours(1)),

    /** Zeiteinheit fuer halbe Tage. */
    HALF_DAYS(Duration.ofHours(12)),

    /** Zeiteinheit fuer Tage. */
    DAYS(Duration.ofDays(1)),

    /** Zeiteinheit fuer Wochen. */
    WEEKS(Duration.ofDays(7)),

    /** Zeiteinheit fuer Monate (= 1 Jahr / 12). */
    MONTHS(Duration.ofSeconds(2_629_746)),

    /** Zeiteinheit fuer Jahre (1 Jahr = 365,2425 Tage). */
    YEARS(Duration.ofSeconds(31_556_952)),

    /** Zeiteinheit fuer Jahrhunderte. */
    DECADES(Duration.ofSeconds(315_569_520)),

    /** Zeiteinheit fuer Jahrhunderte. */
    CENTURIES(Duration.ofSeconds(3_155_695_200)),

    /** Zeiteinheit fuer Jahrtausende. */
    MILLENNIA(Duration.ofSeconds(31_556_952_000)),

    /** Zeiteinheit fuer Jahrmillionen. */
    ERAS(Duration.ofSeconds(31_556_952_000_000_000)),

    /** Zeiteinheit fuer die Ewigkeit. */
    FOREVER(Duration.ofSeconds(Long.MAX_VALUE)),

    /** Unbekannte Zeiteinheit. */
    UNBEKANNT(Duration.ZERO);

    /**
     * Wandelt die Zeiteinheit in eine TimeUnit.
     * Bitte beachten: fuer WEEKS, MONTHS, ... gibt es kein Gegenstueck in
     * TimeUnit. In diesem Fall wird eine IllegalArgumentException geworfen.
     *
     * @return TimeUnit
     */
    fun toTimeUnit(): TimeUnit {
        for (unit in TimeUnit.entries) {
            if (name.equals(unit.name)) {
                return unit
            }
        }
        throw LocalizedIllegalArgumentException(this, "keine Umwandlung moeglich")
    }

    /**
     * Wandelt die Zahl in Nano-Sekunden um.
     *
     * @param n: umzurechnende Zahl
     * @return Nano-Sekunden als BigInteger
     */
    fun toNanos(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n))
    }

    private fun toNanos() : BigInteger {
        try {
            return BigInteger.valueOf(duration.toNanos())
        } catch (ex: ArithmeticException) {
            logger.log(Level.FINE, "$duration' ist zu gross fuer Nanosekunden:", ex)
            return BigInteger.valueOf(duration.toSeconds()).multiply(BigInteger.valueOf(1_000_000_000L))
        }
    }

    /**
     * Wandelt die Zahl in Micro-Sekunden um.
     *
     * @param n: umzurechnende Zahl
     * @return Sekunden als BigInteger
     */
    fun toMicros(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(MICROSECONDS.toNanos())
    }

    /**
     * Wandelt die Zahl in Milli-Sekunden um.
     *
     * @param n: umzurechnende Zahl
     * @return Sekunden als BigInteger
     */
    fun toMillis(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(MILLISECONDS.toNanos())
    }

    /**
     * Wandelt die Zahl in Sekunden um.
     *
     * @param n: umzurechnende Zahl
     * @return Sekunden als BigInteger
     */
    fun toSeconds(n: Long): BigInteger {
        return BigInteger.valueOf(duration.toSeconds()).multiply(BigInteger.valueOf(n))
    }

    /**
     * Wandelt die Zahl in Minuten um.
     *
     * @param n: umzurechnende Zahl
     * @return Minuten als BigInteger
     */
    fun toMinutes(n: Long): BigInteger {
        return BigInteger.valueOf(duration.toMinutes()).multiply(BigInteger.valueOf(n))
    }

    /**
     * Wandelt die Zahl in Stunden um.
     *
     * @param n: umzurechnende Zahl
     * @return Stunden als BigInteger
     */
    fun toHours(n: Long): BigInteger {
        return BigInteger.valueOf(duration.toHours()).multiply(BigInteger.valueOf(n))
    }

    /**
     * Wandelt die Zahl in Tage um.
     *
     * @param n: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toDays(n: Long): BigInteger {
        return BigInteger.valueOf(duration.toDays()).multiply(BigInteger.valueOf(n))
    }

    /**
     * Wandelt die Zahl in Wochen um.
     *
     * @param n: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toWeeks(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(WEEKS.toNanos())
    }

    /**
     * Wandelt die Zahl in Monate um.
     *
     * @param n: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toMonths(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(MONTHS.toNanos())
    }

    /**
     * Wandelt die Zahl in Jahre um.
     *
     * @param n: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toYears(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(YEARS.toNanos())
    }

    /**
     * Wandelt die Zahl in Jahrzehnte um.
     *
     * @param n: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toDecades(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(DECADES.toNanos())
    }

    /**
     * Wandelt die Zahl in Jahrhunderte um.
     *
     * @param n: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toCenturies(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(CENTURIES.toNanos())
    }

    /**
     * Wandelt die Zahl in Jahrtausende um.
     *
     * @param n: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toMillenia(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(MILLENNIA.toNanos())
    }

    /**
     * Wandelt die Zahl in Jahrmillionen um.
     *
     * @param n: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toJahrmillionen(n: Long): BigInteger {
        return toNanos().multiply(BigInteger.valueOf(n)).divide(ERAS.toNanos())
    }

    override fun getDuration(): Duration {
        return duration
    }

    override fun isDurationEstimated(): Boolean {
        return duration.toSeconds() < Long.MAX_VALUE
    }

    override fun isDateBased(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTimeBased(): Boolean {
        TODO("Not yet implemented")
    }

    override fun <R : Temporal?> addTo(temporal: R, amount: Long): R {
        TODO("Not yet implemented")
    }

    override fun between(temporal1Inclusive: Temporal?, temporal2Exclusive: Temporal?): Long {
        TODO("Not yet implemented")
    }


    companion object {

        val logger = Logger.getLogger(Zeiteinheit::class.java.name)

        /**
         * Wandelt die uebergebene TimeUnit in eine Zeiteinheit.
         *
         * @param timeUnit: TimeUnit
         * @return Zeiteinheit
         */
        @JvmStatic
        fun of(timeUnit: TimeUnit): Zeiteinheit {
            for (einheit in entries) {
                if (timeUnit.name.equals(einheit.name)) {
                    return einheit
                }
            }
            return UNBEKANNT
        }

    }

}