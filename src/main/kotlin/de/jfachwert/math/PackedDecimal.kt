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
 * (c)reated 29.03.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.Fachwert
import de.jfachwert.SimpleValidator
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.logging.Logger
import javax.validation.constraints.NotNull

/**
 * Die Klasse PackedDecimal dienst zum speicherschonende Speichern von Zahlen.
 * Sie greift die Idee von COBOL auf, wo es den numerischen Datentyp
 * "COMPUTATIONAL-3 PACKED" gibt, wo die Zahlen in Halb-Bytes (Nibbles)
 * abgespeichert wird. D.h. In einem Byte lassen sich damit 2 Zahlen
 * abspeichern. Diese Praesentation ist auch als BCD (Binary Coded Decimal)
 * bekannt (s. [BCD-Code](https://de.wikipedia.org/wiki/BCD-Code)
 * in Wikipedia).
 *
 * Dieser Datentyp eignet sich damit fuer:
 *
 *  * Abspeichern grosser Menge von Zahlen, wenn dabei die interne
 * Speichergroesse relevant ist,
 *  * Abspeichern von Zahlen beliebiger Groesse
 * (Ersatz fuer [java.math.BigDecimal],
 *  * Abspeichern von Zahlen mit fuehrender Null (z.B. Vorwahl).
 *
 * Eine noch kompaktere Darstellung (ca. 20%) laesst sich mit der Chen-Ho- oder
 * Densely-Packed-Decimal-Kodierung (s.
 * [A Summary of Densely Packed Decimal encoding](http://speleotrove.com/decimal/DPDecimal.html)).
 * Diese kommt hier aber nicht zum Einsatz. Stattdessen kommt der BCD-Algorithmus
 * zum Einsatz. Dadurch koennen auch weitere Trenn- und Fuell-Zeichen aufgenommen
 * werden:
 *
 *  * Vorzeichen (+, -)
 *  * Formattierung ('.', ',')
 *  * Leerzeichen
 *  * Trennzeichen (z.B. fuer Telefonnummern)
 *
 * Die einzelnen Werte, die ein Halb-Byte (Nibble) aufnimmt, sind (angelehnt an
 * [COMPUTATIONAL-3 PACKED](http://acc-gmbh.com/dochtml/Datentypen4.html)
 * in COBOL):
 * <pre>
 * +-----+---+--------------------------------------------------+
 * | 0x0 | 0 | Ziffer 0                                         |
 * | 0x1 | 1 | Ziffer 1                                         |
 * | ... |   |                                                  |
 * | 0x9 | 9 | Ziffer 9                                         |
 * | 0xA | / | Trennzeichen fuer Brueche                        |
 * | 0xB |   | Leerzeichen (Blank)                              |
 * | 0xC | + | positives Vorzeichen                             |
 * | 0xD | - | negatives Vorzeichen                             |
 * | 0xE | . | Formatzeichen Tausenderstelle (im Deutschen)     |
 * | 0xF | , | Trennung Vorkomma/Nachkommastelle (im Deutschen) |
 * +-----+---+--------------------------------------------------+
</pre> *
 *
 *
 * Damit koennen auch Zeichenketten nachgebildet werden, die strenggenommen
 * keine Dezimalzahl darstellen, z.B. "+49/811 32 16-8". Dies ist zwar
 * zulaessig, jedoch duerfen damit keine mathematische Operation angewendet
 * werden. Ansonsten kann die Klasse ueberall dort eingesetzt werden, wo
 * auch eine [java.math.BigDecimal] verwendet wird.
 *
 *
 *
 * Die API orientiert sich an die API von [BigDecimal] und ist auch von
 * der [Number]-Klasse abgeleitet. Allerdings werden noch nicht alle
 * Methoden von [unterstuetzt][BigDecimal]. In diesem Fall kann man auf
 * die Methode [.toBigDecimal] ausweichen.
 *
 *
 *
 * Da diese Klasse eher eine technische als eine fachliche Klasse ist, wurde
 * die englische Bezeichnung aus COBOL uebernommen. Sie wird von einigen
 * Fachwert-Klassen intern verwendet, kann aber auch fuer eigene Zwecke
 * verwendet werden.
 *
 *
 * @author oboehm
 * @since 0.6 (29.03.2018)
 */
