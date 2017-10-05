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
 * (c)reated 28.08.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.pruefung.InvalidValueException;
import org.apache.commons.lang3.Range;

/**
 * Die Liste fuer die Anrede orientiert sich am Handbuch des Gesamtverbands der
 * Deutschen Versicherungswirtschaft (GDV). Deswegen taucht auch "Fraeulein" in
 * der Auswahl auf, obwohl es schon lange nicht mehr zeitgemaess ist.
 */
public enum Anrede {

    /** Unbekannte Anrede. */
    OHNE_ANREDE(""),

    /** Maennliche Anrede. */
    HERR("Herr"),

    /** Weibliche Anrede. */
    FRAU("Frau"),

    /** Firma. */
    FIRMA("Firma"),

    /** Herr und Frau. */
    HERR_UND_FRAU("Herr und Frau"),

    /** Fraeulein (aus historischen Gruenden). */
    FRAEULEIN("Fr\u00e4ulein"),

    /** Vereinigung. */
    VEREINIGUNG("Vereinigung");

    private final String text;

    Anrede(String text) {
        this.text = text;
    }

    /**
     * Als Ergebnis werden die einzelnen Elemente in normaler Schreibweise
     * ausgegeben und nicht in kompletter Grossschreibung.
     *
     * @return z.B. "Herr" oder "Frau"
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Liefert das n-te Element als Anrede zurueck. Die Reihenfolge entspricht
     * dabei der Reihenfolge, wie sie im Handbuch des GDVs dokumnetiert sind.
     *
     * @param n von 0 bis 6
     * @return Anrede
     */
    public static Anrede of(int n) {
        int max = Anrede.values().length - 1;
        if ((n < 0) || (n > max)) {
            throw new InvalidValueException(n, "n", Range.between(0, max));
        }
        return Anrede.values()[n];
    }

}
