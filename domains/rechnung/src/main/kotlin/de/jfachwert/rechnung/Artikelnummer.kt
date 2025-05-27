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
 * (c)reated 10.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.rechnung

import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Die Artikelnummer ist meistens eine Nummer, die den Artiekel beim
 * Hersteller eindeutig kennzeichnet.
 *
 * @author oboehm
 * @since 0.3 (10.07.2017)
 */
open class Artikelnummer
/**
 * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
 * damit diese den [KSimpleValidator] ueberschreiben koennen.
 * Man kann es auch verwenden, um einen eigenen [KSimpleValidator]
 * einsetzen zu koennen.
 *
 * @param nummer   z.B. "000002835042"
 * @param pruefung Pruefverfahren
 */
@JvmOverloads constructor(nummer: String, pruefung: KSimpleValidator<String> = LengthValidator.NOT_EMPTY_VALIDATOR) : Text(nummer, pruefung) {

    companion object {

        private val WEAK_CACHE = WeakHashMap<String, Artikelnummer>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = Artikelnummer("", NullValidator())

        /**
         * Erzeugt eine Artikelnummer.
         *
         * @param nummer z.B. "000002835042"
         * @return Artikelnummer
         */
        @JvmStatic
        fun of(nummer: String): Artikelnummer {
            val copy = String(nummer.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy) { n: String -> Artikelnummer(String(n.toCharArray())) }
        }
    }

}