@JsonSerialize(using = ToStringSerializer::class)
open class PackedDecimal @JvmOverloads constructor(zahl: String, validator: SimpleValidator<String> = VALIDATOR) : AbstractNumber(), Fachwert {

    private val code: ByteArray

    constructor(zahl: Int): this(zahl.toLong()) {}

    companion object {

        private val LOG = Logger.getLogger(PackedDecimal::class.java.name)
        private val VALIDATOR: NullValidator<String> = NullValidator<String>()
        private val CACHE = arrayOfNulls<PackedDecimal>(10)
        private val WEAK_CACHE = WeakHashMap<String, PackedDecimal>()

        init {
            for (i in CACHE.indices) {
                CACHE[i] = PackedDecimal(i)
            }
        }

        /** Null-Konstante fuer Initialisierungen.  */
        val NULL = PackedDecimal("")

        /** Leere PackedDecimal.  */
        @JvmField
        val EMPTY = PackedDecimal("")

        /** Die Zahl 0.  */
        @JvmField
        val ZERO = CACHE[0]

        /** Die Zahl 1.  */
        @JvmField
        val ONE = CACHE[1]

        /** Die Zahl 10.  */
        @JvmField
        val TEN = of(10)

        /**
         * Liefert den uebergebenen String als [PackedDecimal] zurueck.
         * Diese Methode ist dem Konstruktor vorzuziehen, da fuer gaengige Zahlen
         * wie "0" oder "1" immer das gleiche Objekt zurueckgegeben wird.
         *
         * @param zahl beliebige long-Zahl
         * @return Zahl als [PackedDecimal]
         */
        @JvmStatic
        fun valueOf(zahl: Long): PackedDecimal {
            return valueOf(zahl.toString())
        }

        /**
         * Da alle anderen Klassen auch eine of-Methode vorweisen, hat auch diese
         * Klasse eine of-Methode. Ansonsten entspricht dies der valueOf-Methode.
         *
         * @param zahl beliebige long-Zahl
         * @return Zahl als [PackedDecimal]
         * @since 2.0
         */
        @JvmStatic
        fun of(zahl: Long): PackedDecimal {
            return valueOf(zahl)
        }

        /**
         * Liefert den uebergebenen String als [PackedDecimal] zurueck.
         *
         * @param zahl beliebige Zahl
         * @return Zahl als [PackedDecimal]
         */
        @JvmStatic
        fun valueOf(zahl: Double): PackedDecimal {
            return valueOf(java.lang.Double.toString(zahl))
        }

        /**
         * Da alle anderen Klassen auch eine of-Methode vorweisen, hat auch diese
         * Klasse eine of-Methode. Ansonsten entspricht dies der valueOf-Methode.
         *
         * @param zahl beliebige Zahl
         * @return Zahl als [PackedDecimal]
         * @since 2.0
         */
        @JvmStatic
        fun of(zahl: Double): PackedDecimal {
            return valueOf(zahl)
        }

        /**
         * Liefert den uebergebenen String als [PackedDecimal] zurueck.
         * Diese Methode ist dem Konstruktor vorzuziehen, da fuer gaengige Zahlen
         * wie "0" oder "1" immer das gleiche Objekt zurueckgegeben wird.
         *
         * @param zahl beliebige Zahl
         * @return Zahl als [PackedDecimal]
         */
        @JvmStatic
        fun valueOf(zahl: BigDecimal): PackedDecimal {
            return valueOf(zahl.toString())
        }

        /**
         * Da alle anderen Klassen auch eine of-Methode vorweisen, hat auch diese
         * Klasse eine of-Methode. Ansonsten entspricht dies der valueOf-Methode.
         *
         * @param zahl beliebige Zahl
         * @return Zahl als [PackedDecimal]
         * @since 2.0
         */
        @JvmStatic
        fun of(zahl: BigDecimal): PackedDecimal {
            return valueOf(zahl)
        }

        /**
         * Liefert den uebergebenen String als [PackedDecimal] zurueck.
         *
         * @param bruch beliebiger Bruch
         * @return Bruch als [PackedDecimal]
         */
        @JvmStatic
        fun valueOf(bruch: AbstractNumber): PackedDecimal {
            return valueOf(bruch.toString())
        }

        /**
         * Da alle anderen Klassen auch eine of-Methode vorweisen, hat auch diese
         * Klasse eine of-Methode. Ansonsten entspricht dies der valueOf-Methode.
         *
         * @param zahl beliebige Zahl
         * @return Zahl als [PackedDecimal]
         * @since 2.0
         */
        @JvmStatic
        fun of(zahl: AbstractNumber): PackedDecimal {
            return valueOf(zahl)
        }

        /**
         * Liefert den uebergebenen String als [PackedDecimal] zurueck.
         * Diese Methode ist dem Konstruktor vorzuziehen, da fuer gaengige Zahlen
         * wie "0" oder "1" immer das gleiche Objekt zurueckgegeben wird.
         *
         *
         * Im Gegensatz zum String-Konstruktor darf man hier auch 'null' als Wert
         * uebergeben. In diesem Fall wird dies in [.EMPTY] uebersetzt.
         *
         *
         *
         * Die erzeugten PackedDecimals werden intern in einem "weak" Cache
         * abgelegt, damit bei gleichen Zahlen auch die gleichen PackedDecimals
         * zurueckgegeben werden. Dies dient vor allem zur Reduktion des
         * Speicherverbrauchs.
         *
         *
         * @param zahl String aus Zahlen
         * @return Zahl als [PackedDecimal]
         */
        @JvmStatic
        fun valueOf(zahl: String): PackedDecimal {
            val trimmed = StringUtils.trimToEmpty(zahl)
            if (StringUtils.isEmpty(trimmed)) {
                return EMPTY
            }
            return if (trimmed.length == 1 && Character.isDigit(trimmed[0])) {
                CACHE[Character.getNumericValue(trimmed[0])]!!
            } else {
                WEAK_CACHE.computeIfAbsent(zahl) { z: String -> PackedDecimal(z) }
            }
        }

        /**
         * Da alle anderen Klassen auch eine of-Methode vorweisen, hat auch diese
         * Klasse eine of-Methode. Ansonsten entspricht dies der valueOf-Methode.
         *
         * @param zahl beliebige Zahl
         * @return Zahl als [PackedDecimal]
         * @since 2.0
         */
        @JvmStatic
        fun of(zahl: String): PackedDecimal {
            return valueOf(zahl)
        }

        private fun asNibbles(zahl: String): ByteArray {
            val chars = "$zahl ".toCharArray()
            val bytes = ByteArray(chars.size / 2)
            try {
                for (i in bytes.indices) {
                    val upper = decode(chars[i * 2])
                    val lower = decode(chars[i * 2 + 1])
                    bytes[i] = (upper shl 4 or lower).toByte()
                }
            } catch (ex: IllegalArgumentException) {
                throw LocalizedIllegalArgumentException(zahl, "number", ex)
            }
            return bytes
        }

        private fun decode(x: Char): Int {
            return when (x) {
                '0' -> 0x0
                '1' -> 0x1
                '2' -> 0x2
                '3' -> 0x3
                '4' -> 0x4
                '5' -> 0x5
                '6' -> 0x6
                '7' -> 0x7
                '8' -> 0x8
                '9' -> 0x9
                '/' -> 0xA
                '\t', ' ' -> 0xB
                '+' -> 0xC
                '-' -> 0xD
                '.' -> 0xE
                ',' -> 0xF
                else -> throw LocalizedIllegalArgumentException(x, "number")
            }
        }

        private fun encode(nibble: Int): Char {
            return when (0x0F and nibble) {
                0x0 -> '0'
                0x1 -> '1'
                0x2 -> '2'
                0x3 -> '3'
                0x4 -> '4'
                0x5 -> '5'
                0x6 -> '6'
                0x7 -> '7'
                0x8 -> '8'
                0x9 -> '9'
                0xA -> '/'
                0xB -> ' '
                0xC -> '+'
                0xD -> '-'
                0xE -> '.'
                0xF -> ','
                else -> throw IllegalStateException("internal error")
            }
        }

    }

