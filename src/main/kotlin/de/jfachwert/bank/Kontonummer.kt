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
import de.jfachwert.SimpleValidator
import de.jfachwert.pruefung.exception.InvalidLengthException
import de.jfachwert.pruefung.exception.InvalidValueException
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Eigentlich ist die Kontonummer Bestandteil der IBAN. Trotzdem wird sie
 * noch hauefig verwendet und ist uns daher einen eigenen Typ wert.
 *
 * @author oboehm
 * @since 0.1.0
 */
open class Kontonummer
/**
 * Hier gehen wir davon aus, dass eine Kontonummer immer eine Zahl ist und
 * fuehrende Nullen keine Rollen spielen.
 *
 * @param nr        Kontnummer als Zahl
 * @param validator fuer die Pruefung
 */
@JvmOverloads constructor(nr: Long, validator: SimpleValidator<Long> = VALIDATOR) : AbstractFachwert<Long, Kontonummer>(nr, validator) {

    /**
     * Hierueber wird eine neue Kontonummer angelegt.
     *
     * @param nr eine maximal 10-stellige Zahl
     */
    constructor(nr: String) : this(nr.trim { it <= ' ' }.toLong()) {}

    /**
     * Um ein einheitliches Format der Kontonummer zu bekommen, geben wir
     * sie immer 10-stellig aus und fuellen sie notfalls mit fuehrenden
     * Nullen auf.
     *
     * @return a string representation of the object.
     */
    override fun toString(): String {
        return String.format("%010d", code)
    }

    /**
     * Dieser Validator ist fuer die Ueberpruefung von Kontonummern vorgesehen.
     *
     * @since 2.2
     */
    class Validator : SimpleValidator<Long> {
        /**
         * Eine gueltige Kontonummer beginnt bei 1 und hat maximal 10 Stellen.
         *
         * @param kontonr die Kontonummer
         * @return die validierte Kontonummer zur Weiterverabeitung
         */
        override fun validate(kontonr: Long): Long {
            if (kontonr < 1) {
                throw InvalidValueException(kontonr, "account_number")
            }
            if (kontonr > 9999999999L) {
                throw InvalidLengthException(java.lang.Long.toString(kontonr), 1, 10)
            }
            return kontonr
        }

        /**
         * Eine gueltige Kontonummer beginnt bei 1 und hat maximal 10 Stellen.
         *
         * @param kontonr die Kontonummer
         * @return die validierte Kontonummer zur Weiterverabeitung
         */
        fun validate(kontonr: String?): String {
            val normalized = StringUtils.trimToEmpty(kontonr)
            try {
                validate(java.lang.Long.valueOf(normalized))
            } catch (nfe: NumberFormatException) {
                throw InvalidValueException(kontonr, "account_number", nfe)
            }
            return normalized
        }
    }

    companion object {
        private val WEAK_CACHE = WeakHashMap<Long, Kontonummer>()
        private val VALIDATOR = Validator()
        /**
         * Liefert eine Kontonummer zurueck.
         *
         * @param nr Kontonummer
         * @return die Kontonummer
         */
        fun of(nr: Long): Kontonummer {
            return WEAK_CACHE.computeIfAbsent(nr) { n: Long -> Kontonummer(n) }
        }

        /**
         * Liefert eine Kontonummer zurueck.
         *
         * @param code Kontonummer
         * @return die Kontonummer
         */
        @JvmStatic
        fun of(code: String): Kontonummer {
            return of(code.toLong())
        }

        /**
         * Eine gueltige Kontonummer beginnt bei 1 und hat maximal 10 Stellen.
         *
         * @param kontonr die Kontonummer
         * @return die validierte Kontonummer zur Weiterverabeitung
         */
        fun validate(kontonr: String?): String {
            return VALIDATOR.validate(kontonr)
        }

        /**
         * Eine gueltige Kontonummer beginnt bei 1 und hat maximal 10 Stellen.
         *
         * @param kontonr die Kontonummer
         * @return die validierte Kontonummer zur Weiterverabeitung
         */
        fun validate(kontonr: Long): Long {
            return VALIDATOR.validate(kontonr)
        }
    }

}