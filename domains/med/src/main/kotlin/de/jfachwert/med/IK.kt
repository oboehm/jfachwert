/*
 * Copyright (c) 2018-2024 by Oliver Boehm
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
 * (c)reated 10.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.PruefzifferVerfahren
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.LuhnVerfahren
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Die Klasse IK repraesentiert das neunstellige Instituionskennzeichen, das
 * bundesweit eindeutig ist. Es wird fuer Abrechnungen im Bereich der deutschen
 * Sozialversicherung verwendet.
 *
 *
 * Die IK selbst ist eine neunstellige Ziffernfolge, die wie folgt aufgabaut
 * ist:
 *
 *  * 1+2: Klassifikation (beginnend bei 10)
 *  * 3+4: Regionalbereich
 *  * 5-8: Seriennummer
 *  * 9: Pruefziffer (aus den Stellen 3 bis 8)
 *
 * @author oboehm
 * @since 1.1 (10.12.2018)
 */
open class IK
/**
 * Erzeugt ein neues IK-Objekt.
 *
 * @param code      Institutionskennzeichen (mit Pruefziffer), z.B. 260326822
 * @param validator Validator zur Pruefung der Zahl
 */
@JvmOverloads constructor(code: Int, validator: KSimpleValidator<Int> = VALIDATOR) : AbstractFachwert<Int, IK>(code, validator) {

    /**
     * Erzeugt ein neues IK-Objekt.
     *
     * @param code Institutionskennzeichen (mit Pruefziffer), z.B. "260326822"
     */
    constructor(code: String) : this(code.toInt()) {}

    /**
     * Diese Methode liefert immer 'true' zurueck. Es sei denn, nan hat den
     * Default-Validator beim Anlegen der IK deaktiviert.
     *
     * @return true oder false
     */
    override fun isValid(): Boolean {
        return VALIDATOR.isValid(code)
    }

    /**
     * Die ersten beiden Ziffern bilden die Klassifikation, die hierueber
     * zurueckgegeben werden kann.
     *
     * @return Zahl zwischen 10 und 99
     */
    val klassifikation: Int
        get() = code / 10000000

    /**
     * Ziffer 3 und 4 stehen fuer den Regionalbereich. So steht z.B. 91 fuer
     * die Region Schwaben.
     *
     * @return Zahl zwischen 0 und 99
     */
    val regionalbereich: Int
        get() = code / 100000 % 100

    /**
     * Die Seriennummer ermittelt sich aus Ziffer 5 bis 8.
     *
     * @return 4-stellige Zahl
     */
    val seriennummer: Int
        get() = code / 10 % 10000

    /**
     * Die letzte Ziffer ist die Pruefziffer.
     *
     * @return Ziffer zwischen 0 und 9
     */
    val pruefziffer: Int
        get() = code / 100000000

    /**
     * Die LANR ist 9-stellig und wird auch neunstellig ausgegeben.
     *
     * @return 9-stellige Zeichenkette, evtl. mit fuehrenden Nullen
     */
    override fun toString(): String {
        return String.format("%09d", code)
    }



    /**
     * Dieser Validator ist auf IK abgestimmt. Er kombiniert den
     * MOD10-Validator mit dem LengthValidator.
     *
     * @since 2.2
     */
    class Validator : KSimpleValidator<Int> {

        /**
         * Wenn der uebergebene Wert gueltig ist, soll er unveraendert
         * zurueckgegeben werden, damit er anschliessend von der aufrufenden
         * Methode weiterverarbeitet werden kann. Ist der Wert nicht gueltig,
         * soll eine [de.jfachwert.pruefung.exception.ValidationException]
         * geworfen werden.
         *
         * @param value Wert, der validiert werden soll
         * @return Wert selber, wenn er gueltig ist
         */
        override fun validate(value: Int): Int {
            val n = VALIDATOR9.validate(value)
            if (!isSpezialIK(n)) {
                MOD10.validate(Integer.toString(n))
            }
            return n
        }

        private fun isSpezialIK(n: Int): Boolean {
            val klass = n / 10000000
            return klass != 26 && klass != 34 || n % 10000000 == 9999999
        }

        companion object {
            private val MOD10: PruefzifferVerfahren<String> = LuhnVerfahren()
            private val VALIDATOR9 = LengthValidator<Int>(2, 9)
        }

    }



    companion object {

        private val VALIDATOR: KSimpleValidator<Int> = Validator()
        private val WEAK_CACHE = WeakHashMap<Int, IK>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = IK(0, NullValidator())

        /**
         * Liefert eine IK zurueck.
         *
         * @param ik 9-stelliges Insituionskennzeichen
         * @return eine gueltige IK
         */
        @JvmStatic
        fun of(ik: Int): IK {
            return WEAK_CACHE.computeIfAbsent(ik) { code: Int -> IK(code) }
        }

        /**
         * Liefert eine IK zurueck.
         *
         * @param ik 11-stelliges Insituionskennzeichen
         * @return eine gueltige IK
         */
        @JvmStatic
        fun of(ik: String): IK {
            return of(ik.toInt())
        }

        /**
         * Ueberprueft die uebergebenen Nummer, ob sie 9-stellig und eine
         * korrekte IK darstellt.
         *
         * @param nummer 9-stellige Nummer
         * @return die Nummer selbst zur Weiterverarbeitung
         */
        fun validate(nummer: Int): Int {
            return VALIDATOR.validate(nummer)
        }

    }

}