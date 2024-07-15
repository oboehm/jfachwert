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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post

import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.NumberValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import org.apache.commons.lang3.Range
import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.util.*

/**
 * Eine Postleitzahl (PLZ) kennzeichnet den Zustellort auf Briefen, Paketten
 * oder Paeckchen. In Deutschland ist es eine 5-stellige Zahl, wobei auch
 * "0" als fuehrende Ziffer erlaubt ist.
 *
 * Vor der Postleitzahl kann auch noch eine Kennung (wie 'D' fuer Deutschland)
 * stehen. Diese wird mit abgespeichert, wenn sie angegeben ist.
 *
 * @author oboehm
 * @since 0.2.0 (10.04.2017)
 */
open class PLZ : Text {

    /**
     * Hierueber wird eine Postleitzahl angelegt.
     *
     * @param plz z.B. "70839" oder "D-70839"
     */
    constructor(plz: String) : super(plz, VALIDATOR)

    /**
     * Hierueber wird eine Postleitzahl angelegt.
     *
     * @param plz z.B. "70839" oder "D-70839"
     * @param validator fuer die Ueberpruefung
     */
    constructor(plz: String, validator: KSimpleValidator<String>) : super(plz, validator)

    /**
     * Ueber diesen Konstruktor kann die Landeskennung als extra Parameter
     * angegeben werden.
     *
     * @param landeskennung z.B. "D"
     * @param plz z.B. "70839" (fuer Gerlingen)
     */
    constructor(landeskennung: String, plz: String) : this(landeskennung + plz)

    /**
     * Ueber diesen Konstruktor kann die Landeskennung als extra Parameter
     * angegeben werden.
     *
     * @param land z.B. "de_DE"
     * @param plz z.B. "70839" (fuer Gerlingen)
     */
    constructor(land: Locale, plz: String) : this(toLandeskennung(land) + plz)

    /**
     * Hierueber kann man abfragen, ob der Postleitzahl eine Landeskennung
     * vorangestellt ist.
     *
     * @return true, falls PLZ eine Kennung besitzt
     */
    fun hasLandeskennung(): Boolean {
        return hasLandeskennung(code)
    }

    /**
     * Liefert die Landeskennung als String. Wenn keine Landeskennung
     * angegeben wurde, wird eine Exception geworfen.
     *
     * @return z.B. "D" fuer Deutschland
     */
    val landeskennung: String
        get() {
            check(this.hasLandeskennung()) { "keine Landeskennung angegeben" }
            return getLandeskennung(code)
        }

    /**
     * Liefert das Land, die der Landeskennung entspricht.
     *
     * @return z.B. "de_CH" (fuer die Schweiz)
     */
    val land: Locale
        get() {
            val kennung = landeskennung
            return when (kennung) {
                "D" -> Locale("de", "DE")
                "A" -> Locale("de", "AT")
                "CH" -> Locale("de", "CH")
                else -> throw UnsupportedOperationException("unbekannte Landeskennung '$kennung'")
            }
        }

    /**
     * Liefert die eigentliche Postleitzahl ohne Landeskennung.
     *
     * @return z,B. "01001"
     */
    val postleitZahl: String
        get() = getPostleitZahl(code)

    /**
     * Liefert die PLZ in kompakter Schreibweise (ohne Trennzeichen zwischen
     * Landeskennung und eigentlicher PLZ) zurueck.
     *
     * @return z.B. "D70839"
     */
    fun toShortString(): String {
        return code
    }

    /**
     * Liefert die PLZ in mit Trennzeichen zwischen Landeskennung (falls
     * vorhanden) und eigentlicher PLZ zurueck.
     *
     * @return z.B. "D-70839"
     */
    fun toLongString(): String {
        var plz = code
        if (this.hasLandeskennung()) {
            plz = toLongString(plz)
        }
        return plz
    }

    /**
     * Aus Lesbarkeitsgruenden wird zwischen Landeskennung und eigentlicher PLZ
     * ein Trennzeichen mit ausgegeben.
     *
     * @return z.B. "D-70839"
     */
    override fun toString(): String {
        return this.toLongString()
    }



