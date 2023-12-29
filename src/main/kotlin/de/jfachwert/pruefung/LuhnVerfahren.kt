/*
 * Copyright (c) 2018-2022 by Oliver Boehm
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
 * (c)reated 11.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung

/**
 * Das Luhn-Verfahren ist auch als Luhn-Alogorithmus oder Luhn-Formel
 * bekannt und ist eine einfache Methode zur Berechnung einer Pruefsumme.
 * Das Verfahren dient u.a. zur Verifizierung von:
 *
 *  * Kreditkartennummern,
 *  * Sozialversicherungsnummern,
 *  * Nummern von Lokomotiven und Triebwagen.
 *
 * Die Pruefziffer ergibt sich aus der Pruefsumme modulo 10. Sie wird an
 * die bestehende Zahl angehaengt.
 *
 * @author oboehm
 * @since 1.1 (11.12.2018)
 */
open class LuhnVerfahren : Mod10Verfahren() {

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes.
     *
     * @param wert Wert (ohne Pruefziffer)
     * @return errechnete Pruefziffer
     */
    override fun berechnePruefziffer(wert: String): String {
        val sum = getQuersumme(wert)
        return Integer.toString(sum % 10)
    }



    companion object {

        private fun getQuersumme(wert: String): Int {
            if (Regex("[A-Z].*").matches(wert)) {
                return getQuersummeWithLetter(wert)
            }
            var digits = wert.toCharArray()
            var sum = 0
            val length = digits.size
            for (i in 0 until length) { // get digits in reverse order
                var digit = Character.digit(digits[length - i - 1], 10)
                // every 2nd number multiply with 2
                if (i % 2 == 1) {
                    digit *= 2
                }
                sum += if (digit > 9) digit - 9 else digit
            }
            return sum
        }

        /**
         * Beim modifierten Luhn-Verfahren beginnt der Code mit einem
         * Grossbuchstaben. Unter
         * https://de.wikipedia.org/wiki/Krankenversichertennummer
         * ist das Verfahren beschrieben.
         */
        private fun getQuersummeWithLetter(wert: String): Int {
            val n12 = wert.toCharArray()[0] + 1 - 'A'
            return getQuersumme(String.format("%02d%s0", n12, wert.substring(1)))
        }

    }

}