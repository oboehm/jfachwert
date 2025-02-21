/*
 * Copyright (c) 2025 by Oli B.
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
 * (c)reated 20.02.25 by oboehm
 */
package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * LEGS ist der Leistungserbringergruppenschluessel - einem 7-stelligen
 * Schluessel, der sich aus einem 2-stelligen AKtionscode (AC) und 5-stelligen
 * Tarifkennzeichen (TK) zusammengesetzt ist.
 *
 * @author oboehm
 * @since 6.1 (20.02.25)
 */
open class LEGS
/**
 * Erzeugt ein neues BSNR-Objekt.
 *
 * @param code neunstellige Zahl
 * @param validator Validator zur Pruefung der Zahl
 */
@JvmOverloads constructor(code: String, validator: KSimpleValidator<String> = VALIDATOR) : AbstractFachwert<String, LEGS>(code, validator) {

    /**
     * Liefert die LEGS formattiert mit Leerzeichen zurueck
     *
     * @return formatierte LEGS, z.B. "81 02 999"
     */
    val formatted: String
        get() {
            return code.substring(0, 2) + " " + code.substring(2, 4) + " " + getTK()
        }

    /**
     * Liefert die unformattierte LEGS.
     *
     * @return unformattierte LEGS
     */
    val unformatted: String
        get() = code

    /**
     * Liefert das Tarifkennzeichen (TK).
     *
     * @return 3-stellige Vertragsnummer, z.B. "B05"
     */
    open fun getTK() : String {
        return code.substring(4)
    }

    /**
     * Eine gueltige LEGS besteht aus einem 7-stelligen Schluessel.
     *
     * @return normalerweise 'true'
     */
    override fun isValid(): Boolean {
        return VALIDATOR.isVald(code)
    }

    /**
     * Dieser Validator ist fuer die Ueberpruefung von LEGS vorgesehen.
     */
    class Validator : KSimpleValidator<String> {
        /**
         * Mit dieser Methode kann man eine LEGS validieren, ohne dass man erst
         * den Konstruktor aufrufen muss. Formattierungszeichen werden dabei
         * fuer die Pruefung aus dem String entfernt
         *
         * @param value ein 7-stelliger Schluessel, evtl. mit Formattierung
         * @return LEGS in normalisierter Form (ohne Formattier-Zeichen)
         */
        override fun validate(value: String): String {
            val normalized = value.replace(" ", "").replace("/", "")
            return LengthValidator.validate(normalized, 7, 7)
        }
    }

    companion object {

        private val VALIDATOR = Validator()
        private val WEAK_CACHE = WeakHashMap<String, LEGS>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = LEGS("0", NullValidator())

        /**
         * Liefert eine LEGS zurueck.
         *
         * @param code 7-stelliger Schluessel, z.B. "15 02 B05"
         * @return die LEGS
         */
        @JvmStatic
        fun of(code: String): LEGS {
            return WEAK_CACHE.computeIfAbsent(code) { s: String -> LEGS(s) }
        }

    }

}