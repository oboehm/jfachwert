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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.util.ToFachwertSerializer;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Ein Postfach besteht aus einer Nummer ohne fuehrende Nullen und einer
 * Postleitzahl mit Ortsangabe. Die Nummer selbst ist optional, wenn die
 * durch die Postleitzahl bereits das Postfach abgebildet wird.
 * <p>
 * Im Englischen wird das Postfach oft als POB (Post Office Box) bezeichnet.
 * </p>
 *
 * @author oboehm
 * @since 0.2 (19.06.2017)
 */
@JsonSerialize(using = ToFachwertSerializer.class)
public class Postfach implements Fachwert {

    private final BigInteger nummer;
    private final Ort ort;

    /**
     * Zerlegt den uebergebenen String in seine Einzelteile und validiert sie.
     * Folgende Heuristiken werden fuer die Zerlegung herangezogen:
     * <ul>
     *     <li>Format ist "Postfach, Ort" oder nur "Ort" (mit PLZ)</li>
     *     <li>Postfach ist vom Ort durch Komma oder Zeilenvorschub getrennt</li>
     * </ul>
     *
     * @param postfach z.B. "Postfach 98765, 12345 Entenhausen"
     */
    public Postfach(String postfach) {
        this(split(postfach));
    }
    
    private Postfach(String[] postfach) {
        this(postfach[0], postfach[1]);
    }

    /**
     * Erzeugt ein Postfach ohne Postfachnummer. D.h. die PLZ des Ortes
     * adressiert bereits das Postfach.
     *
     * @param ort gueltiger Ort mit PLZ
     */
    public Postfach(Ort ort) {
        this.ort = ort;
        this.nummer = null;
        validate(ort);
    }

    /**
     * Erzeugt ein Postfach mit Postfachnummer. Wenn die uebergebene Nummer
     * leer ist, wird ein Postfach ohne Postfachnummer erzeugt.
     * 
     * @param nummer z.B. "12 34 56"
     * @param ort Ort mit Postleitzahl
     */
    public Postfach(String nummer, String ort) {
        this(toNumber(nummer), new Ort(ort));
    }

    /**
     * Erzeugt ein neues Postfach.
     *
     * @param map mit den einzelnen Elementen fuer "plz", "ortsname" und
     *            "nummer".
     */
    @JsonCreator
    public Postfach(Map<String, String> map) {
        this(toNumber(map.get("nummer")), new Ort(PLZ.of(map.get("plz")), map.get("ortsname")));
    }

    /**
     * Erzeugt ein Postfach.
     *
     * @param nummer positive Zahl ohne fuehrende Null
     * @param ort gueltiger Ort mit PLZ
     */
    public Postfach(long nummer, Ort ort) {
        this(BigInteger.valueOf(nummer), ort);
    }

    /**
     * Erzeugt ein Postfach.
     *
     * @param nummer positive Zahl ohne fuehrende Null
     * @param ort gueltiger Ort mit PLZ
     */
    public Postfach(BigInteger nummer, Ort ort) {
        this.nummer = nummer;
        this.ort = ort;
        validate(nummer, ort);
    }

    /**
     * Erzeugt ein Postfach.
     * 
     * @param nummer positive Zahl oder leer
     * @param ort Ort
     */
    public Postfach(Optional<BigInteger> nummer, Ort ort) {
        this.nummer = nummer.orElse(null);
        this.ort = ort;
        if (this.nummer == null) {
            validate(ort);
        } else {
            validate(nummer.get(), ort);
        }
    }

    /**
     * Zerlegt das uebergebene Postfach in seine Einzelteile und validiert sie.
     * Folgende Heuristiken werden fuer die Zerlegung herangezogen:
     * <ul>
     *     <li>Format ist "Postfach, Ort" oder nur "Ort" (mit PLZ)</li>
     *     <li>Postfach ist vom Ort durch Komma oder Zeilenvorschub getrennt</li>
     * </ul>
     *
     * @param postfach z.B. "Postfach 98765, 12345 Entenhausen"
     */
    public static void validate(String postfach) {
        String[] lines = split(postfach);
        toNumber(lines[0]);
        Ort ort = new Ort(lines[1]);
        if (!ort.getPLZ().isPresent()) {
            throw new InvalidValueException(postfach, "postal_code");
        }
    }
    
    private static String[] split(String postfach) {
        String[] lines = StringUtils.trimToEmpty(postfach).split("[,\\n$]");
        String[] splitted = { "", lines[0]};
        if (lines.length == 2) {
            splitted = lines;
        } else if (lines.length > 2) {
            throw new InvalidValueException(postfach, "post_office_box");
        }
        return splitted;
    }
    
