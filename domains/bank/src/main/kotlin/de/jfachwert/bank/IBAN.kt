/*
 * Copyright (c) 2017-2024 by Oliver Boehm
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
 * (c)reated 10.03.17 by oliver (ob@jfachwert.de)
 */
package de.jfachwert.bank

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.PruefzifferVerfahren
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.Mod97Verfahren
import de.jfachwert.pruefung.NullValidator
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Die IBAN (International Bank Account Number) ist eine international
 * standardisierte Notation fuer Bankkonten, die durch die ISO-Norm ISO 13616-1
 * beschrieben wird.
 *
 * @author oboehm
 */
open class IBAN
/**
 * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
 * damit diese das [PruefzifferVerfahren] ueberschreiben koennen.
 * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
 * indem man das [de.jfachwert.pruefung.NoopVerfahren] verwendet.
 *
 * @param iban        die IBAN
 * @param pzVerfahren das verwendete PruefzifferVerfahren (optional)
 */
@JvmOverloads constructor(iban: String, pzVerfahren: KSimpleValidator<String> = VALIDATOR) : AbstractFachwert<String, IBAN>(iban, pzVerfahren) {

    /**
     * Liefert die IBAN formattiert in der DIN-Form. Dies ist die uebliche
     * Papierform, in der die IBAN in 4er-Bloecke formattiert wird, jeweils
     * durch Leerzeichen getrennt.
     *
     * @return formatierte IBAN, z.B. "DE19 1234 1234 1234 1234 12"
     */
    val formatted: String
        get() {
            val input = unformatted + "   "
            val buf = StringBuilder()
            var i = 0
            while (i < unformatted.length) {
                buf.append(input, i, i + 4)
                buf.append(' ')
                i += 4
            }
            return buf.toString().trim { it <= ' ' }
        }

    /**
     * Liefert die unformattierte IBAN.
     *
     * @return unformattierte IBA
     */
    val unformatted: String
        get() = code

    /**
     * Liefert das Land, zu dem die IBAN gehoert.
     *
     * @return z.B. "de_DE" (als Locale)
     * @since 0.1.0
     */
    val land: Locale
        get() {
            val country = unformatted.substring(0, 2)
            var language = country.lowercase()
            when (country) {
                "AT", "CH" -> language = "de"
            }
            return Locale(language, country)
        }

    /**
     * Liefert die 2-stellige Pruefziffer, die nach der Laenderkennung steht.
     *
     * @return the pruefziffer
     * @since 0.1.0
     */
    val pruefziffer: String
        get() = MOD97.getPruefziffer(unformatted)

    /**
     * Extrahiert aus der IBAN die Bankleitzahl.
     *
     * @return Bankleitzahl
     * @since 0.1.0
     */
    val bLZ: BLZ
        get() {
            val iban = unformatted
            return BLZ(iban.substring(4, 12))
        }

    /**
     * Extrahiert aus der IBAN die Kontonummer nach der Standard-IBAN-Regel.
     * Ausnahmen, wie sie z.B. in
     * http://www.kigst.de/media/Deutsche_Bundesbank_Uebersicht_der_IBAN_Regeln_Stand_Juni_2013.pdf
     * beschrieben sind, werden nicht beruecksichtigt.
     *
     * @return 10-stellige Kontonummer
     * @since 0.1.0
     */
    val kontonummer: Kontonummer
        get() {
            val iban = unformatted
            return Kontonummer(iban.substring(12))
        }

    /**
     * Dieser Validator ist fuer die Ueberpruefung von IBANS vorgesehen.
     *
     * @since 2.2
     */
    class Validator : KSimpleValidator<String> {
        /**
         * Mit dieser Methode kann man eine IBAN validieren, ohne dass man erst
         * den Konstruktor aufrufen muss. Falls die Pruefziffer nicht stimmt,
         * wird eine ValidationException geworfen, wenn
         * die Laenge nicht uebereinstimmt eine
         * [de.jfachwert.pruefung.exception.InvalidLengthException].
         * Die Laenge liegt zwischen 16 (Belgien) und 34 Zeichen.
         *
         * @param value die 22-stellige IBAN
         * @return die IBAN in normalisierter Form (ohne Leerzeichen)
         */
        override fun validate(value: String): String {
            val normalized = StringUtils.remove(value, ' ').uppercase()
            LengthValidator.validate(normalized, 16, 34)
            when (normalized.substring(0, 2)) {
                "AT" -> LengthValidator.validate(normalized, 20)
                "CH" -> LengthValidator.validate(normalized, 21)
                "DE" -> LengthValidator.validate(normalized, 22)
            }
            return MOD97.validate(normalized)
        }
    }

    companion object {
        private val MOD97 = Mod97Verfahren.instance
        private val WEAK_CACHE = WeakHashMap<String, IBAN>()
        private val VALIDATOR: KSimpleValidator<String> = Validator()
        /** Konstante fuer unbekannte IBAN (aus Wikipedia, aber mit korrigierter Pruefziffer).  */
        @JvmField
        val UNBEKANNT = IBAN("DE07123412341234123412")
        /** Null-Konstante.  */
        @JvmField
        val NULL = IBAN("", NullValidator())

        /**
         * Liefert eine IBAN.
         *
         * @param code gueltige IBAN-Nummer
         * @return IBAN
         */
        @JvmStatic
        fun of(code: String): IBAN {
            return WEAK_CACHE.computeIfAbsent(code) { iban: String -> IBAN(iban) }
        }

        /**
         * Mit dieser Methode kann man eine IBAN validieren, ohne dass man erst
         * den Konstruktor aufrufen muss. Falls die Pruefziffer nicht stimmt,
         * wird eine [ValidationException] geworfen, wenn
         * die Laenge nicht uebereinstimmt eine
         * [de.jfachwert.pruefung.exception.InvalidLengthException].
         * Die Laenge liegt zwischen 16 (Belgien) und 34 Zeichen.
         *
         * @param iban die 22-stellige IBAN
         * @return die IBAN in normalisierter Form (ohne Leerzeichen)
         */
        @JvmStatic
        fun validate(iban: String): String {
            return VALIDATOR.validate(iban)
        }

    }

}