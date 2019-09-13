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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private static final SimpleValidator<String> VALIDATOR = LengthValidator.NOT_EMPTY_VALIDATOR;

    /** Null-Wert fuer Initialisierung. */
    public static final Name NULL = new Name("", new NullValidator<>());

    /**
     * Erzeugt einen Namen. Erwartet wird ein einzelner Name, oder
     * "Nachname, Vorname".
     *
     * @param name, z.B. "Duck, Donald"
     */
    public Name(String name) {
        this(name, VALIDATOR);
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
        String nachname = getCode();
        if (nachname.contains(",")) {
            nachname = StringUtils.substringBefore(getCode(), ",").trim();
        } else {
            List<String> namensliste = getNamensListe();
            nachname = namensliste.get(namensliste.size() - 1);
        }
        return nachname;
    }

    /**
     * Liefert den oder die Vornamen als ein String.
     *
     * @return z.B. "Donald"
     */
    public String getVorname() {
        String vorname = getCode();
        if (vorname.contains(",")) {
            vorname = StringUtils.substringAfter(getCode(), ",").trim();
        } else {
            vorname = getNamensListe().get(0);
        }
        return vorname;
    }

    /**
     * Liste die einzelnen Vornamen und Namen als Liste auf.
     *
     * @return Liste mit Namen (mit mind. 1 Namen)
     */
    public List<String> getNamensListe() {
        String namen = getCode().replaceAll("\\.", ". ");
        return Arrays.asList(namen.split("[\\s,]"));
    }

    /**
     * Falls mehr als ein Vornamen exisitiert, kann dies hierueber als Liste
     * von Vornamen angefragt werden.
     *
     * @return Liste mit Vornamen (kann auch leer sein)
     */
    public List<String> getVornamenListe() {
        if (hasVorname()) {
            return Arrays.asList(getVorname().split("\\s"));
        } else {
            return new ArrayList<>();
        }
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
        return Text.replaceUmlaute(this.getNachname()).toLowerCase().hashCode();
    }

    /**
     * Hier werden Namen verglichen. Aktuell werden sie semantisch verglichen,
     * aber darauf sollte man sich nicht verlassen. Man sollte entweder
     * {@link #equalsSemantic(Name)} fuer den semantischen Vergleich und
     * {@link #equalsExact(Name)} fuer den exakten Vergleich verwenden, da
     * es sein kann, dass diese Implementierung kuenftig auf den exakten
     * Vergleich aufbaut.
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
        return equalsSemantic(other);
    }

    /**
     * Hier werden Namen logisch (semantisch) verglichen. So werden Namen auch
     * als gleich angesehen, wenn sie mit oder ohne Umlaute geschrieben werden.
     *
     * @param other der zu vergleichende Name
     * @return true bei Gleichheit
     * @see Object#equals(Object)
     * @since 3.0
     */
    public boolean equalsSemantic(Name other) {
        return isEquals(normalize(this), normalize(other));
    }

    private static Name normalize(Name name) {
        return Name.of(name.replaceUmlaute().toString().replace("-", " ").trim());
    }

    private static boolean isEquals(Name a, Name b) {
        return a.getNachname().equalsIgnoreCase(b.getNachname()) &&
                (shortenVorname(a).equalsIgnoreCase(shortenVorname(b)) ||
                        equalsVornamen(a.getVornamenListe(), b.getVornamenListe()));
    }

    private static String shortenVorname(Name x) {
        return StringUtils.deleteWhitespace(x.getVorname().replace("-", ""));
    }

    private static boolean equalsVornamen(List<String> a, List<String> b) {
        int n = Math.min(a.size(), b.size());
        for (int i = 0; i < n; i++) {
            if (!a.get(i).equalsIgnoreCase(b.get(i))) {
                return false;
            }
        }
        return true;
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