    private static Optional<BigInteger> toNumber(String number) {
        if (StringUtils.isBlank(number)) {
            return Optional.empty();
        }
        String unformatted = StringUtils.replaceAll(number, "Postfach|\\s+", "");
        try {
            return Optional.of(new BigInteger(unformatted));
        } catch (NumberFormatException nfe) {
            throw new InvalidValueException(number, "number", nfe);
        }
    }

    /**
     * Validiert das uebergebene Postfach auf moegliche Fehler.
     *
     * @param nummer    Postfach-Nummer (muss positiv sein)
     * @param ort       Ort mit PLZ
     */
    public static void validate(BigInteger nummer, Ort ort) {
        if (nummer.compareTo(BigInteger.ONE) < 0) {
            throw new InvalidValueException(nummer, "number");
        }
        validate(ort);
    }

    /**
     * Ueberprueft, ob der uebergebene Ort tatsaechlich ein PLZ enthaelt.
     *
     * @param ort Ort mit PLZ
     */
    public static void validate(Ort ort) {
        if (!ort.getPLZ().isPresent()) {
            throw new InvalidValueException(ort, "postal_code");
        }
    }

    /**
     * Liefert die Postfach-Nummer als normale Zahl. Da die Nummer optional
     * sein kann, wird sie als {@link Optional} zurueckgeliefert.
     *
     * @return z.B. 815
     */
    public Optional<BigInteger> getNummer() {
        if (nummer == null) {
            return Optional.empty();
        } else {
            return Optional.of(nummer);
        }
    }

    /**
     * Liefert die Postfach-Nummer als formattierte Zahl. Dies macht natuerlich
     * nur Sinn, wenn diese Nummer gesetzt ist. Daher wird eine
     * {@link IllegalStateException} geworfen, wenn dies nicht der Fall ist.
     *
     * @return z.B. "8 15"
     */
    @SuppressWarnings("squid:S3655")
    public String getNummerFormatted() {
        if (!this.getNummer().isPresent()) {
            throw new IllegalStateException("no number present");
        }
        BigInteger hundert = BigInteger.valueOf(100);
        StringBuilder formatted = new StringBuilder();
        for (BigInteger i = this.getNummer().get(); i.compareTo(BigInteger.ONE) > 0; i = i.divide(hundert)) {
            formatted.insert(0, " " + i.remainder(hundert));
        }
        return formatted.toString().trim();
    }

    /**
     * Liefert die Postleitzahl. Ohne gueltige Postleitzahl kann kein Postfach
     * angelegt werden, weswegen hier immer eine PLZ zurueckgegeben wird.
     *
     * @return z.B. 09876
     * @deprecated bitte {@link #getPLZ()} verwenden
     */
    @Deprecated
    @JsonIgnore
    public PLZ getPlz() {
        return this.ort.getPLZ().get();
    }

    /**
     * Liefert die Postleitzahl. Ohne gueltige Postleitzahl kann kein Postfach
     * angelegt werden, weswegen hier immer eine PLZ zurueckgegeben wird.
     *
     * @return z.B. 09876
     */
    public PLZ getPLZ() {
        return this.ort.getPLZ().get();
    }

    /**
     * Liefert den Ort.
     *
     * @return Ort
     */
    public Ort getOrt() {
        return this.ort;
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
     * Zwei Postfaecher sind gleich, wenn sie die gleiche Attribute haben.
     *
     * @param obj das andere Postfach
     * @return true bei Gleichheit
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Postfach)) {
            return false;
        }
        Postfach other = (Postfach) obj;
        return this.nummer.equals(other.nummer) && this.ort.equals(other.ort);
    }

    /**
     * Da die PLZ meistens bereits ein Postfach adressiert, nehmen dies als
     * Basis fuer die Hashcode-Implementierung.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return this.getOrt().hashCode();
    }

    /**
     * Hierueber wird das Postfach einzeilig ausgegeben.
     *
     * @return z.B. "Postfach 8 15, 09876 Nirwana"
     */
    @Override
    public String toString() {
        if (this.getNummer().isPresent()) {
            return "Postfach " + this.getNummerFormatted() + ", " + this.getOrt();
        } else {
            return this.getOrt().toString();
        }
    }
    
    /**
     * Liefert die einzelnen Attribute eines Postfaches als Map.
     *
     * @return Attribute als Map
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("plz", getPLZ());
        map.put("ortsname", getOrtsname());
        if (getNummer().isPresent()) {
            map.put("nummer", getNummer().get());
        }
        return map;
    }
    
}
