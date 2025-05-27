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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 08.08.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net

import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import org.apache.commons.lang3.Range
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.regex.Pattern

/**
 * Ueber den Domain-Namen wird ein Rechner im Internet adressiert. Man kann
 * ihn zwar auch ueber seine IP-Adresse ansprechen, aber kann man sich als
 * Normalsterblicher schwer merken.
 *
 * Ein Domainname besteht aus mindestens aus Teilen: einem Hostnamen (z. B.
 * ein Firmenname), einem Punkt und einer Top-Level-Domain.
 *
 * @author oboehm
 * @since 0.4 (08.08.2017)
 */
open class Domainname
/**
 * Legt eine Instanz an.
 *
 * @param name      gueltiger Domain-Name
 * @param validator zur Pruefung
 */
@JvmOverloads constructor(name: String, validator: KSimpleValidator<String> = VALIDATOR) : Text(name.trim { it <= ' ' }.lowercase(), validator) {

    /**
     * Liefert die Top-Level-Domain (TLD) zurueck.
     *
     * @return z.B. "de"
     */
    val tLD: Domainname
        get() = Domainname(StringUtils.substringAfterLast(code, "."))

    /**
     * Waehrend die Top-Level-Domain die oberste Ebende wie "de" ist, ist die
     * 2nd-Level-Domain von "www.jfachwert.de" die Domain "jfachwert.de" und
     * die 3rd-Level-Domain ist in diesem Beispiel die komplette Domain.
     *
     * @param level z.B. 2 fuer 2nd-Level-Domain
     * @return z.B. "jfachwert.de"
     */
    fun getLevelDomain(level: Int): Domainname {
        val parts = code.split(".").toTypedArray()
        val firstPart = parts.size - level
        if (firstPart < 0 || level < 1) {
            throw LocalizedIllegalArgumentException(level, "level", Range.of(1, parts.size))
        }
        val name = StringBuilder(parts[firstPart])
        for (i in firstPart + 1 until parts.size) {
            name.append('.')
            name.append(parts[i])
        }
        return Domainname(name.toString())
    }



    /**
     * Dieser Validator ist fuer die Ueberpruefung von Domainnamen vorgesehen.
     *
     * @since 2.2
     */
    class Validator : KSimpleValidator<String> {

        /**
         * Hie valideren wir den Namen auf Richtigkeit. Das Pattern dazu stammt aus
         * https://regex101.com/r/d5Yd6j/1/tests . Allerdings akzeptieren wir auch
         * die TLD wie "de" als gueltigen Domainnamen.
         *
         * @param value Domain-Name
         * @return validierter Domain-Name zur Weiterverarbeitung
         */
        override fun validate(value: String): String {
            if (VALID_PATTERN.matcher(value).matches()) {
                return value
            }
            throw InvalidValueException(value, "name")
        }

        companion object {
            private val VALID_PATTERN = Pattern.compile("^(?=.{1,253}\\.?$)(?:(?!-|[^.]+_)[A-Za-z0-9-_]{1,63}(?<!-)(?:\\.|$))+$")
        }

    }



    companion object {

        private val WEAK_CACHE = WeakHashMap<String, Domainname>()
        private val VALIDATOR: KSimpleValidator<String> = Validator()

        /**
         * Liefert einen Domainnamen.
         *
         * @param name gueltiger Domainname
         * @return Domainname
         */
        @JvmStatic
        fun of(name: String): Domainname {
            val copy = String(name.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy) { s: String -> Domainname(s) }
        }

        /**
         * Hie valideren wir den Namen auf Richtigkeit. Das Pattern dazu stammt aus
         * https://regex101.com/r/d5Yd6j/1/tests . Allerdings akzeptieren wir auch
         * die TLD wie "de" als gueltigen Domainnamen.
         *
         * @param name Domain-Name
         * @return validierter Domain-Name zur Weiterverarbeitung
         */
        fun validate(name: String): String {
            return VALIDATOR.validate(name)
        }

    }

}