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
 * (c)reated 13.04.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.post

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.KFachwert
import de.jfachwert.SimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.validation.ValidationException

/**
 * Ein Ort (oder auch Ortschaft) ist eine Stadt oder Gemeinde. Ein Ort hat
 * i.d.R. eine Postleitzahl (PLZ). Diese ist aber in dieser Klasse optional,
 * sodass man einen Ort auch ohne eine PLZ einsetzen kann.
 *
 * @author oboehm
 * @since 0.2.0 (13.04.2017)
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Ort
/**
 * Hierueber kann ein Ort mit PLZ angelegt werden.
 *
 * @param plz       Postleitzahl des Ortes
 * @param name      Name des Ortes
 * @param validator Validator fuer die Ueberpruefung des Ortes
 */
@JvmOverloads constructor(private val plz: PLZ?, name: String, validator: SimpleValidator<String> = VALIDATOR) : KFachwert {

    /**
     * Liefert den Ortsnamen zurueck.
     *
     * @return den Ortsnamen
     */
    val name: String

    /**
     * Hierueber kann ein Ort (mit oder ohne PLZ) angelegt werden.
     *
     * @param name des Ortes
     */
    constructor(name: String) : this(split(name)) {}

    private constructor(values: Array<String>) : this(if (values[0].isEmpty()) null else PLZ(values[0]), values[1]) {}

    init {
        this.name = verify(name, validator)
    }

    /**
     * Da die Postleitzahl optional ist, wird sie auch als [Optional]
     * zurueckgegeben.
     *
     * @return die PLZ
     */
    val pLZ: Optional<PLZ>
        get() = if (plz == null) {
            Optional.empty()
        } else {
            Optional.of(plz)
        }

    /**
     * Hier wird ein logischer Vergleich vorgenommen, ob der andere Ort
     * der gleiche Ort ist. Kennzeichnend dafuer ist die PLZ. Solange die
     * PLZ die gleiche ist, darf der Ort unterschiedlich geschrieben sein
     * (Bsp.: "73730 Esslingen" und "73730 Esslingen am Necker" werden als
     * gleich angesehen.
     *
     * @param other der andere Ort
     * @return true, falls es der gleiche Ort ist
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Ort) {
            return false
        }
        val thisName = Text.replaceUmlaute(name)
        val otherName = Text.replaceUmlaute(other.name)
        return if (plz == null || other.plz == null) {
            thisName.equals(otherName, ignoreCase = true)
        } else {
            plz == other.plz &&
                    Character.toLowerCase(thisName[0]) == Character.toLowerCase(otherName[0])
        }
    }

    /**
     * Im Gegensatz zur [.equals]-Methode muss hier der andere
     * Ort exakt uebereinstimmen. D.h. Sowohl in der PLZ als auch im Namen.
     *
     * @param other der andere Ort
     * @return true bei exakter Gleichheit
     * @since 2.1
     */
    fun equalsExact(other: Ort): Boolean {
        return plz == other.plz && name == other.name
    }

    /**
     * Da die PLZ optional ist, kann die PLZ nicht fuer den Hashcode
     * herangezogen werden. Und auch beim Ort wird es schwierig, da er
     * unterschiedlich geschrieben werden kann (mit Umlaute oder ohne
     * Umlaute). Fuer den Hashcode wird daher ausgegangen, dass der
     * erste Buchstabe auch bei unterschiedlicher Schreibweise immer
     * gleich ist.
     *
     * @return hashcode
     */
    override fun hashCode(): Int {
        return Character.toUpperCase(Text.replaceUmlaute(name)[0]).toInt()
    }

    /**
     * Liefert den Orstnamen als Ergebnis.
     *
     * @return Ortsname
     */
    override fun toString(): String {
        return if (plz == null) {
            name
        } else {
            plz.toString() + " " + name
        }
    }



    companion object {

        private val VALIDATOR: SimpleValidator<String> = LengthValidator(1)
        private val LOG = Logger.getLogger(Ort::class.java.name)

        /** Null-Wert fuer Initialisierung.  */
        @JvmField
        val NULL = Ort(PLZ.NULL, "", NullValidator())

        /**
         * Hierueber kann ein Ort (mit oder ohne PLZ) angelegt werden.
         *
         * @param name des Ortes
         * @return Ort
         */
        @JvmStatic
        fun of(name: String): Ort {
            return Ort(name)
        }

        /**
         * Hierueber kann ein Ort mit PLZ angelegt werden.
         *
         * @param plz Postleitzahl des Ortes
         * @param name Name des Ortes
         * @return Ort
         */
        @JvmStatic
        fun of(plz: PLZ, name: String): Ort {
            return Ort(plz, name)
        }

        /**
         * Ein Orstname muss mindestens aus einem Zeichen bestehen. Allerdings
         * koennte der ueberbebene Name auch die PLZ noch beinhalten. Dies wird
         * bei der Validierung beruecksichtigt.
         *
         * @param name der Ortsname (mit oder ohne PLZ)
         * @return der validierte Ortsname zur Weiterverabeitung
         */
        fun validate(name: String): String {
            return validate(name, VALIDATOR)
        }

        private fun validate(name: String, validator: SimpleValidator<String>): String {
            val splitted = split(name)
            val ortsname = splitted[1]
            validator.validate(ortsname)
            return name
        }

        private fun verify(name: String, validator: SimpleValidator<String>): String {
            return try {
                validate(name, validator)
            } catch (ex: ValidationException) {
                throw LocalizedIllegalArgumentException(ex)
            }
        }

        private fun split(name: String): Array<String> {
            val input = StringUtils.trimToEmpty(name)
            val splitted = arrayOf("", input)
            if (input.contains(" ")) {
                try {
                    val plz = PLZ.Validator().validate(StringUtils.substringBefore(input, " "))
                    splitted[0] = plz
                    splitted[1] = StringUtils.substringAfter(input, " ").trim { it <= ' ' }
                } catch (ex: ValidationException) {
                    LOG.log(Level.FINE, "no PLZ inside '$name' found:", ex)
                }
            }
            return splitted
        }
    }

}