    /**
     * In dieser Klasse sind die Validierungsregeln der diversen
     * PLZ-Validierungen zusammengefasst. Fuer Deutschland gilt z.B.:
     *
     *  * Eine PLZ besteht aus 5 Ziffern.
     *  * Steht an erster Stelle eine 0, folgt darauf eine Zahl zwischen
     *    1 und 9.
     *  * Steht an erster Stelle eine Zahl zwischen 1 und 9, folgt darauf
     *    eine Zahl zwischen 0 und 9.
     *  * An den letzten drei Stellen steht eine Zahl zwischen 0 und 9.
     *
     * Will man eine PLZ online fuer verschiedene Laender validieren, kann man
     * auf [Zippotam](http://api.zippopotam.us/) zurueckgreifen.
     */
    class Validator : KSimpleValidator<String> {

        /**
         * Eine Postleitahl muss zwischen 3 und 10 Ziffern lang sein. Eventuell
         * kann noch die Laenderkennung vorangestellt werden. Dies wird hier
         * ueberprueft.
         *
         * @param value die PLZ
         * @return die validierte PLZ (zur Weiterverarbeitung)
         */
        override fun validate(value: String): String {
            var plz = normalize(value)
            if (hasLandeskennung(plz)) {
                validateNumberOf(plz)
            } else {
                plz = LengthValidator.validate(plz, 3, 10)
                if (plz.length == 5) {
                    validateNumberDE(plz)
                }
            }
            return plz
        }

        companion object {

            private fun validateNumberOf(plz: String) {
                val kennung = getLandeskennung(plz)
                val zahl = getPostleitZahl(plz)
                when (kennung) {
                    "D" -> {
                        validateNumberDE(zahl)
                        validateNumberWith(plz, 6, zahl)
                    }
                    "CH" -> validateNumberWith(plz, 6, zahl)
                    "A" -> validateNumberWith(plz, 5, zahl)
                    else -> LengthValidator.validate(zahl, 3, 10)
                }
            }

            private fun validateNumberWith(plz: String, length: Int, zahl: String) {
                LengthValidator.validate(plz, length)
                NumberValidator(BigDecimal.ZERO, BigDecimal.TEN.pow(length)).validate(zahl)
            }

            private fun validateNumberDE(plz: String) {
                val n = plz.toInt()
                if (n < 1067 || n > 99998) {
                    throw InvalidValueException(plz, "postal_code", Range.of("01067", "99998"))
                }
            }

            private fun normalize(plz: String): String {
                return StringUtils.replaceChars(plz, " -", "").uppercase()
            }
        }
    }



    companion object {

        private val VALIDATOR: KSimpleValidator<String> = Validator()
        private val WEAK_CACHE = WeakHashMap<String, PLZ>()

        /** Null-Wert fuer Initialisierung.  */
        @JvmField
        val NULL = PLZ("00000", NullValidator())

        private fun toLandeskennung(locale: Locale): String {
            val country = locale.country.uppercase()
            return when (country) {
                "AT", "DE" -> country.substring(0, 1)
                else -> country
            }
        }

        /**
         * Liefert eine PLZ zurueck.
         *
         * @param plz PLZ als String
         * @return PLZ
         */
        @JvmStatic
        fun of(plz: String): PLZ {
            return WEAK_CACHE.computeIfAbsent(plz) { s: String -> PLZ(s) }
        }

        private fun hasLandeskennung(plz: String): Boolean {
            val kennung = plz[0]
            return Character.isLetter(kennung)
        }

        private fun getLandeskennung(plz: String): String {
            return StringUtils.substringBefore(toLongString(plz), "-")
        }

        private fun getPostleitZahl(plz: String): String {
            return if (!hasLandeskennung(plz)) {
                plz
            } else StringUtils.substringAfter(toLongString(plz), "-")
        }

        private fun toLongString(plz: String): String {
            val i = StringUtils.indexOfAny(plz, "0123456789")
            if (i < 0) {
                throw InvalidValueException(plz, "postal_code")
            }
            return plz.substring(0, i) + "-" + plz.substring(i)
        }

    }

}