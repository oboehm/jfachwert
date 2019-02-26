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
 * (c)reated 10.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.rechnung;

import de.jfachwert.PruefzifferVerfahren;
import de.jfachwert.SimpleValidator;
import de.jfachwert.Text;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.NullValidator;

import java.util.WeakHashMap;

/**
 * Die Artikelnummer ist meistens eine Nummer, die den Artiekel beim
 * Hersteller eindeutig kennzeichnet.
 *
 * @author oboehm
 * @since 0.3 (10.07.2017)
 */
public class Artikelnummer extends Text {

    private static final WeakHashMap<String, Artikelnummer> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen. */
    public static final Artikelnummer NULL = new Artikelnummer("", new NullValidator<>());

    /**
     * Erzeugt eine Artikelnummer.
     *
     * @param nummer z.B. "000002835042"
     */
    public Artikelnummer(String nummer) {
        this(nummer, LengthValidator.NOT_EMPTY_VALIDATOR);
    }

    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das {@link PruefzifferVerfahren} ueberschreiben koennen.
     * Man kann es auch verwenden, um ein eigenes {@link PruefzifferVerfahren}
     * einsetzen zu koennen.
     *
     * @param nummer   z.B. "000002835042"
     * @param pruefung Pruefverfahren
     */
    public Artikelnummer(String nummer, SimpleValidator<String> pruefung) {
        super(nummer, pruefung);
    }

    /**
     * Erzeugt eine Artikelnummer.
     *
     * @param nummer z.B. "000002835042"
     * @return Artikelnummer
     */
    public static Artikelnummer of(String nummer) {
        return WEAK_CACHE.computeIfAbsent(nummer, Artikelnummer::new);
    }

}
