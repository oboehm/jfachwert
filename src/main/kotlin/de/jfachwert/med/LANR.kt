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
 * (c)reated 12.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.SimpleValidator
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.Mod10Verfahren
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Die LANR ist die lebenslange Arztnummer. Sie ist eine neunstellige Nummer,
 * die bundesweit von der jeweiligen zustaendigen Kassenaerztlichen Vereinigung
 * an jeden Arzt und Psychotherapeuten vergeben wird, der an der
 * vertragsaertzlichen Versorgung teilnimmt.
 *
 * @author oboehm
 * @since 1.1 (12.12.2018)
 */
open class LANR
/**
 * Erzeugt ein neues LANR-Objekt.
 *
 * @param code      neunstellige Zahl
 * @param validator Validator zur Pruefung der Zahl
 */
@JvmOverloads constructor(code: Int, validator: SimpleValidator<Int> = VALIDATOR) : AbstractFachwert<Int, LANR>(code, validator) {

    /**
     * Erzeugt ein neues LANR-Objekt.
     *
     * @param code neunstellige Zahl
     */
    constructor(code: String) : this(code.toInt()) {}

    /**
     * Die ersten 7 Ziffern kennzeichnen die Arztnummer, die von der
     * Kassenaerztlichen Vereinigung generiert wird. Die 7. Stelle ist
     * dabei die Pruefziffer, die aber leider nicht einheitlich
     * generiert wird und daher zur Pruefung schlecht herangezogen werden
     * kann.
     *
     * @return 7-stellige Arztnummer (inkl. Pruefziffer)
     */
    val arztnummer: Int
        get() = code / 100

    /**
     * Stelle 7 ist die Pruefziffer, die aber wertlos ist, da sie nicht
     * einheitlich generiert wird.
     *
     * @return Zahl zwischen 0 und 9
     */
    val pruefziffer: Int
        get() = code / 100 % 10

    /**
     * Hier wird die 7-stellige Arztnummer ueberprueft, ob die Pruefziffer
     * gueltig ist. Diese wird nach dem Modulo10-Verfahren mit der Gewichtung
     * 4 und 9 ueberprueft. Allerdings wird die Pruefung von den
     * verschiedenen Instituten teilweise unterschiedlich interpretiert,
     * so dass das Ergebnis mit Vorsicht zu geniessen ist.
     *
     * @return true, wenn Pruefziffer uebereinstimmt
     */
    val isValid: Boolean
        get() {
            val mod10Verfahren = Mod10Verfahren(4, 9)
            return mod10Verfahren.isValid(Integer.toString(arztnummer))
        }

    /**
     * Die letzten beiden Ziffern der LANR bilden die Fachgruppe.
     *
     * @return Zahl zwischen 1 und 99
     */
    val fachgruppe: Int
        get() = code % 100

    /**
     * Neben "999999900" als Pseudo-Arztnummer gibt es noch weitere Nummern,
     * die als Pseudo-Nummer angesehen werden. So ist nach
     * https://www.aok-gesundheitspartner.de/imperia/md/gpp/nordost/heilberufe/datenaustausch/lieferbedingungen.pdf
     * die "3333333xx" und "4444444xx" eine Pseudo-Nummer, und nach
     * https://www.gkv-datenaustausch.de/media/dokumente/leistungserbringer_1/krankenhaeuser/fortschreibungen_1/20170522_14_fs.pdf
     * die "555555..." solch eine Pseudo-Nummer.
     *
     * @return true oder false
     * @since 2.3
     */
    val isPseudoNummer: Boolean
        get() {
            val arztNr = arztnummer
            return when (arztNr) {
                3333333, 4444444, 9999999 -> true
                else -> arztNr / 10 == 555555
            }
        }

    /**
     * Die LANR ist 9-stellig und wird auch neunstellig ausgegeben.
     *
     * @return 9-stellige Zeichenkette, evtl. mit fuehrenden Nullen
     */
    override fun toString(): String {
        return String.format("%09d", code)
    }



    companion object {

        private val VALIDATOR = LengthValidator<Int>(4, 9)
        private val WEAK_CACHE = WeakHashMap<Int, LANR>()

        /** Null-Konstante fuer Initialisierungen.  */
        val NULL = LANR(0, NullValidator())

        /** Pseudonummer fuer Bundeswehraerzte, Zahnaerzte und Hebammen.  */
        @JvmField
        val PSEUDO_NUMMER = of(999999900)

        /**
         * Liefert eine LANR zurueck.
         *
         * @param code 9-stellige Nummer
         * @return die LANR
         */
        @JvmStatic
        fun of(code: Int): LANR {
            return WEAK_CACHE.computeIfAbsent(code) { n: Int -> LANR(n) }
        }

        /**
         * Liefert eine LANR zurueck.
         *
         * @param code 9-stellige Nummer
         * @return die LANR
         */
        @JvmStatic
        fun of(code: String): LANR {
            return of(code.toInt())
        }

        /**
         * Ueberprueft die uebergebenen Nummer, ob sie 9-stellig und eine
         * korrekte LANR darstellt. Die Pruefziffer wird nicht ueberprueft,
         * weil sie optional ist und nicht unbedingt stimmen muss.
         *
         * @param nummer 9-stellige Nummer
         * @return die Nummer selbst zur Weiterverarbeitung
         */
        fun validate(nummer: Int): Int {
            return VALIDATOR.validate(nummer)
        }

    }

}