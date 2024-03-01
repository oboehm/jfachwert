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
import java.util.concurrent.TimeUnit

/**
 * Im Gegensatz zur TimeUnit-Klasse aus dem JDK deckt Zeiteinheit auch
 * groessere Einheiten wie Wochen, Monate oder Jahre ab. Die Benennung
 * der Konstanten orientiert sich dabei in TimeUnit, damit es als Ersatz
 * dafuer verwendet werden kann.
 *
 * @author oboehm
 * @since 5.4 (29.02.2024)
 */
enum class Zeiteinheit {

    /** Zeiteinheit fuer Nano-Sekunden .*/
    NANOSECONDS,

    /** Zeiteinheit fuer Micro-Sekunden. */
    MICROSECONDS,

    /** Zeiteinheit fuer Milli-Sekunden. */
    MILLISECONDS,

    /** Zeiteinheit fuer Sekunden. */
    SECONDS,

    /** Zeiteinheit fuer Minuten. */
    MINUTES,

    /** Zeiteinheit fuer Stunden. */
    HOURS,

    /** Zeiteinheit fuer Tage. */
    DAYS,

    /** Zeiteinheit fuer Wochen. */
    WEEKS,

    /** Zeiteinheit fuer Monate. */
    MONTHS,

    /** Zeiteinheit fuer Jahre. */
    YEARS,

    /** Zeiteinheit fuer Jahrhunderte. */
    CENTURIES,

    /** Zeiteinheit fuer Jahrtausende. */
    MILLENNIA,

    /** Unbekannte Zeiteinheit. */
    UNBEKANNT;

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