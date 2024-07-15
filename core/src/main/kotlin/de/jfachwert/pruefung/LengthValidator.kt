/*
 * Copyright (c) 2017-2022 by Oliver Boehm
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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung

import de.jfachwert.PruefzifferVerfahren
import de.jfachwert.pruefung.exception.InvalidLengthException
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import java.io.Serializable
import java.util.*

/**
 * Bei der Laengen-Validierung wird nur die Laenge des Fachwertes geprueft, ob
 * er zwischen der erlaubten Minimal- und Maximallaenge liegt. Ist die
 * Minimallaenge 0, sind leere Werte erlaubt, ist die Maximallaenge unendlich
 * (bzw. groesster Integer-Wert), gibt es keine Laengenbeschraenkung.
 *
 * Urspruenglich besass diese Klasse rein statisiche Methoden fuer die
 * Laengenvaliderung. Ab v0.3.1 kann sie auch anstelle eines
 * Pruefziffernverfahrens eingesetzt werden.
 *
 * @author oboehm
 * @since 0.2 (20.04.2017)
 */
open class LengthValidator<T : Serializable> @JvmOverloads constructor(private val min: Int, private val max: Int = Int.MAX_VALUE) : NoopVerfahren<T>() {

    /**
     * Liefert true zurueck, wenn der uebergebene Wert innerhalb der erlaubten
     * Laenge liegt.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return true oder false
     */
    override fun isValid(wert: T): Boolean {
        val length = Objects.toString(wert, "").length
        return length >= min && length <= max
    }

    /**
     * Ueberprueft, ob der uebergebenen Werte innerhalb der min/max-Werte
     * liegt.
     *
     * @param value zu ueberpruefender Wert
     * @return den ueberprueften Wert (zur Weiterverarbeitung)
     */
    override fun validate(value: T): T {
        if (!isValid(value)) {
            throw InvalidLengthException(Objects.toString(value), min, max)
        }
        return value
    }



    companion object {

        val NOT_EMPTY_VALIDATOR: PruefzifferVerfahren<String> = LengthValidator(1)

        /**
         * Validiert die Laenge des uebergebenen Wertes.
         *
         * @param value zu pruefender Wert
         * @param expected erwartete Laenge
         * @return der gepruefte Wert (zur evtl. Weiterverarbeitung)
         */
        fun validate(value: String, expected: Int): String {
            if (value.length != expected) {
                throw InvalidLengthException(value, expected)
            }
            return value
        }

        /**
         * Validiert die Laenge des uebergebenen Wertes.
         *
         * @param value zu pruefender Wert
         * @param min   geforderte Minimal-Laenge
         * @param max   Maximal-Laenge
         * @return der gepruefte Wert (zur evtl. Weiterverarbeitung)
         */
        fun validate(value: String, min: Int, max: Int): String {
            if (min == max) {
                return validate(value, min)
            }
            if (value.length < min || value.length > max) {
                throw InvalidLengthException(value, min, max)
            }
            return value
        }

        /**
         * Verifziert die Laenge des uebergebenen Wertes. Im Gegensatz zur
         * [.validate]-Methode wird herbei eine
         * [IllegalArgumentException] geworfen.
         *
         * @param value zu pruefender Wert
         * @param min   geforderte Minimal-Laenge
         * @param max   Maximal-Laenge
         * @return der gepruefte Wert (zur evtl. Weiterverarbeitung)
         */
        fun verify(value: String, min: Int, max: Int): String {
            return try {
                validate(value, min, max)
            } catch (ex: IllegalArgumentException) {
                throw LocalizedIllegalArgumentException(ex)
            }
        }

    }

}