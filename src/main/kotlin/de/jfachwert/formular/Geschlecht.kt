/*
 * Copyright (c) 2017-2020 by Oliver Boehm
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
 * (c)reated 05.10.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.formular

import de.jfachwert.pruefung.AccessValidator.access

/**
 * Die Werte fuer das Geschlecht orientieren sich am Handbuch des
 * Gesamtverbands der Deutschen Versicherungswirtschaft (GDV). Auch
 * die Reihenfolge entspricht diesem Handbuch. Zusaetzlich wurde noch
 * [Geschlecht.DIVERS] und [Geschlecht.UNBEKANNT] als Wert mit
 * aufgenommen.
 *
 * @since 2.0
 */
enum class Geschlecht(private val text: String) {

    /** Juristische Person (meist Firmen).  */
    JURISTISCHE_PERSON("juristische Person"),

    /** Maennliches Geschlecht.  */
    MAENNLICH("m\u00e4nnlich"),

    /** Weibliches Geschlecht.  */
    WEIBLICH("weiblich"),

    /** Divers.  */
    DIVERS("divers"),

    /** Unbekanntes Geschlecht.  */
    UNBEKANNT("?");

    /**
     * Als Ergebnis werden die einzelnen Elemente in normaler Schreibweise
     * ausgegeben und nicht in kompletter Grossschreibung.
     *
     * @return z.B. "weiblich"
     */
    override fun toString(): String {
        return text
    }



    companion object {

        /**
         * Liefert das n-te Element als Geschlecht zurueck. Die Reihenfolge
         * entspricht dabei der Reihenfolge, wie sie im Handbuch des GDVs
         * dokumentiert sind.
         *
         * @param n von 0 bis 4
         * @return Geschlecht
         */
        @JvmStatic
        fun of(n: Int): Geschlecht {
            return access(values(), n)
        }

        /**
         * Liefert das Geschlecht, das dem uebergebenen Buchstaben entspricht.
         * Bei unbekanntem Buchstaben wird [Geschlecht.UNBEKANNT]
         * zurueckgegeben.
         *
         * @param key Buchstaben wie 'w', 'm' oder 'j' (juristische Person)
         * @return Geschlecht
         */
        @JvmStatic
        fun of(key: Char): Geschlecht {
            for (g in values()) {
                if (g.text[0] == Character.toLowerCase(key)) {
                    return g
                }
            }
            return UNBEKANNT
        }

    }

}