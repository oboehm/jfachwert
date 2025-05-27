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

import de.jfachwert.PruefzifferVerfahren
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.Mod11Verfahren
import de.jfachwert.pruefung.NoopVerfahren
import de.jfachwert.pruefung.exception.InvalidValueException
import org.apache.commons.lang3.StringUtils

import java.util.*
import java.util.function.Function

/**
 * Die Umsatzsteuer-Identifikationsnummer (USt-IdNr) ist eine eindeutige
 * Kennzeichnung eines Unternehmens innerhalb der Europaeischen Union im
 * umsatzsteuerlichen Sinne.
 *
 * @author oboehm
 * @since 0.1.0
 */
open class UStIdNr
    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das [PruefzifferVerfahren] ueberschreiben koennen.
     * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
     * indem man das [de.jfachwert.pruefung.NoopVerfahren] verwendet.
     *
     * @param nr          die Umsatzsteuer-IdNr.
     * @param pzVerfahren das verwendete PruefzifferVerfahren
     */(nr: String, pzVerfahren: PruefzifferVerfahren<String> = selectPruefzifferVerfahrenFor(nr)) : Text(verify(nr, pzVerfahren)) {

    /**
     * Liefert das Land, zu dem die IBAN gehoert.
     *
     * @return z.B. "DE" (als Locale)
     */
    val land: Locale
        get() = Locale(toLaenderkuerzel(this.code))

    /**
     * Erzeugt eine Umsatzsteuer-IdNr. Die uebergebene Nummer besteht aus
     * einer 2-stelligen Laenderkennung, gefolgt von maximal 12
     * alphanumerischen Zeichen.
     *
     * @param nr, z.B. "DE999999999"
     */
    constructor(nr: String): this(nr, selectPruefzifferVerfahrenFor(nr))

    companion object {

        private val PRUEFZIFFER_VERFAHREN = HashMap<String, PruefzifferVerfahren<String>>()
        private val WEAK_CACHE = WeakHashMap<String, UStIdNr>()

        init {
            PRUEFZIFFER_VERFAHREN["DE"] = Mod11Verfahren(8)
        }

        /**
         * Erzeugt eine Umsatzsteuer-IdNr. Die uebergebene Nummer besteht aus
         * einer 2-stelligen Laenderkennung, gefolgt von maximal 12
         * alphanumerischen Zeichen.
         *
         * @param nr, z.B. "DE999999999"
         * @return UstIdNr
         */
        @JvmStatic
        fun of(nr: String): UStIdNr {
            val copy = String(nr.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy, Function(::UStIdNr))
        }

        private fun selectPruefzifferVerfahrenFor(nr: String): PruefzifferVerfahren<String> {
            val laenderkuerzel = toLaenderkuerzel(nr)
            var verfahren: PruefzifferVerfahren<String>? = PRUEFZIFFER_VERFAHREN[laenderkuerzel]
            if (verfahren == null) {
                verfahren = NoopVerfahren()
            }
            return verfahren
        }

        /**
         * Eine Umsatzsteuer-Id beginnt mit der Laenderkennung (2 Zeichen), gefolgt
         * von maximal 12 alphanumerischen Zeichen. Bei dieser wird, je nach Land, die
         * Pruefziffer validiert (falls bekannt).
         *
         * Die kuerzeste Umsatzsteuer kann in GB mit 5 alphanumerischen Zeichen
         * auftreten.
         *
         * @param nr die Umsatzsteuer-Id, z.B. "DE136695970"
         * @return die validierte Id zur Weiterverarbeitung
         * @since 0.2.0
         */
        fun validate(nr: String): String {
            return selectPruefzifferVerfahrenFor(nr).validate(nr)
        }

        private fun verify(nr: String, verfahren: PruefzifferVerfahren<String>): String {
            val unformatted = StringUtils.remove(nr, ' ')
            LengthValidator.verify(unformatted, 7, 14)
            verfahren.verify(unformatted.substring(2))
            return unformatted
        }

        private fun toLaenderkuerzel(nr: String): String {
            LengthValidator<String>(7).verify(nr)
            val kuerzel = nr.substring(0, 2).uppercase()
            return if (StringUtils.isAlpha(kuerzel)) {
                kuerzel
            } else {
                throw InvalidValueException(nr, "country")
            }
        }
    }

}
