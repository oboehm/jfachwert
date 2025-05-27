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
 * (c)reated 24.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer

import de.jfachwert.KSimpleValidator
import de.jfachwert.math.PackedDecimal
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Die steuerliche Identifikationsnummer (SteuerIdNr) ist eine
 * bundeseinheitliche und dauerhafte Identifikationsnummer von in
 * Deutschland gemeldeten Buergern für Steuerzwecke. Die SteuerIdNr
 * besteht aus insgesamt 11 Ziffern, wobei die letzte Ziffer eine
 * Pruefziffer ist.
 *
 * @author oboehm
 * @since 0.1.0
 */
open class SteuerIdNr
    /**
     * Die SteuerIdNr ist eine 11-stellige Zahl mit einer Pruefziffer.
     *
     * @param idNr      11-stellige Zahl
     * @param validator zur Pruefung
     */
    constructor(idNr: String, validator: KSimpleValidator<PackedDecimal> = VALIDATOR) : Steuernummer(idNr, validator) {

    /**
     * Die SteuerIdNr ist eine 11-stellige Zahl mit einer Pruefziffer.
     *
     * @param idNr 11-stellige Zahl
     */
    constructor(idNr: String) : this(idNr, VALIDATOR)

    /**
     * Eigener Validator fuer die SteuerIdNr-Validierung.
     *
     * @since 2.2
     */
    open class Validator : Steuernummer.Validator() {

        override fun validate(nr: String): String {
            LengthValidator.validate(nr, 11)
            return super.validate(nr)
        }

    }

    companion object {

        private val VALIDATOR = Validator()
        private val WEAK_CACHE = WeakHashMap<String, SteuerIdNr>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = SteuerIdNr("", NullValidator())

        /**
         * Die SteuerIdNr ist eine 11-stellige Zahl mit einer Pruefziffer.
         *
         * @param idNr 11-stellige Zahl
         * @return SteuerIdNr
         */
        @JvmStatic
        fun of(idNr: String): SteuerIdNr {
            val copy = String(idNr.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy) { nr -> SteuerIdNr(nr) }
        }

        /**
         * Eine SteuerId muss genau 11 Stellen besitzen.
         *
         * @param nr the nr
         * @return the string
         */
        fun validate(nr: String): String {
            return VALIDATOR.validate(nr)
        }
    }

}
