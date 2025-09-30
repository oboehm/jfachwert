/*
 * Copyright (c) 2017 by Oliver Boehm
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
 * (c)reated 12.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.rechnung

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.KFachwert
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import org.apache.commons.lang3.Range
import org.apache.commons.lang3.StringUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

/**
 * Vor allem bei Abonnements oder bei wiederkehrenden Gebuehren findet man
 * einen Rechnungsmonat auf der Rechnung. Hier ist nur Monat und Jahr relevant.
 * Entsprechend gibt es auch nur diese Attribute in dieser Klasse.
 *
 * Der Gueltigkeitsbereich des Rechnungsjahres liegt ca. zwischen 2700 v. Chr.
 * (-2700) bis 2700 n. Chr. (+2700), da intern der Monat und das Jahr
 * speicheroptimiert in 2 Bytes abgelegt wird. Diese duerfte aber fuer die
 * meisten Faelle ausreichend sein.
 *
 * Mit 0.8 implementiert diese Klasse auch die wichtigsten Methoden von
 * [LocalDate]. Sie kann damit anstatt der [LocalDate]-Klasse
 * eingesetzt werden, wenn Monats-Genauigkeit ausreicht.
 *
 * @author oboehm
 * @since 0.3.1 (12.07.2017)
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Rechnungsmonat : KFachwert, Comparable<Rechnungsmonat> {

    private val monate: Short

    /**
     * Erzeugt einen gueltigen Rechnungsmonat anhand des uebergebenen
     * [LocalDate]s. Will man ein Rechnungsmonat ueber ein
     * [java.util.Date] anlegen, muss man es vorher mit
     * [java.sql.Date.toLocalDate] in ein [LocalDate] wandeln.
     *
     * Der Default-Konstruktor legt einen Rechnungsmonat vom aktuellen Monat
     * an.
     *
     * @param date Datum
     */
    @JvmOverloads
    constructor(date: LocalDate = LocalDate.now()) : this(date.monthValue, date.year)

    /**
     * Erzeugt einen gueltigen Rechnungsmonat. Normalerweise sollte der
     * Monat als "7/2017" angegeben werden, es werden aber auch andere
     * Formate wie "Jul-2017" oder "2017-07-14" unterstuetzt.
     *
     * Auch wenn "Jul-2017" und andere Formate als gueltiger Rechnungsmonat
     * erkannt werden, sollte man dies nur vorsichtig einsetzen, da hier mit
     * Brute-Force einfach nur geraten wird, welches Format es sein koennte.
     *
     * @param monat z.B. "7/2017" fuer Juli 2017
     */
    constructor(monat: String) {
        val parts = monat.split("/").toTypedArray()
        monate = if (parts.size == 2 && isDigit(parts[0]) && isDigit(parts[1])) {
            //asMonate(verify(MONTH, parts[0], VALID_MONTH_RANGE), verify(YEAR, parts[1], VALID_YEAR_RANGE))
            asMonate(parts[0].toInt(), parts[1].toInt())
        } else {
            val date = toLocalDate(monat)
            asMonate(date.monthValue, date.year)
        }
    }

    private constructor(monate: Int) {
        this.monate = monate.toShort()
    }

    /**
     * Erzeugt einen gueltigen Rechnungsmonat.
     *
     * @param monat zwischen 1 und 12
     * @param jahr vierstellige Zahl zwischen -2730 und +2730
     */
    constructor(monat: Int, jahr: Int) : this(asMonate(monat, jahr).toInt())

    /**
     * Erzeugt einen gueltigen Rechnungsmonat.
     *
     * @param monat MOnat
     * @param jahr vierstellige Zahl
     */
    constructor(monat: Month, jahr: Int) : this(monat.value, jahr)

    /**
     * Liefert den Abrechnungsmonat als Anzahl Monate zurueck.
     *
     * @return Anzahl Monate seit Christi Geburt
     * @since 2.1
     */
    fun asMonate(): Int {
        return monate + 1
    }

    /**
     * Liefert den Monat zurueck.
     *
     * @return Zahl zwischen 1 und 12
     */
    val monat: Int
        get() = monate % 12 + 1

    /**
     * Liefert das Jahr zurueck.
     *
     * @return vierstellige Zahl
     */
    val jahr: Int
        get() = monate / 12

    /**
     * Liefert den Vormonat.
     *
     * @return Vormonat
     */
    val vormonat: Rechnungsmonat
        get() = of(monate - 1)

    /**
     * Liefert den Folgemonat.
     *
     * @return Folgemonat
     */
    val folgemonat: Rechnungsmonat
        get() = of(monate + 1)

    /**
     * Liefert den gleichen Monat im Vorjahr.
     *
     * @return Monat im Vorjahr
     */
    val vorjahr: Rechnungsmonat
        get() = of(monate - 12)

    /**
     * Liefert den gleichen Monat im Folgejahr.
     *
     * @return Monat im Folgejahr
     */
    val folgejahr: Rechnungsmonat
        get() = of(monate + 12)

    /**
     * Liefert den ersten Tag eines Rechnungsmonats.
     *
     * @return z.B. 1.3.2018
     * @since 0.6
     */
    fun ersterTag(): LocalDate {
        return LocalDate.of(jahr, monat, 1)
    }

    /**
     * Diese Methode kann verwendet werden, um den ersten Montag im Monat
     * zu bestimmen. Dazu ruft man diese Methode einfach mit
     * [DayOfWeek.MONDAY] als Parameter auf.
     *
     * @param wochentag z.B. [DayOfWeek.MONDAY]
     * @return z.B. erster Arbeitstag
     * @since 0.6
     */
    fun ersterTag(wochentag: DayOfWeek): LocalDate {
        var tag = ersterTag()
        while (tag.dayOfWeek != wochentag) {
            tag = tag.plusDays(1)
        }
        return tag
    }

    /**
     * Diese Methode liefert den ersten Arbeitstag eines Monats. Allerdings
     * werden dabei keine Feiertag beruecksichtigt, sondern nur die Wochenende,
     * die auf einen ersten des Monats fallen, werden berucksichtigt.
     *
     * @return erster Arbeitstag
     * @since 0.6
     */
    fun ersterArbeitstag(): LocalDate {
        val tag = ersterTag()
        return when (tag.dayOfWeek) {
            DayOfWeek.SATURDAY -> tag.plusDays(2)
            DayOfWeek.SUNDAY -> tag.plusDays(1)
            else -> tag
        }
    }

    /**
     * Liefert den letzten Tag eines Rechnungsmonats.
     *
     * @return z.B. 31.3.2018
     * @since 0.6
     */
    fun letzterTag(): LocalDate {
        return folgemonat.ersterTag().minusDays(1)
    }

    /**
     * Diese Methode kann verwendet werden, um den letzten Freitag im Monat
     * zu bestimmen. Dazu ruft man diese Methode einfach mit
     * [DayOfWeek.FRIDAY] als Parameter auf.
     *
     * @param wochentag z.B. [DayOfWeek.FRIDAY]
     * @return z.B. letzter Arbeitstag
     * @since 0.6
     */
    fun letzterTag(wochentag: DayOfWeek): LocalDate {
        var tag = ersterTag()
        while (tag.dayOfWeek != wochentag) {
            tag = tag.minusDays(1)
        }
        return tag
    }

    /**
     * Diese Methode liefert den letzten Arbeitstag eines Monats. Allerdings
     * werden dabei keine Feiertag beruecksichtigt, sondern nur die Wochenende,
     * die auf einen letzten des Monats fallen, werden berucksichtigt.
     *
     * @return letzter Arbeitstag
     * @since 0.6
     */
    fun letzterArbeitstag(): LocalDate {
        val tag = letzterTag()
        return when (tag.dayOfWeek) {
            DayOfWeek.SATURDAY -> tag.minusDays(1)
            DayOfWeek.SUNDAY -> tag.minusDays(2)
            else -> tag
        }
    }

    /**
     * Liefert das Rechnungsatum als [LocalDate] zurueck. Sollte das
     * Datum als [java.util.Date] benoetigt werden, kann man es mit
     * [java.sql.Date.valueOf] konvertieren.
     *
     * @return z.B. 1.7.2017 fuer "7/2017"
     */
    fun asLocalDate(): LocalDate {
        return ersterTag()
    }

    /**
     * Diese Methode liefert den Rechnungsmonat, der um 'yearsToAdd' in der
     * Zukunft liegt. Sie dient dazu, um den Rechnungsmonat auch als Ersatz
     * fuer [LocalDate] verwenden zu koennen. Deswegen ist der
     * Methodennamen auf Englisch.
     *
     * @param yearsToAdd Anzahl Jahre, die aufaddiert werden
     * @return neuen Rechnungsmonat
     * @since 1.0
     * @see LocalDate.plusYears
     */
    fun plusYears(yearsToAdd: Int): Rechnungsmonat {
        return plusMonths(yearsToAdd * 12)
    }

    /**
     * Diese Methode liefert den Monat, der um 'monthsToAdd' in der Zukunft
     * liegt. Sie dient dazu, um den Rechnungsmonat auch als Ersatz fuer
     * [LocalDate] verwenden zu koennen. Deswegen ist der
     * Methodennamen auf Englisch.
     *
     * @param monthsToAdd Anzahl Monate, die aufaddiert werden
     * @return neuen Rechnungsmonat
     * @since 1.0
     * @see LocalDate.plusMonths
     */
    fun plusMonths(monthsToAdd: Int): Rechnungsmonat {
        return if (monthsToAdd == 0) {
            this
        } else {
            of(monate + monthsToAdd)
        }
    }

    /**
     * Diese Methode liefert den Rechnungsmonat, der um 'years' zurueck
     * liegt. Sie dient dazu, um den Rechnungsmonat auch als Ersatz
     * fuer [LocalDate] verwenden zu koennen. Deswegen ist der
     * Methodennamen auf Englisch.
     *
     * @param years Anzahl Jahre, die subtrahiert werden
     * @return neuen Rechnungsmonat
     * @since 1.0
     * @see LocalDate.minusYears
     */
    fun minusYears(years: Int): Rechnungsmonat {
        return plusYears(-years)
    }

    /**
     * Diese Methode liefert den Monat, der um 'months' zurueck
     * liegt. Sie dient dazu, um den Rechnungsmonat auch als Ersatz fuer
     * [LocalDate] verwenden zu koennen. Deswegen ist der
     * Methodennamen auf Englisch.
     *
     * @param months Anzahl Monate, die subtrahiert werden
     * @return neuen Rechnungsmonat
     * @since 1.0
     * @see LocalDate.minusMonths
     */
    fun minusMonths(months: Int): Rechnungsmonat {
        return plusMonths(-months)
    }

    /**
     * Hiermit kann der Rechnungsmonat im gewuenschten Format ausgegeben
     * werden. Als Parameter sind die gleichen Patterns wie beim
     * [DateTimeFormatter.ofPattern] bzw.
     * [java.text.SimpleDateFormat] moeglich.
     *
     * @param pattern z.B. "MM/yyyy"
     * @return z.B. "07/2017"
     */
    @JvmOverloads
    fun format(pattern: String?, locale: Locale? = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return asLocalDate().format(formatter)
    }

    /**
     * Als Hashcode nehmen wir einfach die Nummer des Monats seit Christi
     * Geburt.
     *
     * @return Nummer des Monats seit 1.1.0000
     */
    override fun hashCode(): Int {
        return monate.toInt()
    }

    /**
     * Zwei Rechnungsmonat sind gleich, wenn Monat und Jahr gleich sind.
     *
     * @param other Vergleichsmonat
     * @return true bei Gleichheit
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Rechnungsmonat) {
            return false
        }
        return monate == other.monate
    }

    /**
     * Als Ausgabe nehmen wir "7/2017" fuer Juli 2017.
     *
     * @return z.B. "7/2017"
     */
    override fun toString(): String {
        return monat.toString() + "/" + jahr
    }

    /**
     * Vergleicht zwei Rechnungsmonate.
     *
     * @param other der andere Rechnnugsmonat
     * @return kleiner 0, wenn der andere Rechnungsmonat davor liegt
     */
    override fun compareTo(other: Rechnungsmonat): Int {
        return monate - other.monate
    }

    companion object {

        private val WEAK_CACHE: MutableMap<Short, Rechnungsmonat> = WeakHashMap()
        private val VALID_MONTH_RANGE = Range.of(1, 12)
        private val VALID_YEAR_RANGE = Range.of(0, 9999)
        private const val MONTH = "month"
        private const val YEAR = "year"
        /** Null-Monat fuer Initialisierungen.  */
        @JvmField
        val NULL = of(0)

        @JvmStatic
        private fun asMonate(monat: Int, jahr: Int): Short {
            if ((monat > 999) && (jahr < 13)) {
                return asMonate(jahr, monat)
            }
            verify(MONTH, monat, VALID_MONTH_RANGE)
            verify(YEAR, jahr, VALID_YEAR_RANGE)
            return (jahr * 12 + monat - 1).toShort()
        }

        /**
         * Liefert den aktuellen Rechnungsmonat.
         *
         * @return den aktuellen Rechnungsmonat
         * @since 3.1
         */
        @JvmStatic
        fun now() : Rechnungsmonat {
            return of(LocalDate.now())
        }

        /**
         * Die of-Methode liefert fuer denselben Rechnungsmonata auch dasselbe
         * Objekt zurueck. D.h. zwei gleiche Rechnungsmonate werden nur einmal
         * angelegt, wenn sie ueber diese Methode angelegt werden. Das lohnt sich
         * vor allem dann, wenn man viele gleiche Rechnungsmonate hat und sich den
         * Overhead eines Objekts sparen will.
         *
         * @param date Datum
         * @return einen Rechnungsmonat
         */
        @JvmStatic
        fun of(date: LocalDate): Rechnungsmonat {
            return of(Rechnungsmonat(date).monate.toInt())
        }

        /**
         * Die of-Methode liefert fuer denselben Rechnungsmonat auch dasselbe
         * Objekt zurueck. D.h. zwei gleiche Rechnungsmonate werden nur einmal
         * angelegt, wenn sie ueber diese Methode angelegt werden. Das lohnt sich
         * vor allem dann, wenn man viele gleiche Rechnungsmonate hat und sich den
         * Overhead eines Objekts sparen will.
         *
         * @param datum Datum
         * @return einen Rechnungsmonat
         */
        @JvmStatic
        fun of(datum: String): Rechnungsmonat {
            return of(Rechnungsmonat(datum).monate.toInt())
        }

        /**
         * Die of-Methode liefert fuer denselben Rechnungsmonata auch dasselbe
         * Objekt zurueck. D.h. zwei gleiche Rechnungsmonate werden nur einmal
         * angelegt, wenn sie ueber diese Methode angelegt werden. Das lohnt sich
         * vor allem dann, wenn man viele gleiche Rechnungsmonate hat und sich den
         * Overhead eines Objekts sparen will.
         *
         * @param monat zwischen 1 und 12
         * @param jahr vierstellige Zahl zwischen -2730 und +2730
         * @return einen Rechnungsmonat
         */
        @JvmStatic
        fun of(monat: Int, jahr: Int): Rechnungsmonat {
            return of(asMonate(monat, jahr).toInt())
        }

        /**
         * Die of-Methode liefert fuer denselben Rechnungsmonata auch dasselbe
         * Objekt zurueck. D.h. zwei gleiche Rechnungsmonate werden nur einmal
         * angelegt, wenn sie ueber diese Methode angelegt werden. Das lohnt sich
         * vor allem dann, wenn man viele gleiche Rechnungsmonate hat und sich den
         * Overhead eines Objekts sparen will.
         *
         * @param monat Monat
         * @param jahr vierstellige Zahl zwischen -2730 und +2730
         * @return einen Rechnungsmonat
         */
        @JvmStatic
        fun of(monat: Month, jahr: Int): Rechnungsmonat {
            return of(monat.value, jahr)
        }

        /**
         * Die of-Methode liefert fuer denselben Rechnungsmonata auch dasselbe
         * Objekt zurueck. D.h. zwei gleiche Rechnungsmonate werden nur einmal
         * angelegt, wenn sie ueber diese Methode angelegt werden. Das lohnt sich
         * vor allem dann, wenn man viele gleiche Rechnungsmonate hat und sich den
         * Overhead eines Objekts sparen will.
         *
         * @param monate Anzahl Monate seit Christi Geburt
         * @return einen Rechnungsmonat
         * @since 2.2.2
         */
        @JvmStatic
        fun of(monate: Int): Rechnungsmonat {
            return WEAK_CACHE.computeIfAbsent(monate.toShort()) { m: Short -> Rechnungsmonat(m.toInt()) }
        }

        private fun toLocalDate(monat: String): LocalDate {
            var normalized = monat.replace("[/.\\s]".toRegex(), "-")
            val parts = monat.split("-").toTypedArray()
            if (parts.size == 2) {
                normalized = "1-$normalized"
            } else if (parts.size != 3) {
                throw LocalizedIllegalArgumentException(monat, MONTH)
            }
            return try {
                LocalDate.parse(normalized)
            } catch (ex: DateTimeParseException) {
                guessLocalDate(normalized, ex)
            }
        }

        private fun guessLocalDate(monat: String, ex: DateTimeParseException): LocalDate {
            val datePatterns = arrayOf("d-MMM-yyyy", "d-MM-yyyy", "yyyy-MMM-d", "yyyy-MM-d", "MMM-d-yyyy")
            for (pattern in datePatterns) {
                for (locale in Locale.getAvailableLocales()) {
                    try {
                        return LocalDate.parse(monat, DateTimeFormatter.ofPattern(pattern, locale))
                    } catch (ignored: DateTimeParseException) {
                        ex.addSuppressed(IllegalArgumentException(
                                ignored.message + " / '" + monat + "' does not match '" + pattern + "'"))
                    }
                }
            }
            throw InvalidValueException(monat, MONTH, ex)
        }

        private fun verify(context: String, number: Int, range: Range<Int>): Int {
            if (!range.contains(number)) {
                throw LocalizedIllegalArgumentException(number, context, range)
            }
            return number
        }

        private fun isDigit(number: String): Boolean {
            return StringUtils.isNumeric(number)
        }
    }

}