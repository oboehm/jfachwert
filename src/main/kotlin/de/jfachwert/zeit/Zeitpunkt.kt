/*
 * Copyright (c) 2023 by Oli B.
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
 * (c)reated 18.07.23 by oboehm
 */
package de.jfachwert.zeit

import de.jfachwert.AbstractFachwert
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import java.math.BigInteger
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


/**
 * Diese Klasse repraesentiert einen Zeitpunkt in der Vergangenheit oder
 * auch in der Zukunft. Sie entspricht damit in etwa der Timestamp- oder
 * LocalDateTime-Klasse, nur dass die Aufloesung hier etwas genauer ist
 * und im Nanosekunden-Bereich liegt.
 *
 * Als Basis wird der 1.1.1970 0:00 UTC verwendet. Auch die Ausgabe verwendet
 * UTC als Basis, wenn nicht der ZoneOffset als Parameter uebergeben wird.
 *
 * Die Klasse ist nicht von der LocalDateTime-Klasse abgeleitet, da sie
 * final ist. Die Timestamp-Klasse kommt nicht in Frage, da diese Klasse
 * nicht immutable ist.
 *
 * Die Zeitpunkt-Klasse ist wie alle JFachwert-Klasse immutable, orientiert
 * sich aber ansonsten neben der LocalDateTime-Klasse auch an der Timestamp-
 * Klasse, sodasss sie als Ersatz fuer diese beide Klassen dienen kann.
 *
 * @author oboehm
 * @since 5.0 (18.07.2023)
 */