    /**
     * Instanziiert ein PackedDecimal.
     *
     * @param zahl Zahl
     */
    constructor(zahl: Long) : this(java.lang.Long.toString(zahl)) {}

    /**
     * Instanziiert ein PackedDecimal.
     *
     * @param zahl Zahl
     */
    constructor(zahl: Double) : this(java.lang.Double.toString(zahl)) {}

    /**
     * Falls man eine [BigDecimal] in eine [PackedDecimal] wandeln
     * will, kann man diesen Konstruktor hier verwenden. Besser ist es
     * allerdings, wenn man dazu [.valueOf] verwendet.
     *
     * @param zahl eine Dezimalzahl
     */
    constructor(zahl: BigDecimal) : this(zahl.toString()) {}

    /**
     * Liefert true zurueck, wenn die Zahl als Bruch angegeben ist.
     *
     * @return true oder false
     */
    val isBruch: Boolean
        get() {
            val s = toString()
            return if (s.contains("/")) {
                try {
                    Bruch.of(s)
                    true
                } catch (ex: IllegalArgumentException) {
                    LOG.fine("$s is not a fraction: $ex")
                    false
                }
            } else {
                false
            }
        }

    /**
     * Da sich mit [PackedDecimal] auch Telefonnummer und andere
     * Zahlenkombinationen abspeichern lassen, die im eigentlichen Sinn
     * keine Zahl darstellen, kann man ueber diese Methode abfragen, ob
     * eine Zahl abespeichdert wurde oder nicht.
     *
     * @return true, falls es sich um eine Zahl handelt.
     */
    val isNumber: Boolean
        get() {
            val packed = toString().replace(" ".toRegex(), "")
            return try {
                BigDecimal(packed)
                true
            } catch (nfe: NumberFormatException) {
                LOG.fine("$packed is not a number: $nfe")
                isBruch
            }
        }

