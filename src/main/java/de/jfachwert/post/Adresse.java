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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.InvalidValueException;
import org.apache.commons.lang3.StringUtils;

/**
 * Bei einer Adresse kann es sich um eine Wohnungsadresse oder Gebaeudeadresse
 * handeln. Sie besteht aus Ort, Strasse und Hausnummer. Sie unterscheidet sich
 * insofern von einer Anschrift, da der Name nicht Bestandteil der Adresse ist.
 *
 * @author oboehm
 * @since 0.2 (02.05.2017)
 */
public class Adresse implements Fachwert {

    private final Ort ort;
    private final String strasse;
    private final String hausnummer;

    /**
     * Erzeugt eine neue Adresse.
     *
     * @param ort        the ort
     * @param strasse    the strasse
     * @param hausnummer the hausnummer
     */
    public Adresse(Ort ort, String strasse, String hausnummer) {
        this.ort = ort;
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        validate(ort, strasse, hausnummer);
    }

    /**
     * Validiert die uebergebene Adresse auf moegliche Fehler.
     *
     * @param ort        der Ort
     * @param strasse    die Strasse
     * @param hausnummer die Hausnummer
     */
    public static void validate(Ort ort, String strasse, String hausnummer) {
        if (!ort.getPLZ().isPresent()) {
            throw new InvalidValueException(ort, "postal_code");
        }
        if (StringUtils.isBlank(strasse)) {
            throw new InvalidValueException(strasse, "street");
        }
        if (StringUtils.isBlank(hausnummer)) {
            throw new InvalidValueException(hausnummer, "house_number");
        }
        if (Character.isDigit(strasse.trim().charAt(0)) && (Character.isLetter(hausnummer.trim().charAt(0)))
                && (strasse.length() < hausnummer.length())) {
            throw new InvalidValueException(strasse + " " + hausnummer, "values_exchanged");
        }
    }

    /**
     * Liefert den Ort.
     *
     * @return Ort
     */
    public Ort getOrt() {
        return ort;
    }

    /**
     * Eine PLZ muss fuer eine Adresse vorhanden sein. Diese wird hierueber
     * zurueckgegeben.
     *
     * @return the plz
     */
    public PLZ getPLZ() {
        return ort.getPLZ().get();
    }

    /**
     * Liefert die Strasse.
     *
     * @return the strasse
     */
    public String getStrasse() {
        return strasse;
    }

    /**
     * Liefert die Strasse.
     *
     * @return Hausnummer, z.B. "10a"
     */
    public String getHausnummer() {
        return hausnummer;
    }

    /**
     * Beim Vergleich wird zwischen Gross- und Kleinschreibung nicht
     * unterschieden.
     *
     * @param obj die andere Adresse
     * @return true oder false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Adresse)) {
            return false;
        }
        Adresse other = (Adresse) obj;
        return this.ort.equals(other.ort) && (this.strasse.equalsIgnoreCase(other.strasse))
                && this.hausnummer.equalsIgnoreCase(other.hausnummer);
    }

    /**
     * Fokus dieser hashCode-Implementierung liegt auf Einfachheit und
     * Performance.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return ort.hashCode() + strasse.hashCode() + hausnummer.hashCode();
    }

    /**
     * Hierueber wird die Adresse, beginnend mit dem Ort, ausgegeben.
     *
     * @return z.B. "12345 Entenhausen, Gansstr. 23"
     */
    @Override
    public String toString() {
        return this.getOrt() + ", " + this.getStrasse() + " " + this.getHausnummer();
    }

}
