/*
 * Copyright (c) 2020 by Oliver Boehm
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
 * (c)reated 25.05.2020 by oboehm
 */

package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.SimpleValidator
import de.jfachwert.math.Promille
import de.jfachwert.pruefung.LengthValidator
import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.util.*

/**
 * Die Klasse PZN.
 *
 * @author oboehm
 * @since 4.0 (25.05.2020)
 */
open class PZN
/**
 * Erzeugt ein neues PZN-Objekt.
 *
 * @param code achtstellige Zahl
 * @param validator Validator zur Pruefung der Zahl
 */
@JvmOverloads constructor(code: Int, validator: SimpleValidator<Int> = VALIDATOR) : AbstractFachwert<Int, PZN>(code, validator) {

    /**
     * Erzeugt ein neues PZN-Objekt.
     *
     * @param code achtstellige Zahl
     */
    constructor(code: String) : this(toInt(code)) {}

    /**
     * Die PZN ist 8-stellig und wird auch achtstellig ausgegeben.
     *
     * @return 8-stellige Zeichenkette mit PZN-Prefix
     */
    override fun toString(): String {
        return String.format("PZN-%08d", code)
    }



    companion object {

        private val VALIDATOR = LengthValidator<Int>(2, 8)
        private val WEAK_CACHE = WeakHashMap<Int, PZN>()

        /**
         * Liefert eine PZN zurueck.
         *
         * @param code 8-stellige Nummer
         * @return die PZN
         */
        @JvmStatic
        fun of(code: Int): PZN {
            return WEAK_CACHE.computeIfAbsent(code) { n: Int -> PZN(n) }
        }

        /**
         * Liefert eine PZN zurueck.
         *
         * @param code 8-stellige Nummer
         * @return die PZN
         */
        @JvmStatic
        fun of(code: String): PZN {
            return of(toInt(code))
        }

        private fun toInt(s: String): Int {
            return s.replace("PZN-", "", true).toInt()
        }

    }

}
