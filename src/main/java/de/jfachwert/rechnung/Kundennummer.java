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
 * (c)reated 09.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.rechnung;

import de.jfachwert.*;
import de.jfachwert.pruefung.*;

/**
 * Eine Kundennummer ist meistens eine vielstellige Zahl oder Zeichenfolge,
 * die einen Kunden eindeutig identifiziert.
 *
 * @author oboehm
 * @since 0.3 (09.07.2017)
 */
public class Kundennummer extends AbstractFachwert<String> {

    /**
     * Erzeugt eine Kundennummer.
     *
     * @param nummer z.B. "100.059"
     */
    public Kundennummer(String nummer) {
        this(nummer, LengthValidator.NOT_EMPTY_VALIDATOR);
    }

    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das {@link PruefzifferVerfahren} ueberschreiben koennen.
     * Man kann es auch verwenden, um ein eigenes {@link PruefzifferVerfahren}
     * einsetzen zu koennen. Standardmaessig wird hier ansonsten nur ueberprueft,
     * ob die Kundennummer nicht leer ist.
     *
     * @param kundennummer die Kundennummer
     * @param pruefung     Pruefverfahren
     */
    public Kundennummer(String kundennummer, PruefzifferVerfahren<String> pruefung) {
        super(pruefung.validate(kundennummer));
    }

}
