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

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.jfachwert.KFachwert
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import de.jfachwert.util.ToFachwertSerializer
import org.apache.commons.lang3.RegExUtils
import org.apache.commons.lang3.StringUtils
import java.math.BigInteger
import java.util.*
import javax.validation.ValidationException

/**
 * Ein Postfach besteht aus einer Nummer ohne fuehrende Nullen und einer
 * Postleitzahl mit Ortsangabe. Die Nummer selbst ist optional, wenn die
 * durch die Postleitzahl bereits das Postfach abgebildet wird.
 *
 *
 * Im Englischen wird das Postfach oft als POB (Post Office Box) bezeichnet.
 *
 *
 * @author oboehm
 * @since 0.2 (19.06.2017)
 */
@JsonSerialize(using = ToFachwertSerializer::class)
open class Postfach : KFachwert {

    private val nummer: BigInteger?

    /**
     * Liefert den Ort.
     *
     * @return Ort
     */
    val ort: Ort

    /**
     * Zerlegt den uebergebenen String in seine Einzelteile und validiert sie.
     * Folgende Heuristiken werden fuer die Zerlegung herangezogen:
     *
     *  * Format ist "Postfach, Ort" oder nur "Ort" (mit PLZ)
     *  * Postfach ist vom Ort durch Komma oder Zeilenvorschub getrennt
     *
     * @param postfach z.B. "Postfach 98765, 12345 Entenhausen"
     */
    constructor(postfach: String) : this(split(postfach)) {}

    private constructor(postfach: Array<String>) : this(postfach[0], postfach[1]) {}

    /**
     * Erzeugt ein Postfach ohne Postfachnummer. D.h. die PLZ des Ortes
     * adressiert bereits das Postfach.
     *
     * @param ort gueltiger Ort mit PLZ
     */
    constructor(ort: Ort) {
        this.ort = ort
        nummer = null
        validate(ort)
    }

    /**
     * Erzeugt ein Postfach mit Postfachnummer. Wenn die uebergebene Nummer
     * leer ist, wird ein Postfach ohne Postfachnummer erzeugt.
     *
     * @param nummer z.B. "12 34 56"
     * @param ort Ort mit Postleitzahl
     */
    constructor(nummer: String, ort: String) : this(toNumber(nummer), Ort(ort)) {}

    /**
     * Erzeugt ein neues Postfach.
     *
     * @param map mit den einzelnen Elementen fuer "plz", "ortsname" und
     * "nummer".
     */
    @JsonCreator
    constructor(map: Map<String, String>) : this(toNumber(map["nummer"]!!), Ort(PLZ.of(map["plz"]!!), map["ortsname"]!!)) {
    }

    /**
     * Erzeugt ein Postfach.
     *
     * @param nummer positive Zahl ohne fuehrende Null
     * @param ort gueltiger Ort mit PLZ
     */
    constructor(nummer: Long, ort: Ort) : this(BigInteger.valueOf(nummer), ort) {}

    /**
     * Erzeugt ein Postfach.
     *
     * @param nummer positive Zahl ohne fuehrende Null
     * @param ort gueltiger Ort mit PLZ
     */
    constructor(nummer: BigInteger, ort: Ort) {
        this.nummer = nummer
        this.ort = ort
        verify(nummer, ort)
    }

    /**
     * Erzeugt ein Postfach.
     *
     * @param nummer positive Zahl oder leer
     * @param ort Ort
     */
    constructor(nummer: Optional<BigInteger>, ort: Ort) {
        this.nummer = nummer.orElse(null)
        this.ort = ort
        if (this.nummer == null) {
            verify(ort)
        } else {
            verify(nummer.get(), ort)
        }
    }

    /**
     * Liefert die Postfach-Nummer als normale Zahl. Da die Nummer optional
     * sein kann, wird sie als [Optional] zurueckgeliefert.
     *
     * @return z.B. 815
     */
    fun getNummer(): Optional<BigInteger> {
        return if (nummer == null) {
            Optional.empty()
        } else {
            Optional.of(nummer)
        }
    }

    /**
     * Liefert die Postfach-Nummer als formattierte Zahl. Dies macht natuerlich
     * nur Sinn, wenn diese Nummer gesetzt ist. Daher wird eine
     * [IllegalStateException] geworfen, wenn dies nicht der Fall ist.
     *
     * @return z.B. "8 15"
     */
    val nummerFormatted: String
        get() {
            check(getNummer().isPresent) { "no number present" }
            val hundert = BigInteger.valueOf(100)
            val formatted = StringBuilder()
            var i = getNummer().get()
            while (i.compareTo(BigInteger.ONE) > 0) {
                formatted.insert(0, " " + i.remainder(hundert))
                i = i.divide(hundert)
            }
            return formatted.toString().trim { it <= ' ' }
        }

    /**
     * Liefert die Postleitzahl. Ohne gueltige Postleitzahl kann kein Postfach
     * angelegt werden, weswegen hier immer eine PLZ zurueckgegeben wird.
     *
     * @return z.B. 09876
     */
    val pLZ: PLZ
        get() = ort.pLZ.get()

    /**
     * Liefert den Ortsnamen.
     *
     * @return Ortsname
     */
    val ortsname: String
        get() = ort.name