    /**
     * Liefert die Zahl als Bruch zurueck.
     *
     * @return Bruch als Zahl
     */
    fun toBruch(): Bruch {
        return Bruch.of(toString())
    }

    /**
     * Liefert die gepackte Dezimalzahl wieder als [BigDecimal] zurueck.
     *
     * @return gepackte Dezimalzahl als [BigDecimal]
     */
    override fun toBigDecimal(): BigDecimal {
        return BigDecimal(toString())
    }

    override fun toByte(): Byte {
        return toBigDecimal().toByte()
    }

    override fun toChar(): Char {
        return toBigDecimal().toChar()
    }

    override fun toShort(): Short {
        return toBigDecimal().toShort()
    }

    /**
     * Summiert den uebergebenen Summanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param summand Summand
     * @return Summe
     */
    fun add(summand: PackedDecimal): PackedDecimal {
        return if (isBruch || summand.isBruch) {
            add(summand.toBruch())
        } else {
            add(summand.toBigDecimal())
        }
    }

    /**
     * Summiert den uebergebenen Summanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param summand Operand
     * @return Differenz
     */
    fun add(summand: BigDecimal): PackedDecimal {
        val summe = toBigDecimal().add(summand)
        return valueOf(summe)
    }

    /**
     * Summiert den uebergebenen Summanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param summand Operand
     * @return Differenz
     */
    fun add(summand: Bruch): PackedDecimal {
        val summe = toBruch().add(summand)
        return valueOf(summe)
    }

    /**
     * Subtrahiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Summand
     * @return Summe
     */
    fun subtract(operand: PackedDecimal): PackedDecimal {
        return if (isBruch || operand.isBruch) {
            subtract(operand.toBruch())
        } else {
            subtract(operand.toBigDecimal())
        }
    }

    /**
     * Subtrahiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Operand
     * @return Differenz
     */
    fun subtract(operand: BigDecimal): PackedDecimal {
        val result = toBigDecimal().subtract(operand)
        return valueOf(result)
    }

    /**
     * Subtrahiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Operand
     * @return Differenz
     */
    fun subtract(operand: Bruch): PackedDecimal {
        val result = toBruch().subtract(operand)
        return valueOf(result)
    }

