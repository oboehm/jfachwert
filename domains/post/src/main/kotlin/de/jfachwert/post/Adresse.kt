/*
 * Copyright (c) 2017-2023 by Oliver Boehm
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
import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import de.jfachwert.pruefung.exception.ValidationException
import de.jfachwert.util.ToFachwertSerializer
import org.apache.commons.lang3.RegExUtils
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern

/**
 * Bei einer Adresse kann es sich um eine Wohnungsadresse oder Gebaeudeadresse
 * handeln. Sie besteht aus Ort, Strasse und Hausnummer. Sie unterscheidet sich
 * insofern von einer Anschrift, da der Name nicht Bestandteil der Adresse ist.
 *
 * @author oboehm
 * @since 0.2 (02.05.2017)
 */
@JsonSerialize(using = ToFachwertSerializer::class)
open class Adresse
@JvmOverloads constructor(
    val ort: Ort, private val strasse: String, private val hausnummer: String, validator: KSimpleValidator<String> = VALIDATOR
) : KFachwert {

    /**
     * Zerlegt die uebergebene Adresse in ihre Einzelteile und baut daraus die
     * Adresse zusammen. Folgende Heuristiken werden fuer die Zerlegung
     * herangezogen:
     *
     *  * Reihenfolge kann Ort, Strasse oder Strasse, Ort sein;
     *  * Ort / Strasse werden durch Komma oder Zeilenvorschub getrennt;
     *  * vor dem Ort steht die PLZ.
     *
     * @param adresse z.B. "12345 Entenhausen, Gansstr. 23"
     */
    constructor(adresse: String) : this(split(adresse)) {}

    private constructor(adresse: Array<String>) : this(Ort(adresse[0]), adresse[1], adresse[2]) {}

    /**
     * Erzeugt eine neue Adresse.
     *
     * @param map mit den einzelnen Elementen fuer "plz", "ortsname",
     * "strasse" und "hausnummer".
     */
    @JsonCreator
    constructor(map: Map<String, String>) : this(Ort(PLZ.of(map["plz"]!!), map["ortsname"]!!), map["strasse"]!!, map["hausnummer"]!!) {
    }

    init {
        verify(ort, strasse, hausnummer, validator)
    }

    /**
     * Liefert den Ortsnamen.
     *
     * @return Ortsname
     */
    val ortsname: String
        get() = ort.name

    /**
     * Eine PLZ *muss* fuer eine Adresse vorhanden sein, sonst laesst
     * sich keine Aresse Anlagen. Diese wird hierueber zurueckgegeben.
     *
     * @return z.B. "80739" fuer Gerlingen
     */
    val pLZ: PLZ
        get() = ort.pLZ.get()

    /**
     * Liefert die Strasse.
     *
     * @return the strasse
     */
    fun getStrasse(): String {
        return strasse
    }

    /**
     * Liefert die Strasse in einer abgekuerzten Schreibweise.
     *
     * @return z.B. "Badstr."
     */
    val strasseKurz: String
        get() = if (PATTERN_STRASSE.matcher(strasse).matches()) {
            strasse.substring(0, StringUtils.lastIndexOfIgnoreCase(strasse, "stra") + 3) + '.'
        } else {
            strasse
        }

    /**
     * Liefert die Hausnummer.
     *
     * @return Hausnummer, z.B. "10a"
     */
    fun getHausnummer(): String {
        return hausnummer
    }

    /**
     * Liefert die Hausnummer in Kurzform (ohne Leerzeichen).
     *
     * @return z.B. "1-3"
     */
    val hausnummerKurz: String
        get() = StringUtils.deleteWhitespace(hausnummer)

    /**
     * Legt ein neues Objekt mit dem angegeben Ort an.
     *
     * @param neu: neuer Ort
     * @return neue Adresse
     * @since 6.6
     */
    fun withOrt(neu: Ort) : Adresse {
        return of(neu, strasse, hausnummer)
    }

    /**
     * Legt ein neues Objekt mit der angegeben Strasse an.
     *
     * @param neu: neue Strasse
     * @return neue Adresse
     * @since 6.6
     */
    fun withStrasse(neu: String) : Adresse {
        return of(ort, neu, hausnummer)
    }

    /**
     * Legt einen neues Objekt mit neuer Hausnummer an.
     *
     * @param neu: neue Hausnummer
     * @return neue Adresse
     * @since 6.6
     */
    fun withHausnummer(neu: String) : Adresse {
        return of(ort, strasse, neu)
    }

    /**
     * Hier wird eine logischer Vergleich mit der anderen Adresse
     * durchgefuehrt. So wird nicht zwischen Gross- und Kleinschreibung
     * unterschieden und z.B. "Badstrasse" und "Badstr." werden als
     * die gleiche Strasse angesehen.
     *
     * @param other die andere Adresse
     * @return true oder false
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Adresse) {
            return false
        }
        return ort.equals(other.ort) && equalsStrasse(other) && equalsHausnummer(other)
    }

    private fun equalsStrasse(other: Adresse): Boolean {
        return normalizeStrasse(this).equals(normalizeStrasse(other), ignoreCase = true)
    }

    private fun equalsHausnummer(other: Adresse): Boolean {
        val thisNr = normalizeHausnummer(getHausnummer())
        val otherNr = normalizeHausnummer(other.getHausnummer())
        return thisNr[0] == otherNr[0] || thisNr[1] == otherNr[0] || thisNr[0] == otherNr[1] || thisNr[1] == otherNr[1]
    }

    /**
     * Im Gegensatz zur [.equals]-Methode muss hier die andere
     * Adresse exakt einstimmen, also auch in Gross- und Kleinschreibung.
     *
     * @param other die andere Adresse
     * @return true oder false
     * @since 2.1
     */
    fun equalsExact(other: Adresse): Boolean {
        return ort.equalsExact(other.ort) && strasse == other.strasse &&
                hausnummer.equals(other.hausnummer, ignoreCase = true)
    }

    /**
     * Fokus dieser hashCode-Implementierung liegt auf Einfachheit und
     * Performance.
     *
     * @return hashCode
     */
    override fun hashCode(): Int {
        return normalizeStrasse(this).lowercase().hashCode()
    }

    /**
     * Hierueber wird die Adresse, beginnend mit dem Ort, ausgegeben.
     *
     * @return z.B. "12345 Entenhausen, Gansstrasse 23"
     */
    override fun toString(): String {
        return ort.toString() + ", " + getStrasse() + " " + getHausnummer()
    }

    /**
     * Hierueber wird die Adresse, beginnend mit dem Ort, in Kurzform ausgegeben.
     *
     * @return z.B. "12345 Entenhausen, Gansstr. 23"
     */
    fun toShortString(): String {
        return ort.toString() + ", " + strasseKurz + " " + hausnummerKurz
    }

    /**
     * Liefert die einzelnen Attribute einer Adresse als Map.
     *
     * @return Attribute als Map
     */
    override fun toMap(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["plz"] = pLZ
        map["ortsname"] = ortsname
        map["strasse"] = getStrasse()
        map["hausnummer"] = getHausnummer()
        return map
    }



    companion object {

        private val log = Logger.getLogger(Adresse::class.java.name)
        private val VALIDATOR: KSimpleValidator<String> = LengthValidator(1)
        private val PATTERN_STRASSE = Pattern.compile(".*(?i)tra(ss|[\u00dfe])e$")

        /** Null-Konstante.  */
        @JvmField
        val NULL = Adresse(Ort.NULL, "", "", NullValidator())

        /**
         * Zerlegt die uebergebene Adresse in ihre Einzelteile und baut daraus die
         * Adresse zusammen. Folgende Heuristiken werden fuer die Zerlegung
         * herangezogen:
         *
         *  * Reihenfolge kann Ort, Strasse oder Strasse, Ort sein
         *  * Ort / Strasse werden durch Komma oder Zeilenvorschub getrennt
         *  * vor dem Ort steht die PLZ
         *
         * @param adresse z.B. "12345 Entenhausen, Gansstr. 23"
         * @return Adresse
         */
        @JvmStatic
        fun of(adresse: String): Adresse {
            return Adresse(adresse)
        }

        /**
         * Liefert eine Adresse mit den uebergebenen Parametern.
         *
         * @param ort     Ort
         * @param strasse Strasse mit oder ohne Hausnummer
         * @return Adresse
         */
        @JvmStatic
        fun of(ort: Ort, strasse: String): Adresse {
            val splitted = toStrasseHausnummer(strasse)
            return of(ort, splitted[0], splitted[1])
        }

        /**
         * Liefert eine Adresse mit den uebergebenen Parametern.
         *
         * @param ort        the ort
         * @param strasse    the strasse
         * @param hausnummer the hausnummer
         * @return Adresse
         */
        @JvmStatic
        fun of(ort: Ort, strasse: String, hausnummer: String): Adresse {
            return Adresse(ort, strasse, hausnummer)
        }

        /**
         * Liefert eine Adresse mit den uebergebenen Parametern.
         *
         * @param ort        the ort
         * @param strasse    the strasse
         * @param hausnummer the hausnummer
         * @return Adresse
         * @since 2.1
         */
        @JvmStatic
        fun of(ort: Ort, strasse: String, hausnummer: Int): Adresse {
            return of(ort, strasse, Integer.toString(hausnummer))
        }

        private fun verify(ort: Ort, strasse: String, hausnummer: String, validator: KSimpleValidator<String>) {
            try {
                validate(ort, strasse, hausnummer, validator)
            } catch (ex: ValidationException) {
                throw LocalizedIllegalArgumentException(ex)
            }
        }

        /**
         * Validiert die uebergebene Adresse auf moegliche Fehler.
         *
         * @param ort        der Ort
         * @param strasse    die Strasse
         * @param hausnummer die Hausnummer
         */
        fun validate(ort: Ort, strasse: String, hausnummer: String) {
            if (StringUtils.isBlank(strasse)) {
                throw InvalidValueException(strasse, "street")
            }
            validate(ort, strasse, hausnummer, VALIDATOR)
        }

        private fun validate(ort: Ort, strasse: String, hausnummer: String, validator: KSimpleValidator<String>) {
            if (!ort.pLZ.isPresent) {
                throw InvalidValueException(ort, "postal_code")
            }
            validator.validate(strasse)
            if (StringUtils.isNotBlank(strasse) && StringUtils.isNotBlank(hausnummer) &&
                    Character.isDigit(strasse.trim { it <= ' ' }[0]) && Character.isLetter(hausnummer.trim { it <= ' ' }[0]) &&
                    strasse.length < hausnummer.length) {
                throw InvalidValueException("$strasse $hausnummer", "values_exchanged")
            }
        }

        /**
         * Zerlegt die uebergebene Adresse in ihre Einzelteile und validiert sie.
         * Folgende Heuristiken werden fuer die Zerlegung herangezogen:
         *
         *  * Reihenfolge kann Ort, Strasse oder Strasse, Ort sein
         *  * Ort / Strasse werden durch Komma oder Zeilenvorschub getrennt
         *  * vor dem Ort steht die PLZ
         *
         * @param adresse z.B. "12345 Entenhausen, Gansstr. 23"
         */
        @JvmStatic
        fun validate(adresse: String) {
            val splitted = split(adresse)
            val ort = Ort(splitted[0])
            validate(ort, splitted[1], splitted[2])
        }

        private fun split(adresse: String): Array<String> {
            val lines = StringUtils.trimToEmpty(adresse).split("[,\\n$]".toRegex()).toTypedArray()
            if (lines.size != 2) {
                throw LocalizedIllegalArgumentException(adresse, "address")
            }
            val splitted: MutableList<String> = ArrayList()
            if (hasPLZ(lines[0])) {
                splitted.add(lines[0].trim { it <= ' ' })
                splitted.addAll(toStrasseHausnummer(lines[1]))
            } else {
                splitted.add(lines[1].trim { it <= ' ' })
                splitted.addAll(toStrasseHausnummer(lines[0]))
            }
            return splitted.toTypedArray()
        }

        private fun hasPLZ(line: String): Boolean {
            return try {
                val ort = Ort(line)
                ort.pLZ.isPresent
            } catch (ex: ValidationException) {
                log.log(Level.FINE, "Keine PLZ in '$line' gefunden.")
                log.log(Level.FINER, "Details:", ex)
                false
            }
        }

        private fun toStrasseHausnummer(line: String): List<String> {
            val indexNr = StringUtils.indexOfAny(line, "0123456789")
            return if (indexNr <= 0) {
                Arrays.asList(line.trim { it <= ' ' }, "")
            } else {
                Arrays.asList(line.substring(0, indexNr).trim { it <= ' ' }, line.substring(indexNr).trim { it <= ' ' })
            }
        }

        private fun normalizeStrasse(adr: Adresse): String {
            return Text.replaceUmlaute(RegExUtils.removeAll(adr.strasseKurz, "[\\s\\p{Punct}]"))
        }

        private fun normalizeHausnummer(nr: String): Array<String> {
            val vonBis = nr.replace("[^\\d\\-]".toRegex(), "")
            val splitted = vonBis.split("-").toTypedArray()
            return when (splitted.size) {
                0 -> arrayOf(vonBis, vonBis)
                1 -> arrayOf(splitted[0], splitted[0])
                else -> {
                    if (splitted[1].isEmpty()) {
                        return arrayOf(splitted[0], splitted[0])
                    }
                    splitted
                }
            }
        }
    }

}