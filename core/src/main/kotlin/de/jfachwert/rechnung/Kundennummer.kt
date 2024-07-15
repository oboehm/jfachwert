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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 09.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.rechnung

import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Eine Kundennummer ist meistens eine vielstellige Zahl oder Zeichenfolge,
 * die einen Kunden eindeutig identifiziert.
 *
 * @author oboehm
 * @since 0.3 (09.07.2017)
 */
open class Kundennummer
/**
 * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
 * damit diese den [KSimpleValidator] ueberschreiben koennen.
 * Man kann es auch verwenden, um einen eigenen [KSimpleValidator]
 * einsetzen zu koennen. Standardmaessig wird hier ansonsten nur ueberprueft,
 * ob die Kundennummer nicht leer ist.
 *
 * @param kundennummer die Kundennummer, z.B. "100.059"
 * @param pruefung     Pruefverfahren
 */
@JvmOverloads constructor(kundennummer: String, pruefung: KSimpleValidator<String> = LengthValidator.NOT_EMPTY_VALIDATOR) : Text(kundennummer, pruefung) {

    companion object {

        private val WEAK_CACHE = WeakHashMap<String, Kundennummer>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = Kundennummer("", NullValidator())

        /**
         * Erzeugt eine Kundennummer.
         *
         * @param nummer z.B. "100.059"
         * @return Kundennummer
         */
        @JvmStatic
        fun of(nummer: String): Kundennummer {
            return WEAK_CACHE.computeIfAbsent(nummer) { n: String -> Kundennummer(n) }
        }
    }

}