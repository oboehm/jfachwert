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

import de.jfachwert.SimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Auf Rechnunungen (und auch im geschaeftlichen Schriftverkehr) gibt es
 * oftmals eine Referenznummer, die durch diese Klasse repraesentiert wird.
 *
 * @author oboehm
 * @since 0.3 (10.07.2017)
 */
open class Referenznummer

/**
 * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
 * damit diese den [SimpleValidator] ueberschreiben koennen.
 * Man kann es auch verwenden, um einen eigenen [SimpleValidator]
 * einsetzen zu koennen.
 *
 * @param nummer   z.B. "000002835042"
 * @param pruefung Pruefverfahren
 */
@JvmOverloads constructor(nummer: String?, pruefung: SimpleValidator<String?>? = LengthValidator.NOT_EMPTY_VALIDATOR) : Text(nummer, pruefung) {

    companion object {

        private val WEAK_CACHE = WeakHashMap<String, Referenznummer>()

        /** Null-Konstante fuer Initialisierungen.  */
        val NULL = Referenznummer("", NullValidator())

        /**
         * Erzeugt eine Referenznummer.
         *
         * @param nummer z.B. "000002835042"
         * @return Referenznummer
         */
        @JvmStatic
        fun of(nummer: String): Referenznummer {
            return WEAK_CACHE.computeIfAbsent(nummer) { n: String? -> Referenznummer(n) }
        }
    }

}