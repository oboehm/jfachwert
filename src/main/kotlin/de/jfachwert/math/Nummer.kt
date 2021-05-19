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
 * (c)reated 24.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math

import de.jfachwert.KFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Die Klasse Nummer dient zum Abspeichern einer beliebigen Nummer. Eine Nummer
 * ist eine positive Ganzzahl und beginnt ueblicherweise mit 1. Dabei kann es
 * sich um eine laufende Nummer, Start-Nummer, Trikot-Nummer, ... handeln.
 *
 * Die Klasse ist Speicher-optimiert, um auch eine große Zahl von Nummern im
 * Speicher halten zu koennen. Und man kann damit auch Zahlen mit fuehrenden
 * Nullen (wie z.B. PLZ) abbilden.
 *
 * Urspruenglich war diese Klasse als Ergaenzung zur [de.jfachwert.Text]-
 * Klasse gedacht.
 *
 * @author oboehm
 * @since 0.6 (24.01.2018)
 */
open class Nummer(code: String) : AbstractNumber(), KFachwert {

    private val code: PackedDecimal



    companion object {

        private val CACHE = arrayOfNulls<Nummer>(11)
        private val VALIDATOR: KSimpleValidator<String> = Validator()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmStatic
        val NULL = Nummer("")

        /**
         * Die of-Methode liefert fuer kleine Nummer immer dasselbe Objekt zurueck.
         * Vor allem wenn man nur kleinere Nummern hat, lohnt sich der Aufruf
         * dieser Methode.
         *
         * @param code Nummer
         * @return eine (evtl. bereits instanziierte) Nummer
         */
        @JvmStatic
        fun of(code: Long): Nummer {
            return if (code >= 0 && code < CACHE.size) {
                CACHE[code.toInt()]!!
            } else {
                Nummer(code)
            }
        }

        /**
         * Die of-Methode liefert fuer kleine Nummer immer dasselbe Objekt zurueck.
         * Vor allem wenn man nur kleinere Nummern hat, lohnt sich der Aufruf
         * dieser Methode.
         *
         * @param code Nummer
         * @return eine (evtl. bereits instanziierte) Nummer
         */
        @JvmStatic
        fun of(code: String): Nummer {
            return of(Nummer(code))
        }

        /**
         * Die of-Methode liefert fuer kleine Nummer immer dasselbe Objekt zurueck.
         * Vor allem wenn man nur kleinere Nummern hat, lohnt sich der Aufruf
         * dieser Methode.
         *
         * @param code Nummer
         * @return eine (evtl. bereits instanziierte) Nummer
         */
        @JvmStatic
        fun of(code: BigInteger): Nummer {
            return of(code.toLong())
        }

        /**
         * Die of-Methode liefert fuer kleine Nummer immer dasselbe Objekt zurueck.
         * Vor allem wenn man nur kleinere Nummern hat, lohnt sich der Aufruf
         * dieser Methode.
         *
         *
         * Diese Methode dient dazu, um ein "ueberfluessige" Nummern, die
         * durch Aufruf anderer Methoden entstanden sind, dem Garbage Collector
         * zum Aufraeumen zur Verfuegung zu stellen.
         *
         *
         * @param other andere Nummer
         * @return eine (evtl. bereits instanziierte) Nummer
         */
        @JvmStatic
        fun of(other: Nummer): Nummer {
            return of(other.toLong())
        }

        /**
         * Ueberprueft, ob der uebergebene String auch tatsaechlich eine Zahl ist.
         *
         * @param nummer z.B. "4711"
         * @return validierter String zur Weiterverarbeitung
         */
        @JvmStatic
        fun validate(nummer: String): String {
            return VALIDATOR.validate(nummer)
        }

        init {
            for (i in CACHE.indices) {
                CACHE[i] = Nummer(i)
            }
        }
    }

    /**
     * Erzeugt eine Nummer als positive Ganzzahl.
     *
     * @param code eine Zahl, z.B. 42
     */
    constructor(code: Int) : this(BigInteger.valueOf(code.toLong())) {}

    /**
     * Erzeugt eine Nummer als positive Ganzzahl.
     *
     * @param code eine Zahl, z.B. 42
     */
    constructor(code: Long) : this(BigInteger.valueOf(code)) {}

    /**
     * Erzeugt eine beliebige Gleitkomma- oder Ganzzahl.
     *
     * @param code eine beliebige Zahl
     */
    constructor(code: BigInteger) : this(code.toString()) {}

    /**
     * Diese Methode liefert die Zahl als BigDecimal zurueck und wird fuer
     * die Default-Implementierung der Number-Methoden benoetigt.
     *
     * @return die Zahl als [BigDecimal]
     */
    override fun toBigDecimal(): BigDecimal {
        return code.toBigDecimal()
    }

    /**
     * Liefert die Zahl als Integer zurueck.
     *
     * @return z.B. 42
     */
    override fun toInt(): Int {
        return code.toInt()
    }

    /**
     * Liefert die Zahl als 'long' zurueck.
     *
     * @return z.B. 42L
     */
    override fun toLong(): Long {
        return code.toLong()
    }

    /**
     * Zwei Nummer sind dann gleich, wenn sie exact gleich geschrieben werden.
     * D.h. fuehrende Nullen werden beim Vergleich nicht ignoriert.
     *
     * @param other Vergleichsobjekt
     * @return true oder false
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Nummer) {
            return false
        }
        return this.toString() == other.toString()
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    override fun hashCode(): Int {
        return Objects.hash(code)
    }

    /**
     * Hier wird die Nummer inklusive fuehrende Null (falls vorhanden)
     * ausgegeben.
     *
     * @return z.B. "0711"
     */
    override fun toString(): String {
        return code.toString()
    }

    /**
     * Dieser Validator ist fuer die Ueberpruefung von Zahlen vorgesehen.
     *
     * @since 3.0
     */
    class Validator : KSimpleValidator<String> {
        /**
         * Wenn die uebergebene Waehrungsstring gueltig ist, wird sie
         * unveraendert zurueckgegeben, damit sie anschliessend von der
         * aufrufenden Methode weiterverarbeitet werden kann. Ist der Wert
         * nicht gueltig, wird eine [InvalidValueException] geworfen.
         *
         * @param nummer Zahl, die validiert wird
         * @return Zahl selber, wenn sie gueltig ist
         */
        override fun validate(nummer: String): String {
            return try {
                BigInteger(nummer).toString()
            } catch (nfe: NumberFormatException) {
                throw InvalidValueException(nummer, "number")
            }
        }
    }

    init {
        this.code = PackedDecimal.valueOf(code)
    }

}