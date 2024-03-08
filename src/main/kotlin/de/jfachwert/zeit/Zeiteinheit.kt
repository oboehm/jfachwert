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

import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import java.math.BigInteger
import java.util.concurrent.TimeUnit

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
enum class Zeiteinheit(private val nanos: BigInteger) {

    /** Zeiteinheit fuer Nano-Sekunden .*/
    NANOSECONDS(BigInteger.ONE),

    /** Zeiteinheit fuer Micro-Sekunden. */
    MICROSECONDS(BigInteger.valueOf(1_000L)),

    /** Zeiteinheit fuer Milli-Sekunden. */
    MILLISECONDS(BigInteger.valueOf(1_000_000L)),

    /** Zeiteinheit fuer Sekunden. */
    SECONDS(BigInteger.valueOf(1_000_000_000L)),

    /** Zeiteinheit fuer Minuten. */
    MINUTES(BigInteger.valueOf(60_000_000_000L)),

    /** Zeiteinheit fuer Stunden. */
    HOURS(BigInteger.valueOf(3_600_000_000_000L)),

    /** Zeiteinheit fuer Tage. */
    DAYS(BigInteger.valueOf(86_400_000_000_000L)),

    /** Zeiteinheit fuer Wochen. */
    WEEKS(BigInteger.valueOf(604_800_000_000_000L)),

    /** Zeiteinheit fuer Monate (= 1 Jahr / 12). */
    MONTHS(BigInteger.valueOf(2_629_746_000_000_000L)),

    /** Zeiteinheit fuer Jahre (1 Jahr = 365,2425 Tage). */
    YEARS(BigInteger.valueOf(31_556_952_000_000_000L)),

    /** Zeiteinheit fuer Jahrhunderte. */
    CENTURIES(BigInteger.valueOf(3_155_695_200_000_000_000L)),

    /** Zeiteinheit fuer Jahrtausende. */
    MILLENNIA(CENTURIES.nanos.multiply(BigInteger.valueOf(10))),

    /** Unbekannte Zeiteinheit. */
    UNBEKANNT(BigInteger.ZERO);

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
     * @param duration: umzurechnende Zahl
     * @return Nano-Sekunden als BigInteger
     */
    fun toNanos(duration: Long): BigInteger {
        return nanos.multiply(BigInteger.valueOf(duration))
    }

    /**
     * Wandelt die Zahl in Micro-Sekunden um.
     *
     * @param duration: umzurechnende Zahl
     * @return Sekunden als BigInteger
     */
    fun toMicros(duration: Long): BigInteger {
        return nanos.multiply(BigInteger.valueOf(duration)).divide(MICROSECONDS.nanos)
    }

    /**
     * Wandelt die Zahl in Milli-Sekunden um.
     *
     * @param duration: umzurechnende Zahl
     * @return Sekunden als BigInteger
     */
    fun toMillis(duration: Long): BigInteger {
        return nanos.multiply(BigInteger.valueOf(duration)).divide(MILLISECONDS.nanos)
    }

    /**
     * Wandelt die Zahl in Sekunden um.
     *
     * @param duration: umzurechnende Zahl
     * @return Sekunden als BigInteger
     */
    fun toSeconds(duration: Long): BigInteger {
        return nanos.multiply(BigInteger.valueOf(duration)).divide(SECONDS.nanos)
    }

    /**
     * Wandelt die Zahl in Minuten um.
     *
     * @param duration: umzurechnende Zahl
     * @return Minuten als BigInteger
     */
    fun toMinutes(duration: Long): BigInteger {
        return nanos.multiply(BigInteger.valueOf(duration)).divide(MINUTES.nanos)
    }

    /**
     * Wandelt die Zahl in Stunden um.
     *
     * @param duration: umzurechnende Zahl
     * @return Stunden als BigInteger
     */
    fun toHours(duration: Long): BigInteger {
        return nanos.multiply(BigInteger.valueOf(duration)).divide(HOURS.nanos)
    }

    /**
     * Wandelt die Zahl in Tage um.
     *
     * @param duration: umzurechnende Zahl
     * @return Tage als BigInteger
     */
    fun toDays(duration: Long): BigInteger {
        return nanos.multiply(BigInteger.valueOf(duration)).divide(DAYS.nanos)
    }



    companion object {

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