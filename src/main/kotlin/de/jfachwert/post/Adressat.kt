/*
 * Copyright (c) 2018 by Oliver Boehm
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
 * (c)reated 18.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.NullValidator;

import java.util.WeakHashMap;

/**
 * Ein Adressat (oder auch Postempfaenger) ist diejenige Person, die in der
 * Adresse benannt ist und für die damit eine Postsendung bestimmt ist. 
 * Hierbei kann es sich um eine natuerliche oder um eine juristische Person 
 * handeln.
 *
 * @author oboehm
 * @since 0.5 (18.01.2018)
 */
public class Adressat extends Name {

    private static final WeakHashMap<String, Adressat> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen. */
    public static final Adressat NULL = new Adressat("", new NullValidator<>());

    /**
     * Erzeugt eine Adressat mit dem angegebenen Namen. Dabei kann es sich um
     * eine natuerliche Person (z.B. "Mustermann, Max") oder eine juristische
     * Person (z.B. "Ich AG") handeln.
     * <p>
     * Das Format des Adressat ist so, wie er auf dem Brief angegeben wird:
     * "Nachname, Vorname" bei Personen bzw. Name bei juristischen Personen.
     * </p>
     *
     * @param name z.B. "Mustermann, Max"
     */
    public Adressat(String name) {
        this(name, LengthValidator.NOT_EMPTY_VALIDATOR);
    }

    /**
     * Erzeugt eine Adressat mit dem angegebenen Namen. Dabei kann es sich um
     * eine natuerliche Person (z.B. "Mustermann, Max") oder eine juristische
     * Person (z.B. "Ich AG") handeln.
     * <p>
     * Das Format des Adressat ist so, wie er auf dem Brief angegeben wird:
     * "Nachname, Vorname" bei Personen bzw. Name bei juristischen Personen.
     * </p>
     *
     * @param name      z.B. "Mustermann, Max"
     * @param validator Validator fuer die Ueberpruefung des Namens
     */
    public Adressat(String name, SimpleValidator<String> validator) {
        super(name, validator);
    }

    /**
     * Liefert einen Adressat mit dem angegebenen Namen.
     * 
     * @param name z.B. "Mustermann, Max"
     * @return Addressat mit dem angegebenen Namen
     */
    public static Adressat of(String name) {
        return WEAK_CACHE.computeIfAbsent(name, Adressat::new);
    }

    /**
     * Der Name ist der Teil vor dem Komma (bei Personen). Bei Firmen ist
     * es der komplette Name.
     * 
     * @return z.B. "Mustermann"
     */
    public String getName() {
        return getNachname();
    }

    /**
     * Bei natuerlichen Personen mit Vornamen kann hierueber der Vorname
     * ermittelt werden.
     * 
     * @return z.B. "Max"
     */
    @Override
    public String getVorname() {
        if (hasVorname()) {
            return super.getVorname();
        } else {
            throw new IllegalStateException("keine nat\u00fcrliche Person: " + this.getCode());
        }
    }

}
