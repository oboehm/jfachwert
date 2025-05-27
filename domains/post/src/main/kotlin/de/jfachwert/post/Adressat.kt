/*
 * Copyright (c) 2018-2020 by Oliver Boehm
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
 * (c)reated 18.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post

import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * Ein Adressat (oder auch Postempfaenger) ist diejenige Person, die in der
 * Adresse benannt ist und für die damit eine Postsendung bestimmt ist.
 * Hierbei kann es sich um eine natuerliche oder um eine juristische Person
 * handeln.
 *
 * @author oboehm
 * @since 0.5 (18.01.2018)
 */
open class Adressat
/**
 * Erzeugt eine Adressat mit dem angegebenen Namen. Dabei kann es sich um
 * eine natuerliche Person (z.B. "Mustermann, Max") oder eine juristische
 * Person (z.B. "Ich AG") handeln.
 *
 * Das Format des Adressat ist so, wie er auf dem Brief angegeben wird:
 * "Nachname, Vorname" bei Personen bzw. Name bei juristischen Personen.
 *
 * @param name      z.B. "Mustermann, Max"
 * @param validator Validator fuer die Ueberpruefung des Namens
 */
@JvmOverloads constructor(name: String, validator: KSimpleValidator<String> = LengthValidator.NOT_EMPTY_VALIDATOR) : Name(name, validator) {

    /**
     * Der Name ist der Teil vor dem Komma (bei Personen). Bei Firmen ist
     * es der komplette Name.
     *
     * @return z.B. "Mustermann"
     */
    val name: String
        get() = nachname

    /**
     * Bei natuerlichen Personen mit Vornamen kann hierueber der Vorname
     * ermittelt werden.
     *
     * @return z.B. "Max"
     */
    override val vorname: String
        get() {
            if (hasVorname()) {
                return super.vorname
            } else {
                throw IllegalStateException("keine nat\u00fcrliche Person: " + code)
            }
        }



    companion object {

        private val WEAK_CACHE = WeakHashMap<String, Adressat>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = Adressat("", NullValidator())

        /**
         * Liefert einen Adressat mit dem angegebenen Namen.
         *
         * @param name z.B. "Mustermann, Max"
         * @return Addressat mit dem angegebenen Namen
         */
        @JvmStatic
        fun of(name: String): Adressat {
            val copy = String(name.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy) { s: String -> Adressat(String(s.toCharArray())) }
        }

    }

}