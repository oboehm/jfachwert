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
package de.jfachwert.rechnung;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Vor allem bei Abonnements oder bei wiederkehrenden Gebuehren findet man
 * einen Rechnungsmonat auf der Rechnung. Hier ist nur Monat und Jahr relevant.
 * Entsprechend gibt es auch nur diese Attribute in dieser Klasse.
 * <p>
 * Der Gueltigkeitsbereich des Rechnungsjahres liegt ca. zwischen 2700 v. Chr.
 * (-2700) bis 2700 n. Chr. (+2700), da intern der Monat und das Jahr
 * speicheroptimiert in 2 Bytes abgelegt wird. Diese duerfte aber fuer die
 * meisten Faelle ausreichend sein.
 * </p>
 * <p>
 * Mit 0.8 implementiert diese Klasse auch die wichtigsten Methoden von
 * {@link LocalDate}. Sie kann damit anstatt der {@link LocalDate}-Klasse
 * eingesetzt werden, wenn Monats-Genauigkeit ausreicht.
 * </p>
 *
 * @author oboehm
 * @since 0.3.1 (12.07.2017)
 */
@JsonSerialize(using = ToStringSerializer.class)
public class Rechnungsmonat implements Fachwert {

    private static final Map<Short, Rechnungsmonat> CACHE = new WeakHashMap<>();
    private static final Range<Integer> VALID_MONTH_RANGE = Range.between(1, 12);
    private static final Range<Integer> VALID_YEAR_RANGE = Range.between(0, 9999);
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private final short monate;

    /**
     * Der Default-Konstruktor legt einen Rechnungsmonat vom aktuellen Monat
     * an.
     */
    public Rechnungsmonat() {
        this(LocalDate.now());
    }

    /**
     * Erzeugt einen gueltigen Rechnungsmonat anhand des uebergebenen
     * {@link LocalDate}s. Will man ein Rechnungsmonat ueber ein
     * {@link java.util.Date} anlegen, muss man es vorher mit
     * {@link java.sql.Date#toLocalDate()} in ein {@link LocalDate} wandeln.
     *
     * @param date Datum
     */
    public Rechnungsmonat(LocalDate date) {
        this(date.getMonthValue(), date.getYear());
    }

    /**
     * Erzeugt einen gueltigen Rechnungsmonat. Normalerweise sollte der
     * Monat als "7/2017" angegeben werden, es werden aber auch andere
     * Formate wie "Jul-2017" oder "2017-07-14" unterstuetzt.
     * <p>
     * Auch wenn "Jul-2017" und andere Formate als gueltiger Rechnungsmonat
     * erkannt werden, sollte man dies nur vorsichtig einsetzen, da hier mit
     * Brute-Force einfach nur geraten wird, welches Format es sein koennte.
     * </p>
     * 
     * @param monat z.B. "7/2017" fuer Juli 2017
     */
    public Rechnungsmonat(String monat) {
        String[] parts = monat.split("/");
        if ((parts.length == 2) && isDigit(parts[0]) && isDigit(parts[1])) {
            this.monate =
                    asMonate(validate(MONTH, parts[0], VALID_MONTH_RANGE), validate(YEAR, parts[1], VALID_YEAR_RANGE));
        } else {
            LocalDate date = toLocalDate(monat);
            this.monate = asMonate(date.getMonthValue(), date.getYear());
        }
    }
    
    private static short asMonate(int monat, int jahr) {
        return (short) (jahr * 12 + monat - 1);
    }
    
    private Rechnungsmonat(int monate) {
        this.monate = (short) monate;
    }

    /**
     * Erzeugt einen gueltigen Rechnungsmonat.
     *
     * @param monat zwischen 1 und 12
     * @param jahr vierstellige Zahl zwischen -2730 und +2730
     */
    public Rechnungsmonat(int monat, int jahr) {
        this(monat + "/" + jahr);
    }

