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
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.ValidationException
import de.jfachwert.util.ToFachwertSerializer
import org.apache.commons.lang3.StringUtils
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Die Anschrift besteht aus Namen und Adresse oder Postfach. Der Name kann
 * dabei eine Person oder eine Personengruppe (zum Beispiel Unternehmen,
 * Vereine und Aehnliches) sein.
 *
 * @author oboehm
 * @since 0.2 (12.05.2017)
 */
@JsonSerialize(using = ToFachwertSerializer::class)
open class Anschrift private constructor(
        val adressat: Adressat, private val adresse: Adresse?, private val postfach: Postfach?) : KFachwert {

    /**
     * Zerlegt die uebergebene Anschrift in Adressat und Adresse oder Postfach,
     * um daraus eine Anschrift zu erzeugen. Folgende Heuristiken werden fuer
     * die Zerlegung herangezogen:
     *
     *  * Adressat steht an erster Stelle
     *  * Einzelteile werden durch Komma oder Zeilenvorschub getrennt
     *
     * @param anschrift z.B. "Donald Duck, 12345 Entenhausen, Gansstr. 23"
     */
    constructor(anschrift: String) : this(split(anschrift)) {}

    private constructor(anschrift: Array<Any?>) : this(Adressat(anschrift[0].toString()), anschrift[1] as Adresse?, anschrift[2] as Postfach?) {}

    /**
     * Erzeugt eine neue Anschrift aus der uebergebenen Map.
     *
     * @param map mit den einzelnen Elementen fuer "adressat" und "adresse".
     */
    @JsonCreator
    constructor(map: Map<String, Any>) : this(Adressat(map["adressat"].toString()), Adresse(map = map["adresse"] as Map<String, String>)) {
    }

    /**
     * Erzeugt aus dem Adressaten und Adresse eine Anschrift.
     *
     * @param name    Namen einer Person oder Personengruppe
     * @param adresse eine gueltige Adresse
     */
    constructor(name: Adressat, adresse: Adresse?) : this(name, adresse, null) {}

    /**
     * Erzeugt aus dem Adressaten und einem Postfach eine Anschrift.
     *
     * @param name     Namen einer Person oder Personengruppe
     * @param postfach ein gueltiges Postfach
     */
    constructor(name: Adressat, postfach: Postfach?) : this(name, null, postfach) {}

    init {
        if (adresse == null) {
            if (postfach == null) {
                throw InvalidValueException("post_office_box")
            }
        } else {
            if (postfach != null) {
                throw InvalidValueException(adresse, ADDRESS)
            }
        }
    }

    /**
     * Liefert den Namen. Ein Name kann eine Person oder eine Personengruppe
     * (zum Beispiel Unternehmen, Vereine und Aehnliches) sein. Will man den
     * kompletten Namen (mit Vor- und Nachname), nimmt man die
     * [.getAdressat]-Methode.
     *
     * @return z.B. "Mustermann"
     */
    val name: String
        get() = adressat.name

    /**
     * Liefert die Adresse der Anschrift. Voraussetzung fuer den Aufruf dieser
     * Methode ist, dass die Anschrift tatsaechlich eine Adresse enthaelt, und
     * kein Postfach.
     *
     * @return eine gueltige Adresse
     */
    fun getAdresse(): Adresse {
        checkNotNull(adresse) { "no address available" }
        return adresse
    }

    /**
     * Liefert das Postfach der Anschrift. Voraussetzung fuer den Aufruf dieser
     * Methode ist, dass die Anschrift tatsaechlich ein Postfach enthaelt, und
     * keine Adresse.
     *
     * @return ein gueltiges Postfach
     */
    fun getPostfach(): Postfach {
        checkNotNull(postfach) { "no post office box available" }
        return postfach
    }

    /**
     * Hierueber kann abgefragt werden, ob die Anschrift eine Adresse oder ein
     * Postfach beinhaltet.
     *
     * @return true bei Postfach
     */
    fun hasPostfach(): Boolean {
        return postfach != null
    }

    /**
     * Zwei Anschriften sind gleich, wenn sie den gleichen Namen und die
     * gleiche Adresse besitzen. Dabei spielt es keine Rolle, ob der Name
     * gross- oder kleingeschrieben ist.
     *
     * @param other die andere Anschrift
     * @return true bei Gleichheit
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Anschrift) {
            return false
        }
        return (name.equals(other.name, ignoreCase = true)
                && getAdresse().equals(other.getAdresse()))
    }

    /**
     * Da eine hashCode-Methode performant sein soll, wird nur der Name zur
     * Bildung des Hash-Codes herangezogen.
     *
     * @return Hashcode des Namen.
     */
    override fun hashCode(): Int {
        return adressat.hashCode()
    }

    /**
     * Der Namen mit Anschrift wird einzeilig zurueckgegeben.
     *
     * @return z.B. "Dagobert Duck, 12345 Entenhausen, Geldspeicher 23"
     */
    override fun toString(): String {
        return name + ", " + if (hasPostfach()) getPostfach() else getAdresse()
    }

    /**
     * Liefert die einzelnen Attribute eines Postfaches als Map.
     *
     * @return Attribute als Map
     */
    override fun toMap(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["adressat"] = adressat
        map["adresse"] = getAdresse()
        return map
    }



    companion object {

        private val log = Logger.getLogger(Anschrift::class.java.name)
        private const val ADDRESS = "address"

        /** Null-Wert fuer Initialisierung.  */
        @JvmField
        val NULL = Anschrift(Adressat.NULL, Adresse.NULL)

        /**
         * Zerlegt die uebergebene Anschrift in Adressat und Adresse oder Postfach,
         * um daraus eine Anschrift zu erzeugen. Folgende Heuristiken werden fuer
         * die Zerlegung herangezogen:
         *
         *  * Adressat steht an erster Stelle
         *  * Einzelteile werden durch Komma oder Zeilenvorschub getrennt
         *
         * @param anschrift z.B. "Donald Duck, 12345 Entenhausen, Gansstr. 23"
         * @return Anschrift
         */
        @JvmStatic
        fun of(anschrift: String): Anschrift {
            return Anschrift(anschrift)
        }

        /**
         * Erzeugt aus dem Adressaten und einem Postfach eine Anschrift.
         *
         * @param name     Namen einer Person oder Personengruppe
         * @param postfach ein gueltiges Postfach
         * @return Anschrift
         */
        @JvmStatic
        fun of(name: Adressat, postfach: Postfach): Anschrift {
            return Anschrift(name, postfach)
        }

        /**
         * Erzeugt aus dem Adressaten und Adresse eine Anschrift.
         *
         * @param name    Namen einer Person oder Personengruppe
         * @param adresse eine gueltige Adresse
         * @return Anschrift
         */
        @JvmStatic
        fun of(name: Adressat, adresse: Adresse): Anschrift {
            return Anschrift(name, adresse)
        }

        /**
         * Zerlegt die uebergebene Anschrift in Adressat und Adresse oder Postfach
         * fuer die Validierung.i Folgende Heuristiken werden fuer die Zerlegung
         * herangezogen:
         *
         *  * Adressat steht an erster Stelle
         *  * Einzelteile werden durch Komma oder Zeilenvorschub getrennt
         *
         * @param anschrift z.B. "Donald Duck, 12345 Entenhausen, Gansstr. 23"
         */
        @JvmStatic
        fun validate(anschrift: String) {
            split(anschrift)
        }

        private fun split(anschrift: String): Array<Any?> {
            val lines = StringUtils.trimToEmpty(anschrift).split("[,\\n$]".toPattern()).toTypedArray()
            if (lines.size < 2) {
                throw InvalidValueException(anschrift, ADDRESS)
            }
            val parts = arrayOfNulls<Any>(3)
            parts[0] = Adressat(lines[0])
            val adresseOrPostfach = anschrift.substring(lines[0].length + 1).trim { it <= ' ' }
            try {
                parts[1] = null
                parts[2] = Postfach(adresseOrPostfach)
            } catch (ex: ValidationException) {
                log.log(Level.FINE, "'$adresseOrPostfach' ist kein Postfach.")
                log.log(Level.FINER, "Details:", ex)
                parts[1] = Adresse(adresseOrPostfach)
                parts[2] = null
            }
            return parts
        }
    }

}