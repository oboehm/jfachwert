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
 * (c)reated 11.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank

import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.exception.InvalidLengthException
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * BIC steht fuer Bank (oder auch Businiess) Identifier Code und kennzeichnet
 * weltweit Kreditinstitute, Broker oder aehnliche Unternehmen. Im Allegemeinen
 * wird die BIC im Zahlungsverkehr zusammen mit der IBAN verwendet.
 *
 * Der BIC hat eine Laenge von 11 oder 14 alphanumerischen Zeichen mit
 * folgendem Aufbau: BBBBCCLLbbb
 *
 *  * BBBB: 4-stelliger Bankcode, vom Geldinstitut frei waehlbar
 *    (nur Buchstaben)
 *  * CC: 2-stelliger Laendercode nach ISO 3166-1 (nur Buchstaben)
 *  * LL: 2-stellige Codierung des Ortes (Buchstaben/Ziffern)
 *  * bbb: 3-stellige Kennzeichnung (Branche-Code) der Filiale oder
 *    Abteilung. Kann um "XXX" auf 6-stellig ergaenzt werden
 *    (Buchstaben/Ziffern)
 *
 * @author oliver (ob@aosd.de)
 */
open class BIC
/**
 * Hierueber wird eine neue BIC angelegt.
 *
 * @param code      eine 11- oder 14-stellige BIC
 * @param validator zum Pruefen der BIC (optional)
 */
@JvmOverloads constructor(code: String, validator: KSimpleValidator<String> = VALIDATOR) : Text(code, validator) {

    /**
     * Dieser Validator ist fuer die Ueberpruefung von BICs vorgesehen.
     *
     * @since 2.2
     */
    class Validator : KSimpleValidator<String> {
        /**
         * Hierueber kann man eine BIC validieren.
         *
         * @param value die BIC (11- oder 14-stellig)
         * @return die validierte BIC (zur Weiterverarbeitung)
         */
        override fun validate(value: String): String {
            val normalized = StringUtils.trim(value)
            val allowedLengths = Arrays.asList(8, 11, 14)
            if (!allowedLengths.contains(normalized.length)) {
                throw InvalidLengthException(normalized, allowedLengths)
            }
            return normalized
        }
    }

    companion object {

        private val WEAK_CACHE = WeakHashMap<String, BIC>()
        private val VALIDATOR: KSimpleValidator<String> = Validator()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = BIC("", NullValidator())

        /**
         * Liefert eine BIC zurueck.
         * <p>
         * Anmerkung: der uebergebene Text wird kopiert, weil sonst die
         * verwendete WeakHashMap eine StrongReference daraus macht
         * (s. Issue #29).
         * </p>
         *
         * @param code eine 11- oder 14-stellige BIC
         * @return Text
         */
        @JvmStatic
        fun of(code: String): BIC {
            val copy = String(code.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy) { n: String -> BIC(n) }
        }

        /**
         * Hierueber kann man eine BIC ohne den Umweg ueber den Konstruktor
         * validieren.
         *
         * @param bic die BIC (11- oder 14-stellig)
         * @return die validierte BIC (zur Weiterverarbeitung)
         */
        fun validate(bic: String): String {
            return VALIDATOR.validate(bic)
        }
    }

}