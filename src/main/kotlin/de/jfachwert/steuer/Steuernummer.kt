/*
 * Copyright (c) 2017-2019 by Oliver Boehm
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
 * (c)reated 13.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer

import de.jfachwert.AbstractFachwert
import de.jfachwert.PruefzifferVerfahren
import de.jfachwert.SimpleValidator
import de.jfachwert.math.PackedDecimal
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.Mod11Verfahren
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Die Steuernummer oder Steuer-Identnummer ist eine eindeutige Nummer, die vom
 * Finanzamt vergeben wird. Die Nummer ist eindeutig einem Steuerpflichtigen
 * zugeordnet.
 *
 * Die Laenge der Steuernummer variierte beim Standardschema der Laender
 * zwischen 10 und 11 Ziffern und hatte für das Bundesschema einheitlich 13
 * Ziffern.
 *
 * Seit 2008 ist die Steuernummer durch die Steuer-Identifikationsnummer
 * abgeloest, die aus 10 Ziffer + Pruefziffer besteht. Diese Unterscheidung
 * wird in dieser Klasse aber (noch) nicht vorgenommen.
 *
 *
 * Zur Reduzierung des internen Speicherverbrauchs wird die BLZ als
 * [PackedDecimal] abgelegt.
 *
 *
 * @author oboehm
 * @since 0.0.2
 */
open class Steuernummer
    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das [PruefzifferVerfahren] ueberschreiben koennen.
     * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
     * indem man das [de.jfachwert.pruefung.NoopVerfahren] verwendet.
     *
     * @param nr          die Steuernummer
     * @param pzVerfahren das verwendete PruefzifferVerfahren
     */
    constructor(nr: String, pzVerfahren: SimpleValidator<PackedDecimal> = VALIDATOR) : AbstractFachwert<PackedDecimal, Steuernummer>(PackedDecimal.of(nr), pzVerfahren) {

    /**
     * Hierueber wird eine neue Steuernummer angelegt.
     *
     * @param nr eine 10- bis 13-stellige Steuernummer.
     */
    constructor(nr: String) : this(nr, VALIDATOR)

    /**
     * Die letzte Ziffer ist die Pruefziffer, die hierueber abgefragt werden
     * kann.
     *
     * @return Wert zwischen 0 und 9
     */
    val pruefziffer: Int
        get() = VALIDATOR.getPruefziffer(this.code)


    /**
     * Eigener Validator fuer die Steuernummern-Validierung.
     *
     * @since 2.2
     */
    open class Validator : SimpleValidator<PackedDecimal> {

        /**
         * Die Steuernummer muss zwischen 10 und 13 Stellen lang sein und die
         * Pruefziffer muss stimmen (falls sie bekannt ist).
         *
         * @param nr die Steuernummer
         * @return die validierte Steuernummer zur Weiterverarbeitung
         */
        override fun validate(nr: PackedDecimal): PackedDecimal {
            validate(nr.toString())
            return nr
        }

        open fun validate(nr: String): String {
            LengthValidator.validate(nr, 10, 13)
            return if (nr.length == 11) {
                MOD11.verify(nr)
            } else nr
        }

        /**
         * Die letzte Ziffer ist die Pruefziffer, die hierueber abgefragt werden
         * kann.
         *
         * @param nr Steuernummer
         * @return Wert zwischen 0 und 9
         */
        fun getPruefziffer(nr: PackedDecimal): Int {
            return Integer.parseInt(MOD11.getPruefziffer(nr.toString()))
        }

        companion object {

            private val MOD11 = Mod11Verfahren(10)
        }

    }

    companion object {

        private val VALIDATOR = Validator()
        private val WEAK_CACHE = WeakHashMap<String, Steuernummer>()

        /** Null-Konstante fuer Initialisierungen.  */
        val NULL = Steuernummer("", NullValidator())

        /**
         * Hierueber wird eine neue Steuernummer angelegt.
         *
         * @param nr eine 10- bis 13-stellige Steuernummer.
         * @return Steuernummer
         */
        @JvmStatic
        fun of(nr: String): Steuernummer = WEAK_CACHE.computeIfAbsent(nr) { n -> Steuernummer(n) }

        /**
         * Die Steuernummer muss zwischen 10 und 13 Stellen lang sein und die
         * Pruefziffer muss stimmen (falls sie bekannt ist).
         *
         * @param nr die Steuernummer
         * @return die validierte Steuernummer zur Weiterverarbeitung
         */
        fun validate(nr: String): String {
            return VALIDATOR.validate(nr)
        }
    }

}
