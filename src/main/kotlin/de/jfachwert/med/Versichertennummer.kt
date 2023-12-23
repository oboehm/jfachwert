/*
 * Copyright (c) 2023 by Oli B.
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
 * (c)reated 22.12.23 by oboehm
 */
package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.PruefzifferVerfahren
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Seit der Einfuehrung der elektronischen Gesundheitskarte im Jahr 2012 werden
 * die ersten zehn Stellen der Krankenversichertennummer ebenso wie die
 * Rentenversicherungsnummer einmalig vergeben und bleiben lebenslang gleich
 * (aus https://de.wikipedia.org/wiki/Krankenversichertennummer).
 *
 * Die Versichertennummer ist dabei die 10-stellige Nummer am Anfang der
 * Krankenverichertennummer.
 *
 * @author oboehm
 * @since 5.1 (22.12.23)
 */
open class Versichertennummer
/**
 * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
 * damit diese das [PruefzifferVerfahren] ueberschreiben koennen.
 * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
 * indem man das [de.jfachwert.pruefung.NoopVerfahren] verwendet.
 *
 * @param iban        die IBAN
 * @param pzVerfahren das verwendete PruefzifferVerfahren (optional)
 */
@JvmOverloads constructor(code: String, pzVerfahren: KSimpleValidator<String> = Versichertennummer.VALIDATOR) : AbstractFachwert<String, Versichertennummer>(code, pzVerfahren) {

    companion object {
        private val WEAK_CACHE = WeakHashMap<String, Versichertennummer>()
        private val VALIDATOR: KSimpleValidator<String> = NullValidator()
        /** Null-Konstante.  */
        @JvmField
        val NULL = Versichertennummer("", NullValidator())

        /**
         * Liefert eine IBAN.
         *
         * @param code gueltige IBAN-Nummer
         * @return IBAN
         */
        @JvmStatic
        fun of(code: String): Versichertennummer {
            return WEAK_CACHE.computeIfAbsent(code) { c: String -> Versichertennummer(c) }
        }
    }

}