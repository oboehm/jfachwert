/*
 * Copyright (c) 2024 by Oliver Boehm
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
 * (c)reated 12.07.24 by oliver (ob@oasd.de)
 */

package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Eine Hilfsmittelnummer ist eine eindeutige Kennzeichnung fuer medizinische
 * Hilfsmittel im Hilfsmittelverzeichnis der gesetzlichen Krankenkassen in
 * Deutschland. Diese Nummer dient der Identifikation und Abrechnung von
 * Hilfsmitteln, wie Prothesen, Rollstuehlen oder Hoergeraeten, die von den
 * Krankenkassen erstattet werden. Sie ermoeglicht eine standardisierte
 * Zuordnung und erleichtert die Kommunikation zwischen Herstellern,
 * Leistungserbringern und Kostentraegern.
 *
 * @author Oli B.
 * @since 5.2 (25.05.2020)
 */
open  class Hilfsmittelnummer
    /**
     * Erzeugt ein neues Hilfsmittel-Objekt.
     *
     * @param code zehnstellige Zahl
     * @param validator Validator zur Pruefung der Zahl
     */
    @JvmOverloads constructor(code: Long, validator: KSimpleValidator<Long> = VALIDATOR) : AbstractFachwert<Long, Hilfsmittelnummer>(code, validator) {

    /**
     * Erzeugt ein neues Hilfsmittel-Objekt.
     *
     * @param code zehnstellige Zahl
     */
    constructor(code: String) : this(toLong(code))

    /**
     * Diese Methode liefert immer 'true' zurueck. Es sei denn, nan hat den
     * Default-Validator beim Anlegen der Hilfsmittelnummer deaktiviert.
     *
     * @return true oder false
     */
    override fun isValid(): Boolean {
        return VALIDATOR.isValid(code)
    }

    /**
     * Die ersten beiden Ziffern stehen fuer die Produktgruppe
     *
     * @return z.B. 18 fuer Kranken- und Behindertenfahrzeuge
     */
    fun getProduktgruppe() : Int {
        return (code / 100_000_000).toInt()
    }

    /**
     * Ziffer 3 und 4 steht fuer den Anwendungsort.
     *
     * @return z.B. 50 fuer Innenraum und Aussenbereich
     */
    fun getAnwendungsort() : Int {
        return (code / 1_000_000 % 100).toInt()
    }

    /**
     * Ziffer 5 und 6 steht fuer die Untergruppe.
     *
     * @return z.B. 3 fuer Adaptivrollstuehle
     */
    fun getUntergruppe() : Int {
        return (code / 10_000 % 100).toInt()
    }

    /**
     * Ziffer 7 steht fuer die Produktart.
     *
     * @return z.B. 2 fuer Spezialrollstuehle
     */
    fun getProduktart() : Int {
        return (code / 1000 % 10).toInt()
    }

    /**
     * Die letzten 3 Ziffern dienen der genauen Produktidentifikation.
     *
     * @return z.B. 6 fuer Mio Design 2018
     */
    fun getProdukt() : Int {
        return (code % 1000).toInt()
    }

    override fun toString(): String {
        return String.format("%02d.%02d.%02d.%d%03d", getProduktgruppe(), getAnwendungsort(), getUntergruppe(),
            getProduktart(), getProdukt())
    }

    /**
     * Im Gegensatz zur toString()-Methode wird hier die Hilfsmittelnummer
     * 10-stellig, aber ohne Trennpunkte ausgegeben.
     *
     * @return z.B. "0507023011"
     */
    fun toShortString(): String {
        return String.format("%010d", code)
    }



    companion object {

        private val WEAK_CACHE = WeakHashMap<Long, Hilfsmittelnummer>()
        /** Default-PZN-Validator. */
        @JvmField
        val VALIDATOR = Validator()
        /** Null-Konstante.  */
        @JvmField
        val NULL = Hilfsmittelnummer(0, NullValidator())

        /**
         * Liefert eine Hilfsmittelnummer zurueck.
         *
         * @param code 10-stellige Nummer
         * @return die Hilfsmittelnummer
         */
        @JvmStatic
        fun of(code: Long): Hilfsmittelnummer {
            return WEAK_CACHE.computeIfAbsent(code) { n: Long -> Hilfsmittelnummer(n) }
        }

        /**
         * Liefert eine Hilfsmittelnummer zurueck.
         *
         * @param code 10-stellige Nummer
         * @return die Hilfsmittelnummer
         */
        @JvmStatic
        fun of(code: String): Hilfsmittelnummer {
            return of(toLong(code))
        }

        private fun toLong(s: String): Long {
            return s.replace(".", "").toLong()
        }

    }

    class Validator : KSimpleValidator<Long> {

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
        override fun validate(value: Long): Long {
            return VALIDATOR10.validate(value)
        }

        companion object {
            private val VALIDATOR10 = LengthValidator<Long>(9, 10)
        }

    }

}