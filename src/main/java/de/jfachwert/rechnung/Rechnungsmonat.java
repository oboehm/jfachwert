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

import de.jfachwert.*;
import de.jfachwert.pruefung.*;
import org.apache.commons.lang3.*;

import java.time.*;
import java.time.format.*;

/**
 * Vor allem bei Abonnements oder bei wiederkehrenden Gebuehren findet man
 * einen Rechnungsmonat auf der Rechnung. Hier ist nur Monat und Jahr relevant.
 * Entsprechend gibt es auch nur diese Attribute in dieser Klasse.
 *
 * @author oboehm
 * @since 0.3.1 (12.07.2017)
 */
public class Rechnungsmonat implements Fachwert {

    private static final Range<Integer> VALID_MONTH_RANGE = Range.between(1, 12);
    private static final Range<Integer> VALID_YEAR_RANGE = Range.between(0, 9999);
    private final int monat;
    private final int jahr;

    /**
     * Erzeugt einen gueltigen Rechnungsmonat.
     *
     * @param monat z.B. "7/2017" fuer Juli 2017
     */
    public Rechnungsmonat(String monat) {
        String[] parts = monat.split("/");
        this.monat = validate("month", parts[0], VALID_MONTH_RANGE);
        this.jahr = validate("year", parts[1], VALID_YEAR_RANGE);
    }

    /**
     * Erzeugt einen gueltigen Rechnungsmonat.
     *
     * @param monat zwischen 1 und 12
     * @param jahr vierstellige Zahl
     */
    public Rechnungsmonat(int monat, int jahr) {
        this(monat + "/" + jahr);
    }

    private static int validate(String context, String value, Range<Integer> range) {
        int number = Integer.parseInt(value);
        if (!range.contains(number)) {
            throw new InvalidValueException(value, context, range);
        }
        return number;
    }

    /**
     * Liefert den Monat zurueck.
     *
     * @return Zahl zwischen 1 und 12
     */
    public int getMonat() {
        return monat;
    }

    /**
     * Liefert das Jahr zurueck.
     *
     * @return vierstellige Zahl
     */
    public int getJahr() {
        return jahr;
    }

    /**
     * Liefert das Rechnungsatum als {@link LocalDate} zurueck.
     *
     * @return z.B. 1.7.2017 fuer "7/2017"
     */
    public LocalDate asLocalDate() {
        return LocalDate.of(getJahr(), getMonat(), 1);
    }

    /**
     * Hiermit kann der Rechnungsmonats im gewuenschten Format ausgegeben
     * werden. Als Parameter sind die gleichen Patterns wie beim
     * {@link java.text.SimpleDateFormat} moeglich.
     *
     * @param pattern z.B. "MM/yyyy"
     * @return z.B. "07/2017"
     */
    public String format(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
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
        return this.jahr * 12 + this.monat;
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
        return this.monat == other.monat && this.jahr == other.jahr;
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
