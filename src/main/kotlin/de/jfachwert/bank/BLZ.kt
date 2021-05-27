/*
 * Copyright (c) 2017-2020 by Oliver Boehm
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
 * (c)reated 16.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.math.PackedDecimal
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.NumberValidator
import org.apache.commons.lang3.RegExUtils
import java.util.*

/**
 * Die BLZ (Bankleitzahl) ist eine eindeutige Kennziffer, die in Deutschland
 * und Oesterreich eindeutig ein Kreditinstitut identifiziert. In Deutschland
 * ist die BLZ eine 8-stellige, in Oesterreich eine 5-stellige Zahl (mit
 * Ausnahme der Oesterreichischen Nationalbank mit 3 Stellen).
 *
 * Zur Reduzierung des internen Speicherverbrauchs wird die BLZ als
 * [PackedDecimal] abgelegt.
 *
 * @author oboehm
 * @since 16.03.2017
 */
open class BLZ
/**
 * Hierueber wird eine neue BLZ angelegt.
 *
 * @param code      eine 5- oder 8-stellige Zahl
 * @param validator fuer die Ueberpruefung
 */
@JvmOverloads constructor(code: String, validator: KSimpleValidator<PackedDecimal> = VALIDATOR) :
        AbstractFachwert<PackedDecimal, BLZ>(PackedDecimal.valueOf(code), validator) {

    /**
     * Hierueber wird eine neue BLZ angelegt.
     *
     * @param code eine 5- oder 8-stellige Zahl
     */
    constructor(code: Int) : this(Integer.toString(code)) {}

    /**
     * Liefert die unformattierte BLZ.
     *
     * @return unformattierte BLZ, z.B. "64090100"
     */
    val unformatted: String
        get() = code.toString()

    /**
     * Liefert die BLZ in 3er-Gruppen formattiert.
     *
     * @return formatierte LBZ, z.B. "640 901 00"
     */
    val formatted: String
        get() {
            val input = unformatted + "   "
            val buf = StringBuilder()
            var i = 0
            while (i < unformatted.length) {
                buf.append(input, i, i + 3)
                buf.append(' ')
                i += 3
            }
            return buf.toString().trim { it <= ' ' }
        }

    /**
     * Dieser Validator ist fuer die Ueberpruefung von BLZs vorgesehen.
     *
     * @since 2.2
     */
    class Validator : KSimpleValidator<PackedDecimal> {
        /**
         * Eine BLZ darf maximal 8-stellig sein.
         *
         * @param value die Bankleitzahl
         * @return die Bankleitzahl zur Weiterverabeitung
         */
        override fun validate(value: PackedDecimal): PackedDecimal {
            val normalized = validate(value.toString())
            return PackedDecimal.of(normalized)
        }

        /**
         * Eine BLZ darf maximal 8-stellig sein.
         *
         * @param blz die Bankleitzahl
         * @return die Bankleitzahl zur Weiterverabeitung
         */
        fun validate(blz: String?): String {
            val normalized = RegExUtils.replaceAll(blz, "\\s", "")
            return NUMBER_VALIDATOR.validate(normalized)
        }

        /**
         * Eine BLZ darf maximal 8-stellig sein.
         *
         * @param blz die Bankleitzahl
         * @return die Bankleitzahl zur Weiterverabeitung
         */
        fun validate(blz: Int): Int {
            validate(Integer.toString(blz))
            return blz
        }

        companion object {
            private val NUMBER_VALIDATOR = NumberValidator(100, 99999999)
        }
    }

    companion object {

        private val WEAK_CACHE = WeakHashMap<String, BLZ>()
        private val VALIDATOR = Validator()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = BLZ("", NullValidator())

        /**
         * Eine BLZ darf maximal 8-stellig sein.
         *
         * @param blz die Bankleitzahl
         * @return die Bankleitzahl zur Weiterverabeitung
         */
        @JvmStatic
        fun validate(blz: String): String {
            return VALIDATOR.validate(PackedDecimal.of(blz)).toString()
        }

        /**
         * Liefert eine BLZ zurueck.
         *
         * @param code maximal 8-stellige Nummer
         * @return die BLZ
         */
        @JvmStatic
        fun of(code: Int): BLZ {
            return of(Integer.toString(code))
        }

        /**
         * Liefert eine BLZ zurueck.
         *
         * @param code maximal 8-stellige Nummer
         * @return die BLZ
         */
        @JvmStatic
        fun of(code: String): BLZ {
            return WEAK_CACHE.computeIfAbsent(code) { n: String -> BLZ(n) }
        }
    }

}