open class Zeitpunkt
constructor(t: BigInteger): AbstractFachwert<BigInteger, Zeitpunkt>(t) {

    /**
     * Erzeugt einen aktuellen Zeitpunkt.
     */
    constructor() : this(currentTimeNanos()) {}

    /**
     * Erzeugt einen neuen Zeitpunkt.
     *
     * @param code Ganz-Zahl als String
     */
    constructor(code: String) : this(toNanos(code)) {}

    /**
     * Liefert den aktuellen Zeitpunkt in Nanosekunden seit 1970 zurueck.
     *
     * @return Zeit seit 1.1.1970 in ns
     */
    fun getTimeInNanos() : BigInteger {
        return code
    }

    /**
     * Liefert den aktuellen Zeitpunkt in Millisekunden seit 1970 zurueck.
     *
     * @return Zeit seit 1.1.1970 in ms
     */
    fun getTimeInMillis() : Long {
        return code.divide(Zeitdauer.MILLISECOND_IN_NANOS).toLong()
    }

    /**
     * Liefert den Nano-Anteil der Sekunde
     *
     * @return Nano-Anteil, von 0 bis 999_999_999
     */
    fun getNanos(): Int {
        return code.mod(Zeitdauer.SECOND_IN_NANOS).toInt()
    }

    fun toEpochSecond(): Long {
        return code.divide(Zeitdauer.SECOND_IN_NANOS).toLong()
    }

    /**
     * Zieht den uebergebenen Zeitpunkt ab.
     *
     * @param t: Zeitpunkt, der abgezogen wird
     * @return ermittelter Zeitpunkt als Kopie
     */
    fun minus(t: Zeitpunkt) : Zeitpunkt {
        return of(code.subtract(t.code))
    }

    /**
     * Addiert zwei Zeitpunkte
     *
     * @param t: Zeitpunkt, der addiert wird
     * @return ermittelter Zeitpunkt als Kopie
     */
    fun plus(t: Zeitpunkt) : Zeitpunkt {
        return of(code.add(t.code))
    }

    /**
     * Wandelt den Zeitpunkt in einen Timestamp um.
     *
     * @return Timestamp aus java.sql
     */
    fun toTimestamp() : Timestamp {
        return Timestamp.valueOf(toLocalDateTime())
    }

    /**
     * Wandelt den Zeitpunkt in ein LocalDateTime um.
     *
     * @return LocalDateTime aus java.time
     */
    fun toLocalDateTime() : LocalDateTime {
        return toLocalDateTime(ZoneOffset.UTC)
    }

    /**
     * Wandelt den Zeitpunkt in ein LocalDateTime um.
     *
     * @param offset Offset zu UTC
     * @return LocalDateTime aus java.time
     */
    fun toLocalDateTime(offset: ZoneOffset) : LocalDateTime {
        return LocalDateTime.ofEpochSecond(toEpochSecond(), getNanos(), offset)
    }

    /**
     * Zeitpunkt wird als Zeit-/Datumsanage ausgegeben.
     *
     * @return Ausgabe aehnlich wie bei Timestamp, aber Nonosekunden-genau
     */
    override fun toString(): String {
        return toString(ZoneOffset.UTC)
    }

    /**
     * Zeitpunkt wird als Zeit-/Datumsanage ausgegeben.
     *
     * @param offset Offset zu UTC
     * @return Ausgabe aehnlich wie bei Timestamp, aber Nonosekunden-genau
     */
    fun toString(offset: ZoneOffset): String {
        val dtfb = DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val formatter = dtfb.toFormatter()
        return String.format("%s.%09d", formatter.format(toLocalDateTime(offset)), getNanos())
    }



    companion object {

        private val log = Logger.getLogger(Zeitpunkt::class.java.name)
        private val WEAK_CACHE = WeakHashMap<BigInteger, Zeitpunkt>()
        /** Die Epoche beginnt am 1.1.1970. */
        @JvmField
        val EPOCH = Zeitpunkt(BigInteger.ZERO)

        /**
         * Liefert einen Zeitpunkt zurueck.
         *
         * @param code beliebige Zahl
         * @return der Zeitpunkt
         */
        @JvmStatic
        fun of(code: BigInteger): Zeitpunkt {
            return WEAK_CACHE.computeIfAbsent(code) { n: BigInteger -> Zeitpunkt(n) }
        }

        /**
         * Liefert einen Zeitpunkt zurueck.
         *
         * @param code beliebige Zahl als String
         * @return der Zeitpunkt
         */
        @JvmStatic
        fun of(code: String): Zeitpunkt {
            try {
                return of(BigInteger(code))
            } catch (ex: NumberFormatException) {
                log.log(Level.FINE, "'$code' ist keine Zahl und wird als Datum behandelt:", ex)
                return of(toLocalDateTime(code))
            }
        }

        private fun toLocalDateTime(code: String): LocalDateTime {
            //val timePatterns = arrayOf("EEE MMM d HH:mm:ss zzz yyyy", "H:m:s", "H:m", "h:m", "K:m", "k:m")
            try {
                val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n")
                return LocalDateTime.parse(code, pattern)
            } catch (ex: DateTimeParseException) {
                throw LocalizedIllegalArgumentException(code, "unknown_time_format", ex)
            }
        }

        /**
         * Liefert einen Zeitpunkt zurueck.
         *
         * @param t beliebiger Zeitpunkt als LocalDateTime
         * @return der Zeitpunkt
         */
        @JvmStatic
        fun of(t: LocalDateTime): Zeitpunkt {
            return of(BigInteger.valueOf(t.toEpochSecond(ZoneOffset.UTC)).multiply(Zeitdauer.SECOND_IN_NANOS)
                .add(BigInteger.valueOf(t.nano.toLong())))
        }

        /**
         * Liefert den aktuellen Zeitpunkt zurueck.
         *
         * @return aktuellen Zeitpunkt
         */
        @JvmStatic
        fun now(): Zeitpunkt {
            return Zeitpunkt(currentTimeNanos())
        }

        private fun currentTimeNanos(): BigInteger {
            val now = LocalDateTime.now(ZoneOffset.UTC)
            return BigInteger.valueOf(now.toEpochSecond(ZoneOffset.UTC)).multiply(Zeitdauer.SECOND_IN_NANOS)
                .add(BigInteger.valueOf(now.nano.toLong()))
        }

        private fun toNanos(s: String): BigInteger {
            return try {
                BigInteger(s)
            } catch (ex: IllegalArgumentException) {
                log.log(Level.FINE, "'$s' ist keine Zahl und wird als Datum behandelt.")
                log.log(Level.FINER, "Details:", ex)
                dateToNanos(s)
            }
        }

        private fun dateToNanos(s: String): BigInteger {
            val dtfb = DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            val formatter = dtfb.toFormatter()
            val ldt = LocalDateTime.parse(s, formatter)
            val seconds = ldt.toEpochSecond(ZoneOffset.UTC)
            return BigInteger.valueOf(seconds).multiply(Zeitdauer.SECOND_IN_NANOS).add(BigInteger.valueOf(ldt.nano.toLong()))
        }

    }

}