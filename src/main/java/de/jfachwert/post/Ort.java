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

import de.jfachwert.Fachwert;

import java.util.Objects;

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
public class Ort implements Fachwert {

    private final String name;
    private final PLZ plz;

    /**
     * Hierueber kann ein Ort (ohne PLZ) angelegt werden.
     *
     * @param name des Ortes
     */
    public Ort(String name) {
        this(null, name);
    }

    /**
     * Hierueber kann ein Ort mit PLZ angelegt werden.
     *
     * @param name des Ortes
     */
    public Ort(PLZ plz, String name) {
        this.plz = plz;
        this.name = name;
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
            return this.name;
        } else {
            return this.plz + " " + this.name;
        }
    }

}
