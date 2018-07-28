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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.jfachwert.Fachwert;
import de.jfachwert.util.ToFachwertSerializer;
import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ValidationException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bei einer Adresse kann es sich um eine Wohnungsadresse oder Gebaeudeadresse
 * handeln. Sie besteht aus Ort, Strasse und Hausnummer. Sie unterscheidet sich
 * insofern von einer Anschrift, da der Name nicht Bestandteil der Adresse ist.
 *
 * @author oboehm
 * @since 0.2 (02.05.2017)
 */
@JsonSerialize(using = ToFachwertSerializer.class)
public class Adresse implements Fachwert {

    private static final Logger LOG = Logger.getLogger(Adresse.class.getName());

    private final Ort ort;
    private final String strasse;
    private final String hausnummer;

    /**
     * Zerlegt die uebergebene Adresse in ihre Einzelteile und baut daraus die
     * Adresse zusammen. Folgende Heuristiken werden fuer die Zerlegung 
     * herangezogen:
     * <ul>
     *     <li>Reihenfolge kann Ort, Strasse oder Strasse, Ort sein</li>
     *     <li>Ort / Strasse werden durch Komma oder Zeilenvorschub getrennt</li>
     *     <li>vor dem Ort steht die PLZ</li>
     * </ul>
     * 
     * @param adresse z.B. "12345 Entenhausen, Gansstr. 23"
     */
    public Adresse(String adresse) {
        this(split(adresse));
    }
    
    private Adresse(String[] adresse) {
        this(new Ort(adresse[0]), adresse[1], adresse[2]);
    }
    
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
     * Erzeugt eine neue Adresse.
     *
     * @param map mit den einzelnen Elementen fuer "plz", "ortsname",
     *            "strasse" und "hausnummer".
     */
    @JsonCreator
    public Adresse(Map<String, String> map) {
        this(new Ort(PLZ.of(map.get("plz")), map.get("ortsname")), map.get("strasse"), map.get("hausnummer"));
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
     * Zerlegt die uebergebene Adresse in ihre Einzelteile und validiert sie.
     * Folgende Heuristiken werden fuer die Zerlegung herangezogen:
     * <ul>
     *     <li>Reihenfolge kann Ort, Strasse oder Strasse, Ort sein</li>
     *     <li>Ort / Strasse werden durch Komma oder Zeilenvorschub getrennt</li>
     *     <li>vor dem Ort steht die PLZ</li>
     * </ul>
     * 
     * @param adresse z.B. "12345 Entenhausen, Gansstr. 23"
     */
    public static void validate(String adresse) {
        String[] splitted = split(adresse);
        Ort ort = new Ort(splitted[0]);
        validate(ort, splitted[1], splitted[2]);
    }
    
    private static String[] split(String adresse) {
        String[] lines = StringUtils.trimToEmpty(adresse).split("[,\\n$]");
        if (lines.length != 2) {
            throw new InvalidValueException(adresse, "address");
        }
        List<String> splitted = new ArrayList<>();
        if (hasPLZ(lines[0])) {
            splitted.add(lines[0].trim());
            splitted.addAll(toStrasseHausnummer(lines[1]));
        } else {
            splitted.add(lines[1].trim());
            splitted.addAll(toStrasseHausnummer(lines[0]));
        }
        return splitted.toArray(new String[3]);
    }

    private static boolean hasPLZ(String line) {
        try {
            Ort ort = new Ort(line);
            return ort.getPLZ().isPresent();
        } catch (ValidationException ex) {
            LOG.log(Level.FINE, "no PLZ inside '" + line + "' found:", ex);
            return false;
        }
    }

    private static List<String> toStrasseHausnummer(String line) {
        String[] splitted = line.trim().split("\\s+");
        if (splitted.length != 2) {
            splitted = line.split("\\s+[0-9]", 2);
            splitted[1] = line.substring(splitted[0].length()).trim();
        }
        if (splitted.length != 2) {
            throw new InvalidValueException(line, "street_or_house_number");
        }
        return Arrays.asList(splitted);
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
     * Liefert den Ortsnamen.
     * 
     * @return Ortsname
     */
    public String getOrtsname() {
        return ort.getName();
    }

    /**
     * Eine PLZ <em>muss</em> fuer eine Adresse vorhanden sein, sonst laesst
     * sich keine Aresse Anlagen. Diese wird hierueber zurueckgegeben.
     *
     * @return z.B. "80739" fuer Gerlingen
     */
    @SuppressWarnings("squid:S3655")
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

    /**
     * Liefert die einzelnen Attribute einer Adresse als Map.
     *
     * @return Attribute als Map
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("plz", getPLZ());
        map.put("ortsname", getOrtsname());
        map.put("strasse", getStrasse());
        map.put("hausnummer", getHausnummer());
        return map;
    }

}
