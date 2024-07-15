/*
 * Copyright (c) 2024 by Oli B.
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
 * (c)reated 09.02.24 by oboehm
 */
package de.jfachwert.med

import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.NoopVerfahren
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Die Zahnarztnummer (ZANR) entspricht im wesentlichen der LANR.
 * Sie ist seit 1.1.2023 verpflichtend (s.a.
 * https://www.kzv-berlin.de/fuer-praxen/abrechnung/zahnarztnummer).
 *
 * @author oboehm
 * @since 5.3 (09.02.24)
 */
open class ZANR
    /**
     * Erzeugt ein neues LANR-Objekt.
     *
     * @param code      neunstellige Zahl
     * @param validator Validator zur Pruefung der Zahl
     */
    @JvmOverloads constructor(code: Int, validator: KSimpleValidator<Int> = VALIDATOR) : LANR(code, validator) {

    override fun isZahnarzt(): Boolean {
        return true
    }

    companion object {

        private val WEAK_CACHE = WeakHashMap<Int, ZANR>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = ZANR(0, NullValidator())

        /**
         * Liefert eine ZANR zurueck.
         *
         * @param code 9-stellige Nummer
         * @return die ZANR
         */
        @JvmStatic
        fun of(code: Int): ZANR {
            return WEAK_CACHE.computeIfAbsent(code) { n: Int -> ZANR(n) }
        }

        /**
         * Liefert eine ZANR zurueck.
         *
         * @param code 9-stellige Nummer
         * @return die ZANR
         */
        @JvmStatic
        fun of(code: String): ZANR {
            return of(code.toInt())
        }

        /**
         * Wandelt eine LANR in eine ZANR.
         *
         * @param lanr LANR
         * @return die ZANR
         */
        @JvmStatic
        fun of(lanr: LANR): ZANR {
            return ZANR(lanr.code, NoopVerfahren())
        }

    }

}