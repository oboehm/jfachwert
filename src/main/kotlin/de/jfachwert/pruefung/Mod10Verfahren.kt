/*
 * Copyright (c) 2018-2020 by Oliver Boehm
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

import de.jfachwert.PruefzifferVerfahren
import org.apache.commons.lang3.StringUtils

/**
 * Das Modulo-10-Verfahren ist auch als Luhn-Alogorithmus oder Luhn-Formel
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
 * Die verschiedenen Modulo10-Verfahren, die es gibt, unterscheiden sich noch
 * in der Gewichtung der einzelnen Ziffern. Naeheres kann man unter
 * https://www.activebarcode.de/codes/checkdigit/modulo10.html nachlesen.
 *
 * Die Gewichtung gibt an, mit welcher Zahl die ungeraden und geraden
 * Stellen mulitpliziert werden, bevor die Quersumme fuer die Preufung
 * gebildet wird. Bei Barcodes wird hier z.B. die Werte 4 und 9 verwendet.
 *
 * @author oboehm
 * @since 1.1 (11.12.2018)
 */
open class Mod10Verfahren(private val gewichtungUngerade: Int, private val gewichtungGerade: Int) : PruefzifferVerfahren<String> {

    /**
     * Die Gewichtung ist fuer die ungeraden Ziffern relevant. Sie werden
     * damit multipliziert, bevor die Quersumme gebildet wird.
     *
     * @param gewichtung typischerweise z.B. 3
     */
    @JvmOverloads
    constructor(gewichtung: Int = 2) : this(gewichtung, 1) {
    }

    /**
     * Liefert true zurueck, wenn der uebergebene Wert gueltig ist.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return true oder false
     */
    override fun isValid(wert: String): Boolean {
        return if (StringUtils.length(wert) < 1) {
            false
        } else {
            getPruefziffer(wert) == berechnePruefziffer(wert.substring(0, wert.length - 1))
        }
    }

    /**
     * Meistens ist die letzte Ziffer die Pruefziffer, die hierueber abgefragt
     * werden kann.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return meist ein Wert zwischen 0 und 9
     */
    override fun getPruefziffer(wert: String): String {
        return wert.substring(wert.length - 1)
    }

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes. Die Berechung stammt
     * aus https://de.wikipedia.org/wiki/Luhn-Algorithmus#Java.
     *
     * @param wert Wert (ohne Pruefziffer)
     * @return errechnete Pruefziffer
     */
    override fun berechnePruefziffer(wert: String): String {
        val sum = getQuersumme(wert)
        return Integer.toString((10 - sum % 10) % 10)
    }

    private fun getQuersumme(wert: String): Int {
        val digits = wert.toCharArray()
        var sum = 0
        val length = digits.size
        for (i in 0 until length) {
            var digit = Character.digit(digits[i], 10)
            digit *= if (i % 2 == 0) {
                gewichtungUngerade
            } else {
                gewichtungGerade
            }
            sum += digit
        }
        return sum
    }



    companion object {

        /** EAN13 mit Gewichtung 3.  */
        @JvmField
        val EAN13 = Mod10Verfahren(1, 3)

        /** Code25 wird mit der Gewichtung 3 und 1 berchnet.  */
        @JvmField
        val CODE25 = Mod10Verfahren(3, 1)
        /** Leitcode oder Identcode hat eine Gewichtung von 4 und 9.  */

        @JvmField
        val LEITCODE = Mod10Verfahren(4, 9)

    }

}