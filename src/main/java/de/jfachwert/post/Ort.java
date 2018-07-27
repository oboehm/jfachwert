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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 13.04.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.post;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.LengthValidator;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ValidationException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ein Ort (oder auch Ortschaft) ist eine Stadt oder Gemeinde. Ein Ort hat
 * i.d.R. eine Postleitzahl (PLZ). Diese ist aber in dieser Klasse optional,
 * sodass man einen Ort auch ohne eine PLZ einsetzen kann.
 * <p>
 * Anmerkung: Die PLZ ist nicht als Optional realisert, da in Java Optionals
 * leider nicht serialiserbar sind :-(
 * </p>
 *
 * @author oboehm
 * @since 0.2.0 (13.04.2017)
 */
@JsonSerialize(using = ToStringSerializer.class)
public class Ort implements Fachwert {

    private static final Logger LOG = Logger.getLogger(Ort.class.getName());

    private final String name;
    private final PLZ plz;

    /**
     * Hierueber kann ein Ort (ohne PLZ) angelegt werden.
     *
     * @param name des Ortes
     */
    public Ort(String name) {
        this(split(name));
    }

    private Ort(String[] values) {
        this(values[0].isEmpty() ? null : new PLZ(values[0]), values[1]);
    }
    
    /**
     * Hierueber kann ein Ort mit PLZ angelegt werden.
     *
     * @param plz Postleitzahl des Ortes
     * @param name Name des Ortes
     */
    public Ort(PLZ plz, String name) {
        this.plz = plz;
        this.name = validate(name);
    }

    /**
     * Ein Orstname muss mindestens aus einem Zeichen bestehen. Allerdings
     * koennte der ueberbebene Name auch die PLZ noch beinhalten. Dies wird
     * bei der Validierung beruecksichtigt.
     *
     * @param name der Ortsname (mit oder ohne PLZ)
     * @return der validierte Ortsname zur Weiterverabeitung
     */
    public static String validate(String name) {
        String[] splitted = split(name);
        String ortsname = splitted[1];
        LengthValidator.validate(ortsname, 1, Integer.MAX_VALUE);
        return name;
    }

    private static String[] split(String name) {
        String input = StringUtils.trimToEmpty(name);
        String[] splitted = new String[]{"", input};
        if (input.contains(" ")) {
            try {
                String plz = PLZ.validate(StringUtils.substringBefore(input, " "));
                splitted[0] = plz;
                splitted[1] = StringUtils.substringAfter(input, " ").trim();
            } catch (ValidationException ex) {
                LOG.log(Level.FINE, "no PLZ inside '" + name + "' found:", ex);
            }
        }
        return splitted;
    }

    /**
     * Liefert den Ortsnamen zurueck.
     *
     * @return den Ortsnamen
     */
    public String getName() {
        return this.name;
    }

    /**
     * Da die Postleitzahl optional ist, wird sie auch als {@link Optional}
     * zurueckgegeben.
     *
     * @return die PLZ
     */
    public Optional<PLZ> getPLZ() {
        if (this.plz == null) {
            return Optional.empty();
        } else {
            return Optional.of(this.plz);
        }
    }

    /**
     * Beim Vergleich wird nicht zwischen Gross- und Kleinschreibung
     * unterschieden.
     *
     * @param obj der andere Ort
     * @return true, falls es der gleiche Ort ist
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ort)) {
            return false;
        }
        Ort other = (Ort) obj;
        return Objects.equals(this.plz, other.plz) && this.name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    /**
     * Liefert den Orstnamen als Ergebnis.
     *
     * @return Ortsname
     */
    @Override
    public String toString() {
        if (this.plz == null) {
            return this.getName();
        } else {
            return this.plz + " " + this.getName();
        }
    }

}