    /**
     * Zwei Postfaecher sind gleich, wenn sie die gleiche Attribute haben.
     *
     * @param other das andere Postfach
     * @return true bei Gleichheit
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Postfach) {
            return false
        }
        return nummer == other.nummer && ort.equals(other.ort)
    }

    /**
     * Da die PLZ meistens bereits ein Postfach adressiert, nehmen dies als
     * Basis fuer die Hashcode-Implementierung.
     *
     * @return hashCode
     */
    override fun hashCode(): Int {
        return ort.hashCode()
    }

    /**
     * Hierueber wird das Postfach einzeilig ausgegeben.
     *
     * @return z.B. "Postfach 8 15, 09876 Nirwana"
     */
    override fun toString(): String {
        return if (getNummer().isPresent) {
            "Postfach " + nummerFormatted + ", " + ort
        } else {
            ort.toString()
        }
    }

    /**
     * Liefert die einzelnen Attribute eines Postfaches als Map.
     *
     * @return Attribute als Map
     */
    override fun toMap(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["plz"] = pLZ
        map["ortsname"] = ortsname
        if (nummer != null) {
            map["nummer"] = nummer
        }
        return map
    }



    companion object {

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = Postfach(Ort.NULL)

        /**
         * Zerlegt den uebergebenen String in seine Einzelteile und validiert sie.
         * Folgende Heuristiken werden fuer die Zerlegung herangezogen:
         *
         *  * Format ist "Postfach, Ort" oder nur "Ort" (mit PLZ)
         *  * Postfach ist vom Ort durch Komma oder Zeilenvorschub getrennt
         *
         * @param postfach z.B. "Postfach 98765, 12345 Entenhausen"
         * @return Postfach
         */
        @JvmStatic
        fun of(postfach: String): Postfach {
            return Postfach(postfach)
        }

        /**
         * Erzeugt ein Postfach.
         *
         * @param nummer positive Zahl ohne fuehrende Null
         * @param ort gueltiger Ort mit PLZ
         * @return Postfach
         */
        @JvmStatic
        fun of(nummer: Long, ort: Ort): Postfach {
            return Postfach(nummer, ort)
        }

        /**
         * Erzeugt ein Postfach.
         *
         * @param nummer positive Zahl ohne fuehrende Null
         * @param ort gueltiger Ort mit PLZ
         * @return Postfach
         */
        @JvmStatic
        fun of(nummer: BigInteger, ort: Ort): Postfach {
            return Postfach(nummer, ort)
        }

        /**
         * Zerlegt das uebergebene Postfach in seine Einzelteile und validiert sie.
         * Folgende Heuristiken werden fuer die Zerlegung herangezogen:
         *
         *  * Format ist "Postfach, Ort" oder nur "Ort" (mit PLZ)
         *  * Postfach ist vom Ort durch Komma oder Zeilenvorschub getrennt
         *
         * @param postfach z.B. "Postfach 98765, 12345 Entenhausen"
         */
        @JvmStatic
        fun validate(postfach: String) {
            val lines = split(postfach)
            toNumber(lines[0])
            val ort = Ort(lines[1])
            if (!ort.pLZ.isPresent) {
                throw InvalidValueException(postfach, "postal_code")
            }
        }

        private fun split(postfach: String): Array<String> {
            val lines = StringUtils.trimToEmpty(postfach).split("[,\\n$]".toRegex()).toTypedArray()
            var splitted = arrayOf("", lines[0])
            if (lines.size == 2) {
                splitted = lines
            } else if (lines.size > 2) {
                throw InvalidValueException(postfach, "post_office_box")
            }
            return splitted
        }

        private fun toNumber(number: String): Optional<BigInteger> {
            if (StringUtils.isBlank(number)) {
                return Optional.empty()
            }
            val unformatted = RegExUtils.replaceAll(number, "Postfach|\\s+", "")
            return try {
                Optional.of(BigInteger(unformatted))
            } catch (nfe: NumberFormatException) {
                throw InvalidValueException(number, "number", nfe)
            }
        }

        /**
         * Validiert das uebergebene Postfach auf moegliche Fehler.
         *
         * @param nummer    Postfach-Nummer (muss positiv sein)
         * @param ort       Ort mit PLZ
         */
        fun validate(nummer: BigInteger, ort: Ort) {
            if (nummer.compareTo(BigInteger.ONE) < 0) {
                throw InvalidValueException(nummer, "number")
            }
            validate(ort)
        }

        private fun verify(nummer: BigInteger, ort: Ort) {
            try {
                validate(nummer, ort)
            } catch (ex: ValidationException) {
                throw LocalizedIllegalArgumentException(ex)
            }
        }

        /**
         * Ueberprueft, ob der uebergebene Ort tatsaechlich ein PLZ enthaelt.
         *
         * @param ort Ort mit PLZ
         */
        fun validate(ort: Ort) {
            if (!ort.pLZ.isPresent) {
                throw InvalidValueException(ort, "postal_code")
            }
        }

        private fun verify(ort: Ort) {
            try {
                validate(ort)
            } catch (ex: ValidationException) {
                throw LocalizedIllegalArgumentException(ex)
            }
        }
    }

}