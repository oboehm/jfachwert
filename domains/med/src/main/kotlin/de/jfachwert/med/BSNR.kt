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
 * (c)reated 16.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Die Betriebstaettennummer (BSNR) ist eine neunstellige Nummer, die im Rahmen
 * der vertragsaerztlichen Versorgung den Ort der Leistungserbringung
 * (Betriebsstaette) eindeutig identifiziert. Sie wurde zusammen mit der
 * [LANR] 2008 bundesweit eingefuehrt.
 *
 * @author oboehm
 * @since 1.1 (16.12.2018)
 */
open class BSNR
/**
 * Erzeugt ein neues BSNR-Objekt.
 *
 * @param code neunstellige Zahl
 * @param validator Validator zur Pruefung der Zahl
 */
@JvmOverloads constructor(code: Int, validator: KSimpleValidator<Int> = VALIDATOR) : AbstractFachwert<Int, BSNR>(code, validator) {

    /**
     * Erzeugt ein neues BSNR-Objekt.
     *
     * @param code neunstellige Zahl
     */
    constructor(code: String) : this(code.toInt()) {}

    /**
     * Laut Wikipedia ist "179999900" eine Pseudo-Nummer. In diesem Fall gibt
     * diese Methode "true" zurueck.
     *
     * @return true oder false
     * @since 2.3
     */
    val isPseudoNummer: Boolean
        get() = code == 179999900

    /**
     * Diese Methode liefert immer 'true' zurueck. Es sei denn, nan hat den
     * Default-Validator beim Anlegen der BSNR deaktiviert.
     *
     * @return true oder false
     */
    override fun isValid(): Boolean {
        return VALIDATOR.isValid(code)
    }

    /**
     * Die BSNR ist 9-stellig und wird auch neunstellig ausgegeben.
     *
     * @return 9-stellige Zeichenkette, evtl. mit fuehrenden Nullen
     */
    override fun toString(): String {
        return String.format("%09d", code)
    }



    companion object {

        private val VALIDATOR = LengthValidator<Int>(2, 9)
        private val WEAK_CACHE = WeakHashMap<Int, BSNR>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = BSNR(0, NullValidator())

        /** Pseudonummer.  */
        @JvmField
        val PSEUDO_NUMMER = of(179999900)

        /**
         * Liefert eine BSNR zurueck.
         *
         * @param code 9-stellige Nummer
         * @return die BSNR
         */
        @JvmStatic
        fun of(code: Int): BSNR {
            return WEAK_CACHE.computeIfAbsent(code) { n: Int -> BSNR(n) }
        }

        /**
         * Liefert eine BSNR zurueck.
         *
         * @param code 9-stellige Nummer
         * @return die BSNR
         */
        @JvmStatic
        fun of(code: String): BSNR {
            return of(code.toInt())
        }

        /**
         * Ueberprueft die uebergebenen Nummer, ob sie 9-stellig und eine
         * korrekte BSNR darstellt. Die Pruefziffer wird nicht ueberprueft,
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