    /**
     * Erzeugt einen gueltigen Rechnungsmonat.
     *
     * @param monat MOnat
     * @param jahr vierstellige Zahl
     */
    public Rechnungsmonat(Month monat, int jahr) {
        this(monat.getValue(), jahr);
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
    public static Rechnungsmonat of(LocalDate date) {
        return of(new Rechnungsmonat(date));
    }

    /**
     * Die of-Methode liefert fuer denselben Rechnungsmonata auch dasselbe
     * Objekt zurueck. D.h. zwei gleiche Rechnungsmonate werden nur einmal
     * angelegt, wenn sie ueber diese Methode angelegt werden. Das lohnt sich
     * vor allem dann, wenn man viele gleiche Rechnungsmonate hat und sich den
     * Overhead eines Objekts sparen will.
     *
     * @param datum Datum
     * @return einen Rechnungsmonat
     */
    public static Rechnungsmonat of(String datum) {
        return of(new Rechnungsmonat(datum));
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
    public static Rechnungsmonat of(int monat, int jahr) {
        return of(new Rechnungsmonat(monat, jahr));
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
    public static Rechnungsmonat of(Month monat, int jahr) {
        return of(new Rechnungsmonat(monat, jahr));
    }

    /**
     * Die of-Methode liefert fuer denselben Rechnungsmonata auch dasselbe
     * Objekt zurueck. D.h. zwei gleiche Rechnungsmonate werden nur einmal
     * angelegt, wenn sie ueber diese Methode angelegt werden. Das lohnt sich
     * vor allem dann, wenn man viele gleiche Rechnungsmonate hat und sich den
     * Overhead eines Objekts sparen will.
     * <p>
     * Diese Methode dient dazu, um ein "ueberfluessige" Rechnungsmonate, die
     * durch Aufruf anderer Methoden entstanden sind, dem Garbage Collector
     * zum Aufraeumen zur Verfuegung zu stellen.
     * </p>
     *
     * @param other anderer Rechnungsmonat
     * @return einen (bereits instanziierten) Rechnungsmonat
     */
    public static Rechnungsmonat of(Rechnungsmonat other) {
        Short key = other.monate;
        Rechnungsmonat alreadyCreated = CACHE.get(key);
        if (alreadyCreated == null) {
            alreadyCreated = other;
            CACHE.put(key, other);
        }
        return alreadyCreated;
    }

    private static LocalDate toLocalDate(String monat) {
        String normalized = monat.replaceAll("[/.\\s]", "-");
        String[] parts = monat.split("-");
        if (parts.length == 2) {
            normalized = "1-" + normalized;
        } else if (parts.length != 3) {
            throw new InvalidValueException(monat, MONTH);
        }
        try {
            return LocalDate.parse(normalized);
        } catch (DateTimeParseException ex) {
            return guessLocalDate(normalized, ex);
        }
    }

    private static LocalDate guessLocalDate(String monat, DateTimeParseException ex) {
        String[] datePatterns = {"d-MMM-yyyy", "d-MM-yyyy", "yyyy-MMM-d", "yyyy-MM-d", "MMM-d-yyyy"};
        for (String pattern : datePatterns) {
            for (Locale locale : Locale.getAvailableLocales()) {
                try {
                    return LocalDate.parse(monat, DateTimeFormatter.ofPattern(pattern, locale));
                } catch (DateTimeParseException ignored) {
                    ex.addSuppressed(new IllegalArgumentException(
                            ignored.getMessage() + " / '" + monat + "' does not match '" + pattern + "'"));
                }
            }
        }
        throw new InvalidValueException(monat, MONTH, ex);
    }

    private static int validate(String context, String value, Range<Integer> range) {
        int number = Integer.parseInt(value);
        if (!range.contains(number)) {
            throw new InvalidValueException(value, context, range);
        }
        return number;
    }

    private static boolean isDigit(String number) {
        return StringUtils.isNumeric(number);
    }

    /**
     * Liefert den Monat zurueck.
     *
     * @return Zahl zwischen 1 und 12
     */
    public int getMonat() {
        return (monate % 12) + 1;
    }

    /**
     * Liefert das Jahr zurueck.
     *
     * @return vierstellige Zahl
     */
    public int getJahr() {
        return monate / 12;
    }

    /**
     * Liefert den Vormonat.
     *
     * @return Vormonat
     */
    public Rechnungsmonat getVormonat() {
        return new Rechnungsmonat(monate - 1);
    }

    /**
     * Liefert den Folgemonat.
     *
     * @return Folgemonat
     */
    public Rechnungsmonat getFolgemonat() {
        return new Rechnungsmonat(monate + 1);
    }

    /**
     * Liefert den gleichen Monat im Vorjahr.
     *
     * @return Monat im Vorjahr
     */
    public Rechnungsmonat getVorjahr() {
        return new Rechnungsmonat(monate - 12);
    }

    /**
     * Liefert den gleichen Monat im Folgejahr.
     *
     * @return Monat im Folgejahr
     */
    public Rechnungsmonat getFolgejahr() {
        return new Rechnungsmonat(monate + 12);
    }

    /**
     * Liefert den ersten Tag eines Rechnungsmonats.
     * 
     * @return z.B. 1.3.2018
     * @since 0.6
     */
    public LocalDate ersterTag() {
        return LocalDate.of(getJahr(), getMonat(), 1);
    }

    /**
     * Diese Methode kann verwendet werden, um den ersten Montag im Monat
     * zu bestimmen. Dazu ruft man diese Methode einfach mit
     * {@link DayOfWeek#MONDAY} als Parameter auf.
     * 
     * @param wochentag z.B. {@link DayOfWeek#MONDAY}
     * @return z.B. erster Arbeitstag
     * @since 0.6
     */
    public LocalDate ersterTag(DayOfWeek wochentag) {
        LocalDate tag = ersterTag();
        while (tag.getDayOfWeek() != wochentag) {
            tag = tag.plusDays(1);
        }
        return tag;
    }

    /**
     * Diese Methode liefert den ersten Arbeitstag eines Monats. Allerdings
     * werden dabei keine Feiertag beruecksichtigt, sondern nur die Wochenende,
     * die auf einen ersten des Monats fallen, werden berucksichtigt.
     * 
     * @return erster Arbeitstag
     * @since 0.6
     */
    public LocalDate ersterArbeitstag() {
        LocalDate tag = ersterTag();
        switch (tag.getDayOfWeek()) {
            case SATURDAY:
                return tag.plusDays(2);
            case SUNDAY:
                return tag.plusDays(1);
            default:
                return tag;
        }
    }

    /**
     * Liefert den letzten Tag eines Rechnungsmonats.
     *
     * @return z.B. 31.3.2018
     * @since 0.6
     */
    public LocalDate letzterTag() {
        return getFolgemonat().ersterTag().minusDays(1);
    }

    /**
     * Diese Methode kann verwendet werden, um den letzten Freitag im Monat
     * zu bestimmen. Dazu ruft man diese Methode einfach mit
     * {@link DayOfWeek#FRIDAY} als Parameter auf.
     *
     * @param wochentag z.B. {@link DayOfWeek#FRIDAY}
     * @return z.B. letzter Arbeitstag
     * @since 0.6
     */
    public LocalDate letzterTag(DayOfWeek wochentag) {
        LocalDate tag = ersterTag();
        while (tag.getDayOfWeek() != wochentag) {
            tag = tag.minusDays(1);
        }
        return tag;
    }

    /**
     * Diese Methode liefert den letzten Arbeitstag eines Monats. Allerdings
     * werden dabei keine Feiertag beruecksichtigt, sondern nur die Wochenende,
     * die auf einen letzten des Monats fallen, werden berucksichtigt.
     *
     * @return letzter Arbeitstag
     * @since 0.6
     */
    public LocalDate letzterArbeitstag() {
        LocalDate tag = letzterTag();
        switch (tag.getDayOfWeek()) {
            case SATURDAY:
                return tag.minusDays(1);
            case SUNDAY:
                return tag.minusDays(2);
            default:
                return tag;
        }
    }

    /**
     * Liefert das Rechnungsatum als {@link LocalDate} zurueck. Sollte das
     * Datum als {@link java.util.Date} benoetigt werden, kann man es mit
     * {@link java.sql.Date#valueOf(LocalDate)} konvertieren.
     *
     * @return z.B. 1.7.2017 fuer "7/2017"
     */
    public LocalDate asLocalDate() {
        return ersterTag();
    }

    /**
     * Diese Methode liefert den Rechnungsmonat, der um 'yearsToAdd' in der
     * Zukunft liegt. Sie dient dazu, um den Rechnungsmonat auch als Ersatz
     * fuer {@link LocalDate} verwenden zu koennen. Deswegen ist der
     * Methodennamen auf Englisch.
     *
     * @param yearsToAdd Anzahl Jahre, die aufaddiert werden
     * @return neuen Rechnungsmonat
     * @since 1.0
     * @see LocalDate#plusYears(long)
     */
    public Rechnungsmonat plusYears(int yearsToAdd) {
        return plusMonths(yearsToAdd * 12);
    }

    /**
     * Diese Methode liefert den Monat, der um 'monthsToAdd' in der Zukunft
     * liegt. Sie dient dazu, um den Rechnungsmonat auch als Ersatz fuer
     * {@link LocalDate} verwenden zu koennen. Deswegen ist der
     * Methodennamen auf Englisch.
     *
     * @param monthsToAdd Anzahl Monate, die aufaddiert werden
     * @return neuen Rechnungsmonat
     * @since 1.0
     * @see LocalDate#plusMonths(long)
     */
    public Rechnungsmonat plusMonths(int monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        } else {
            return new Rechnungsmonat(monate + monthsToAdd);
        }
    }

    /**
     * Diese Methode liefert den Rechnungsmonat, der um 'yeara' zurueck
     * liegt. Sie dient dazu, um den Rechnungsmonat auch als Ersatz
     * fuer {@link LocalDate} verwenden zu koennen. Deswegen ist der
     * Methodennamen auf Englisch.
     *
     * @param years Anzahl Jahre, die subtrahiert werden
     * @return neuen Rechnungsmonat
     * @since 1.0
     * @see LocalDate#minusYears(long)
     */
    public Rechnungsmonat minusYears(int years) {
        return plusYears(-years);
    }

    /**
     * Diese Methode liefert den Monat, der um 'months' zurueck
     * liegt. Sie dient dazu, um den Rechnungsmonat auch als Ersatz fuer
     * {@link LocalDate} verwenden zu koennen. Deswegen ist der
     * Methodennamen auf Englisch.
     *
     * @param months Anzahl Monate, die subtrahiert werden
     * @return neuen Rechnungsmonat
     * @since 1.0
     * @see LocalDate#minusMonths(long)
     */
    public Rechnungsmonat minusMonths(int months) {
        return plusMonths(-months);
    }

    /**
     * Hiermit kann der Rechnungsmonats im gewuenschten Format ausgegeben
     * werden. Als Parameter sind die gleichen Patterns wie beim
     * {@link DateTimeFormatter#ofPattern(String, java.util.Locale)} bzw.
     * {@link java.text.SimpleDateFormat} moeglich.
     *
     * @param pattern z.B. "MM/yyyy"
     * @return z.B. "07/2017"
     */
    public String format(String pattern) {
        return format(pattern, Locale.getDefault());
    }

    /**
     * Hiermit kann der Rechnungsmonats im gewuenschten Format ausgegeben
     * werden. Als Parameter sind die gleichen Patterns wie beim
     * {@link DateTimeFormatter#ofPattern(String, Locale)} bzw.
     * {@link java.text.SimpleDateFormat} moeglich.
     *
     * @param pattern z.B. "MM/yyyy"
     * @param locale gewuenschte Locale
     * @return z.B. "07/2017"
     */
    public String format(String pattern, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
        return asLocalDate().format(formatter);
    }

    /**
     * Als Hashcode nehmen wir einfach die Nummer des Monats seit Christi
     * Geburt.
     *
     * @return Nummer des Monats seit 1.1.0000
     */
    @Override
    public int hashCode() {
        return this.monate;
    }

    /**
     * Zwei Rechnungsmonat sind gleich, wenn Monat und Jahr gleich sind.
     *
     * @param obj Vergleichsmonat
     * @return true bei Gleichheit
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Rechnungsmonat)) {
            return false;
        }
        Rechnungsmonat other = (Rechnungsmonat) obj;
        return this.monate == other.monate;
    }

    /**
     * Als Ausgabe nehmen wir "7/2017" fuer Juli 2017.
     *
     * @return z.B. "7/2017"
     */
    @Override
    public String toString() {
        return this.getMonat() + "/" + this.getJahr();
    }

}
