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
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.sql.Timestamp
import java.text.MessageFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.util.*
import java.util.concurrent.TimeUnit
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
 * Sie kann auch fuer die Konvertierung zwichen den verschiedenen Time- und
 * Date-Klassen verwendet werden:
 * <pre>
 *     LocalDate d = Zeitpunkt.of(new Date()).toLocalDate();
 * </pre>
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
     * Liefert den Zeitpunkt in Jahren zurueck. Auch hier hist der Bezugspunkt
     * der 1970. Das Namenschema von getTimeInMillis wurde hier beibeihaltn,
     * sodass auch diese Methode englisch-sprachig benannt ist.
     *
     * @return Zeit in Jahren seit 1970
     */
    fun getTimeInYears() : Long {
        return code.divide(Zeitdauer.YEAR_IN_NANOS).toLong()
    }

    /**
     * Liefert den Nano-Anteil der Sekunde
     *
     * @return Nano-Anteil, von 0 bis 999_999_999
     */
    fun getNanos(): Int {
        return code.mod(Zeitdauer.SECOND_IN_NANOS).toInt()
    }

    /**
     * Liefert die Anzahl der Sekunden seit dem 1.1.1970. Diese Methode
     * gilt nur fuer "normale" Zeitpunkte. Fuer groessere, weit entfernte
     * Zeitpunkte sollte man toEpochSecondExact nehmen.
     *
     * @return Anzahl Sekunden seit 1.1.1970 als Long-Wert
     */
    fun toEpochSecond(): Long {
        return toEpochSecondExact().toLong()
    }

    /**
     * Liefert die Anzahl der Sekunden seit dem 1.1.1970.
     *
     * @return Anzahl Sekunden seit 1.1.1970 als BigInteger
     */
    fun toEpochSecondExact(): BigInteger {
        return code.divide(Zeitdauer.SECOND_IN_NANOS)
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
     * Wandelt den Zeitpunkt in ein LocalDate um.
     *
     * @return LocalDate aus java.time
     */
    fun toLocalDate() : LocalDate {
        return toLocalDate(ZoneOffset.UTC)
    }

    /**
     * Wandelt den Zeitpunkt in ein LocalDate um.
     *
     * @param offset Offset zu UTC
     * @return LocalDate aus java.time
     */
    fun toLocalDate(offset: ZoneOffset) : LocalDate {
        return toLocalDateTime(offset).toLocalDate()
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
        if (isOutOfLocalDateTime()) {
            throw IllegalStateException("Zeitpunkt $code kann nicht auf LocalDateTime abgebildet werden.")
        }
        return LocalDateTime.ofEpochSecond(toEpochSecond(), getNanos(), offset)
    }

    private fun isOutOfLocalDateTime(): Boolean {
        val sec = toEpochSecondExact()
        return (sec.compareTo(BigInteger.valueOf(Integer.MIN_VALUE.toLong())) < 0)
                || (sec.compareTo(BigInteger.valueOf(Integer.MAX_VALUE.toLong())) > 0)
    }

    /**
     * Wandelt den Zeitpunkt in ein Date um.
     *
     * @return Date aus java.util
     */
    fun toDate() : Date {
        return Date(getTimeInMillis())
    }

    /**
     * Zeitpunkt wird als Zeit-/Datumsangabe ausgegeben.
     *
     * @return Ausgabe aehnlich wie bei Timestamp, aber Nonosekunden-genau
     */
    override fun toString(): String {
        return toShortString()
    }

    /**
     * Zeitpunkt wird als Zeit-/Datumsangabe ausgegeben.
     *
     * @return Ausgabe aehnlich wie bei Timestamp, aber Nonosekunden-genau
     */
    fun toLongString(): String {
        if (isOutOfLocalDateTime()) {
            return toStringInYears(getTimeInYears())
        } else {
            return toString("yyyy-MM-dd HH:mm:ss.n", ZoneOffset.UTC)
        }
    }

    private fun toStringInYears(years: Long): String {
        var jahre = years
        var bundleKey = "years_after"
        if (years < 0) {
            jahre = -years
            bundleKey = "years_before"
        }
        if (jahre < 1_000_000) {
            return getLocalizedMessage(bundleKey, jahre, "")
        }
        val nf = NumberFormat.getInstance()
        val mille = BigDecimal.valueOf(jahre).divide(BigDecimal.valueOf(1_000_000), 1, RoundingMode.HALF_DOWN)
        if (mille.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return getLocalizedMessage(bundleKey, nf.format(mille), getLocalizedString("million") + " ")
        }
        val mrd = mille.divide(BigDecimal.valueOf(1000), 1, RoundingMode.HALF_DOWN)
        return getLocalizedMessage(bundleKey, nf.format(mrd), getLocalizedString("billion") + " ")
    }

    /**
     * Wenn der Zeitpunkt der Tagesanfang ist (0:00), wird nur das Datum
     * ausgegeben. Ansonsten mit Uhrzeit in Kurzform, d.h. ohne 0-Werte
     * am Ende.
     *
     * @return String in Kurzform ohne Uhrzeit (wenn keine vorhanden).
     */
    fun toShortString(): String {
        var s = toLongString()
        if (!s.endsWith("000000")) {
            return s
        }
        s = s.substring(0, s.length-6)
        if (!s.endsWith(".000")) {
            return s
        }
        s = s.substring(0, s.length-4)
        if (!s.endsWith(":00")) {
            return s
        }
        s = s.substring(0, s.length-3)
        if (!s.endsWith("00:00")) {
            return s
        }
        return s.substring(0, s.length-5).trim()
    }

    /**
     * Zeitpunkt wird als Zeit-/Datumsanage ausgegeben.
     *
     * @param pattern Pattern fuer die Ausgabe
     * @param offset  Offset zu UTC
     * @return Default-Ausgabe aehnlich wie bei Timestamp, aber Nonosekunden-genau
     */
    fun toString(pattern : String = "yyyy-MM-dd HH:mm:ss.n", offset: ZoneOffset = ZoneOffset.UTC) : String {
        val datePattern = pattern.removeSuffix(".n")
        val dtfb = DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern(datePattern))
        val formatter = dtfb.toFormatter()
        val formattedDate = formatter.format(toLocalDateTime(offset))
        if (pattern.endsWith(".n")) {
            return String.format("%s.%09d", formattedDate, getNanos())
        } else {
            return formattedDate
        }
    }

    /**
     * Liefert den lokalisierten String aus dem [ResourceBundle]. Falls
     * dieser nicht existiert wird der Schluessel fuer die Resource selbst
     * als Rueckgabewert verwendet.
     *
     * @param key Resource-Schluessel
     * @return lokalisierter String
     */
    fun getLocalizedString(key: String): String {
        return try {
            BUNDLE.getString(key)
        } catch (ex: MissingResourceException) {
            logger.log(Level.FINE, "resource for $key not found", ex)
            key
        }
    }

    /**
     * Diese Methode sollte von [.getLocalizedMessage] aufgerufen
     * werden, damit das [ResourceBundle] fuer die lokalisierte
     * Message angezogen wird.
     *
     * @param key Eintrag aus messages.properties
     * @param args die einzelnen Arugmente zum 'key'
     * @return lokalisierter String
     */
    fun getLocalizedMessage(key: String, vararg args: Any?): String {
        return MessageFormat.format(getLocalizedString(key), *args)
    }



    companion object {

        private val log = Logger.getLogger(Zeitpunkt::class.java.name)
        private val WEAK_CACHE = WeakHashMap<BigInteger, Zeitpunkt>()
        private val logger: Logger = Logger.getLogger(Zeitpunkt::class.java.name)
        private val BUNDLE = ResourceBundle.getBundle("de.jfachwert.messages")
        /** Die Epoche beginnt am 1.1.1970. */
        @JvmField
        val EPOCH = Zeitpunkt(BigInteger.ZERO)
        /** Der minimale Zeitpunkt steht fuer den Urknall vor 13,8 Mrd. Jahren. */
        @JvmField
        val MIN = Zeitpunkt(BigInteger("-435500000000000000000000000"))
        /** Der maximale Zeitpunkt liegt weit in der Zukunft. */
        @JvmField
        val MAX = Zeitpunkt(BigInteger("999999999999999999999999999999"))

        /**
         * Liefert einen Zeitpunkt zurueck.
         *
         * @param code Anzahl ns seit 1.1.1970
         * @return der Zeitpunkt
         */
        @JvmStatic
        fun of(code: BigInteger): Zeitpunkt {
            return WEAK_CACHE.computeIfAbsent(code) { n: BigInteger -> Zeitpunkt(n) }
        }

        /**
         * Liefert einen Zeitpunkt zurueck.
         *
         * @param code Anzahl ns seit 1.1.1970
         * @param unit Zeiteinheit
         * @return der Zeitpunkt
         */
        @JvmStatic
        fun of(code: BigInteger, unit: TimeUnit): Zeitpunkt {
            return of(code.multiply(BigInteger.valueOf(unit.toNanos(1))))
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
                return of(dateToNanos(code))
            }
        }

        /**
         * Liefert einen Zeitpunkt zurueck.
         *
         * @param t beliebiger Zeitpunkt als LocalDate
         * @return der Zeitpunkt
         */
        @JvmStatic
        fun of(t: LocalDate): Zeitpunkt {
            return of(t.atStartOfDay())
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
         * Liefert einen Zeitpunkt zurueck.
         *
         * @param t beliebiger Zeitpunkt als Date
         * @return der Zeitpunkt
         */
        @JvmStatic
        fun of(t: Date): Zeitpunkt {
            val nanos = BigInteger.valueOf(t.time).multiply(Zeitdauer.MILLISECOND_IN_NANOS)
            return of(nanos)
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
            val datePatterns = arrayOf(
                "yyyy-MM-dd", "dd-MMM-yyyy", "dd-MM-yyyy", "yyyy-MMM-dd",
                "MMM-dd-yyyy", "dd MMM yyyy", "dd MM yyyy", "yyyy MMM dd", "yyyy MM dd", "MMM dd yyyy", "dd.MMM.yyyy",
                "dd.MM.yyyy", "yyyy.MMM.dd", "MMM.dd.yyyy"
            )
            val dtfb = DateTimeFormatterBuilder()
            for (dp in datePatterns) {
                dtfb
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.SSSSSSSSS"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.SSSSSSSS"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.SSSSSSS"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.SSSSSS"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.SSSSS"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.SSSS"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.SSS"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.SS"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s.S"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m:s"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp H:m"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp h:m"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp K:m"))
                    .appendOptional(DateTimeFormatter.ofPattern("$dp k:m"))
                    .appendOptional(DateTimeFormatter.ofPattern(dp))
            }
            dtfb
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            val formatter = dtfb.toFormatter()
            try {
                val ldt = LocalDateTime.parse(s, formatter)
                val seconds = ldt.toEpochSecond(ZoneOffset.UTC)
                return BigInteger.valueOf(seconds).multiply(Zeitdauer.SECOND_IN_NANOS)
                    .add(BigInteger.valueOf(ldt.nano.toLong()))
            } catch (ex: DateTimeParseException) {
                throw LocalizedIllegalArgumentException(s, "unknown_time_format", ex)
            }
        }

    }

}