    /**
     * Mulitpliziert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Summand
     * @return Produkt
     */
    fun multiply(operand: PackedDecimal): PackedDecimal {
        return if (isBruch || operand.isBruch) {
            multiply(operand.toBruch())
        } else {
            multiply(operand.toBigDecimal())
        }
    }

    /**
     * Multipliziert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Operand
     * @return Produkt
     */
    fun multiply(operand: BigDecimal): PackedDecimal {
        val produkt = toBigDecimal().multiply(operand)
        return valueOf(produkt)
    }

    /**
     * Multipliziert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Operand
     * @return Produkt
     */
    fun multiply(operand: Bruch): PackedDecimal {
        val produkt = toBruch().multiply(operand)
        return valueOf(produkt)
    }

    /**
     * Dividiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Operand
     * @return Ergebnis der Division
     */
    fun divide(operand: PackedDecimal): PackedDecimal {
        return if (isBruch || operand.isBruch) {
            divide(operand.toBruch())
        } else {
            divide(operand.toBigDecimal())
        }
    }

    /**
     * Dividiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Operand
     * @return Ergebnis der Division
     */
    fun divide(operand: Bruch): PackedDecimal {
        return multiply(operand.kehrwert())
    }

    /**
     * Dividiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * [PackedDecimal] zurueck
     *
     * @param operand Operand
     * @return Ergebnis der Division
     */
    fun divide(operand: BigDecimal): PackedDecimal {
        val result = toBigDecimal().divide(operand, RoundingMode.HALF_UP)
        return valueOf(result)
    }

    /**
     * Verschiebt den Dezimalpunkt um n Stellen nach links.
     *
     * @param n Anzahl Stellen
     * @return eine neue [PackedDecimal]
     */
    fun movePointLeft(n: Int): PackedDecimal {
        val result = toBigDecimal().movePointLeft(n)
        return valueOf(result)
    }

    /**
     * Verschiebt den Dezimalpunkt um n Stellen nach rechts.
     *
     * @param n Anzahl Stellen
     * @return eine neue [PackedDecimal]
     */
    fun movePointRight(n: Int): PackedDecimal {
        val result = toBigDecimal().movePointRight(n)
        return valueOf(result)
    }

    /**
     * Setzt die Anzahl der Nachkommastellen.
     *
     * @param n z.B. 0, falls keine Nachkommastelle gesetzt sein soll
     * @param mode Rundungs-Mode
     * @return eine neue [PackedDecimal]
     */
    fun setScale(n: Int, mode: RoundingMode): PackedDecimal {
        val result = toBigDecimal().setScale(n, mode)
        return valueOf(result)
    }

    override fun toString(): String {
        val buf = StringBuilder()
        for (b in code) {
            buf.append(encode(b.toInt() shr 4))
            buf.append(encode(b.toInt() and 0x0F))
        }
        return buf.toString().trim { it <= ' ' }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    override fun hashCode(): Int {
        return this.toString().hashCode()
    }

    /**
     * Beim Vergleich zweier PackedDecimals werden auch fuehrende Nullen
     * beruecksichtigt. D.h. '711' und '0711' werden als unterschiedlich
     * betrachtet.
     *
     * @param other zu vergleichende PackedDedimal
     * @return true bei Gleichheit
     * @see Object.equals
     */
    override fun equals(other: Any?): Boolean {
        return other is PackedDecimal && this.toString() == other.toString()
    }

    /**
     * Vergleicht die andere Zahl mit der aktuellen Zahl.
     *
     * @param other die andere Zahl, die verglichen wird
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     */
    override fun compareTo(other: AbstractNumber): Int {
        return if (other is PackedDecimal) {
            compareTo(other)
        } else {
            super.compareTo(other)
        }
    }

    /**
     * Vergleicht die andere Zahl mit der aktuellen Zahl.
     *
     * @param other die andere [PackedDecimal], die verglichen wird.
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     */
    operator fun compareTo(@NotNull other: PackedDecimal): Int {
        return toBruch().compareTo(other.toBruch())
    }

    init {
        code = asNibbles(validator.validate(zahl))
    }

}