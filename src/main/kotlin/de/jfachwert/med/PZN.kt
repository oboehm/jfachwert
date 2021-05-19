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
import de.jfachwert.PruefzifferVerfahren
import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.LengthValidator
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
@JvmOverloads constructor(code: Int, validator: KSimpleValidator<Int> = VALIDATOR) : AbstractFachwert<Int, PZN>(code, validator) {

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

        private val VALIDATOR = Validator()
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



    /**
     * Die Pruefziffer der PZN wird nach dem Modulo 11 berechnet. Dabei wird
     * jede Ziffer der PZN mit einem unterschiedlichen Faktor von eins bis neun
     * gewichtet. Ueber die Produkte wird die Summe gebildet und durch 11
     * dividiert. Der verbleibende ganzzahlige Rest bildet die Pruefziffer.
     * Bleibt als Rest die Zahl 10, dann wird diese Ziffernfolge nicht als PZN verwendet
     */
    class Validator : KSimpleValidator<Int> {

        /**
         * Wenn der uebergebene Wert gueltig ist, soll er unveraendert
         * zurueckgegeben werden, damit er anschliessend von der aufrufenden
         * Methode weiterverarbeitet werden kann. Ist der Wert nicht gueltig,
         * soll eine [javax.validation.ValidationException] geworfen
         * werden.
         *
         * @param value Wert, der validiert werden soll
         * @return Wert selber, wenn er gueltig ist
         */
        override fun validate(value: Int): Int {
            val n = VALIDATOR8.validate(value)
            MOD11.validate(Integer.toString(n))
            return n
        }

        companion object {
            private val MOD11: PruefzifferVerfahren<String> = Mod11Verfahren()
            private val VALIDATOR8 = LengthValidator<Int>(2, 8)
        }

    }


    /**
     * Die Pruefziffer der PZN wird nach dem Modulo 11 berechnet. Dabei wird
     * jede Ziffer der PZN mit einem unterschiedlichen Faktor von 1 bis 9
     * gewichtet. Ueber die Produkte wird die Summe gebildet und durch 11
     * dividiert. Der verbleibende ganzzahlige Rest bildet die Pruefziffer.
     * Bleibt als Rest die Zahl 10, dann wird diese Ziffernfolge nicht als PZN
     * verwendet
     */
    class Mod11Verfahren : PruefzifferVerfahren<String> {

        /**
         * Die Pruefziffer ist die letzte Ziffer.
         *
         * @param wert eine PZN
         * @return ein Wert zwischen 0 und 9
         */
        override fun getPruefziffer(wert: String): String {
            return wert.last().toString()
        }

        /**
         * Berechnet die Pruefziffer des uebergebenen Wertes.
         *
         * @param wert Wert
         * @return errechnete Pruefziffer
         */
        override fun berechnePruefziffer(wert: String): String {
            val sum = getQuersumme(wert)
            return Integer.toString((sum % 11) % 10)
        }

        private fun getQuersumme(wert: String): Int {
            val digits = wert.toCharArray()
            var sum = 0
            val length = digits.size-1
            val anfangsWichtung = 8 - length
            for (i in 0 until length) {
                val digit = Character.digit(digits[i], 10)
                sum += digit * (anfangsWichtung + i)
            }
            return sum
        }

    }

}
