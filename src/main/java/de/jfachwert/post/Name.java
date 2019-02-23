/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 19.02.2019 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.SimpleValidator;
import de.jfachwert.Text;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.NullValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.WeakHashMap;

/**
 * Die Klasse Name steht fuer einen normalen Namen mit Vorname und Nachnamen,
 * kann aber auch fuer Firmennamen genutzt werden.
 *
 * @author oboehm
 * @since 2.1 (19.02.2019)
 */
public class Name extends Text {

    private static final WeakHashMap<String, Name> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Wert fuer Initialisierung. */
    public static final Name NULL = new Name("", new NullValidator<>());

    /**
     * Erzeugt einen Namen. Erwartet wird ein einzelner Name, oder
     * "Nachname, Vorname".
     *
     * @param name, z.B. "Duck, Donald"
     */
    public Name(String name) {
        this(name, LengthValidator.NOT_EMPTY_VALIDATOR);
    }

    /**
     * Erzeugt einen Namen. Erwartet wird ein einzelner Name, oder
     * "Nachname, Vorname".
     *
     * @param name,     z.B. "Duck, Donald"
     * @param validator Validator fuer die Ueberpruefung
     */
    public Name(String name, SimpleValidator<String> validator) {
        super(name, validator);
    }

    /**
     * Erzeugt einen neuen Namen, falls er noch nicht existiert. Falls er
     * bereits existiert, wird dieser zurueckgegeben, um Duplikate zu
     * vermeiden.
     *
     * @param name, z.B. "Duck, Donald"
     * @return Name
     */
    public static Name of(String name) {
        return WEAK_CACHE.computeIfAbsent(name, Name::new);
    }

    /**
     * Liefert den Nachnamen.
     *
     * @return z.B. "Duck"
     */
    public String getNachname() {
        return StringUtils.substringBefore(getCode(), ",");
    }

    /**
     * Liefert den oder die Nachnamen.
     *
     * @return z.B. "Donald"
     */
    public String getVorname() {
        return StringUtils.substringAfter(getCode(), ",").trim();
    }

    /**
     * Liefert 'true' zurueck, falls ein Vorname im abgespeicherten Namen
     * enthalten ist.
     *
     * @return true, false
     */
    public boolean hasVorname() {
        return getCode().contains(",");
    }

    @Override
    public int hashCode() {
        return normalize(this).toLowerCase().hashCode();
    }

    /**
     * Hier werden Namen logisch verglichen. So werden Namen auch als gleich
     * angesehen, wenn sie mit oder ohne Umlaute geschrieben werden.
     *
     * @param obj zu vergleichender Name
     * @return true bei Gleichheit
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Name) || (!this.getClass().isAssignableFrom(obj.getClass()))) {
            return false;
        }
        Name other = (Name) obj;
        return normalize(this).equalsIgnoreCase(normalize(other));
    }

    private static String normalize(Name name) {
        return StringUtils.deleteWhitespace(name.replaceUmlaute().toString().replace("-", ""));
    }

    /**
     * Im Gegensatz zur {@link #equals(Object)}-Methode muss hier der andere
     * Name exakt einstimmen, also auch in Gross- und Kleinschreibung.
     *
     * @param other der andere Name
     * @return true oder false
     * @since 2.1
     */
    public boolean equalsExact(Name other) {
        return super.equals(other);
    }

}
