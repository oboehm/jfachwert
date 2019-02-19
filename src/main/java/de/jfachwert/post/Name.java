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

import java.util.WeakHashMap;

/**
 * Die Klasse Name steht fuer einen normalen Namen mit Vorname und Nachnamen,
 * kann aber auch fuer Firmennamen genutzt werden.
 *
 * @author oboehm
 * @since 2.1 (19.02.2019)
 */
public class Name extends Text {

    private static final SimpleValidator<String> VALIDATOR = new LengthValidator<>(1);
    private static final WeakHashMap<String, Name> WEAK_CACHE = new WeakHashMap<>();

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

}
