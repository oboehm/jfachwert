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
 * (c)reated 11.12.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.util

import de.jfachwert.AbstractFachwert
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Die Klasse TinyUUID ist ein einfacher Wrapper um UUID mit dem Ziel, eine
 * kuerzere Praesentation als das Original zur Verfuegung zu stellen. Die
 * Original-UUID hat eine Laenge von 35 Zeichen, belegt aber intern nur
 * 128 Bits oder 16 Bytes. Damit laeest sich der Speicheraufwand um ueber 50%
 * reduzieren.
 *
 * Die Klasse implementiert die wichtigsten Methoden und Konstruktoren
 * der [UUID]-Klasse, sodass sie als Ersatz fuer diese Klasse verwendet
 * werden kann.
 *
 * @author oboehm
 * @since 0.6+ (11.12.2017)
 */
open class TinyUUID(uuid: UUID) : AbstractFachwert<UUID, TinyUUID>(uuid) {

    /**
     * Instantiiert eine eine neue TinyUUID anhand eines Strings. Dieser kann
     * sowohl in Form einer UUID ("4e8108fa-e517-41bd-8372-a828843030ba") als
     * auch in Form ohne Trennzeichen ("4e8108fae51741bd8372a828843030ba")
     * angegeben werden.
     *
     * @param uuid z.B. "4e8108fa-e517-41bd-8372-a828843030ba"
     */
    constructor(uuid: String) : this(toUUID(uuid)) {}

    /**
     * Instantiiert eine neue TinyUUID. Die uebergebene Zahl wird dabei auf
     * 128 Bit normalisiert, damit es beim Vergleich keine Ueberraschungen
     * wegen unterschiedlichem Vorzeichen gibt.
     *
     * @param number 128-Bit-Zahl
     */
    constructor(number: BigInteger) : this(toUUID(number)) {}

    /**
     * Instantiiert eine neue TinyUUID.
     *
     * @param bytes 16 Bytes
     */
    constructor(bytes: ByteArray) : this(toUUID(bytes)) {}

    /**
     * Liefert die UUID als 128-Bit-Zahl zurueck. Diese kann auch negativ
     * sein.
     *
     * @return Zahl
     */
    fun toNumber(): BigInteger {
        val uuid = code
        return toBigInteger(uuid.toString())
    }

    /**
     * Liefert die 128-Bit-Zahl als Byte-Array zurueck.
     *
     * @return 16-stelliges Byte-Array
     */
    fun toBytes(): ByteArray {
        val bytes = toNumber().toByteArray()
        return to16Bytes(bytes)
    }

    /**
     * Liefert die [UUID] zurueck. Darueber kann man auf die weniger
     * wichtigen Methoden von [UUID] zurueckgreifen, die in dieser Klasse
     * fehlen.
     *
     * @return die [UUID]
     */
    val uUID: UUID
        get() = code

    /**
     * Liefert die unteren 64 Bits der 128-bittigen UUID.
     *
     * @return die ersten 64 Bits
     */
    val leastSignificantBits: Long
        get() = uUID.leastSignificantBits

    /**
     * Liefert die oberen 64 Bits der 128-bittigen UUID.
     *
     * @return die oberen 64 Bits
     */
    val mostSignificantBits: Long
        get() = uUID.mostSignificantBits

    /**
     * [TinyUUID] ist zwar als Ersatz fuer die [UUID]-Klasse gedacht,
     * liefert aber immer die Kurzform ueber die toString()-Methode zurueck.
     * D.h. das Ergebis entspricht der Ausgabe von [.toShortString].
     *
     * @return z.B. "ix9de14vQgGKwXZUaruCzw"
     */
    override fun toString(): String {
        return toShortString()
    }

    /**
     * Liefert eine verkuerzte Darstellung einer UUID als String. Die Laenge
     * reduziert sich dadurch auf 22 Zeichen. Diese kann z.B. dazu genutzt
     * werden, um eine UUID platzsparend abzuspeichern, wenn man dazu nicht
     * das Ergebnis aus [.toBytes] (16 Bytes) verwenden will.
     *
     * Damit der resultierende String auch URL-safe ist, werden die Zeichen
     * '/' und '+' durch '_' und '-' ersetzt.
     *
     * @return 22 Zeichen, z.B. "ix9de14vQgGKwXZUaruCzw"
     */
    open fun toShortString(): String {
        val s = Base64.getEncoder().withoutPadding().encodeToString(toBytes())
        return s.replace('/', '_').replace('+', '-')
    }

    /**
     * Dies ist das Gegenstueck zu [.toShortString].
     *
     * @return z.B. "4e8108fa-e517-41bd-8372-a828843030ba"
     */
    fun toLongString(): String {
        return uUID.toString()
    }



    companion object {

        /** Minimale UUID.  */
        @JvmField
        val MIN = TinyUUID("00000000-0000-0000-0000-000000000000")

        /** Maximale UUID (die aber als Nummer negativ ist).  */
        @JvmField
        val MAX = TinyUUID("ffffffff-ffff-ffff-ffff-ffffffffffff")

        private fun to16Bytes(number: BigInteger): ByteArray {
            return to16Bytes(number.toByteArray())
        }

        private fun to16Bytes(bytes: ByteArray): ByteArray {
            return if (bytes.size > 15) {
                Arrays.copyOfRange(bytes, bytes.size - 16, bytes.size)
            } else {
                val bytes16 = ByteArray(16)
                System.arraycopy(bytes, 0, bytes16, 16 - bytes.size, bytes.size)
                bytes16
            }
        }

        private fun toBigInteger(uuid: String): BigInteger {
            return try {
                BigInteger(uuid.replace("-".toRegex(), ""), 16)
            } catch (nfe: NumberFormatException) {
                throw InvalidValueException(uuid, "UUID")
            }
        }

        private fun toString(number: BigInteger): String {
            val bytes = to16Bytes(number)
            return toString(bytes)
        }

        private fun toString(bytes: ByteArray): String {
            return String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
                    bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7],
                    bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15])
        }

        /**
         * Aehnlich wie [UUID.fromString] wird hier eine
         * [TinyUUID] anhand des uebergebenen Strings erzeugt.
         * Der uebergebene String kann dabei das Format einer UUID
         * besitzen, kann aber auch ein Ergebnis von [.toShortString]
         * sein.
         *
         * @param id z.B. "ix9de14vQgGKwXZUaruCzw"
         * @return a TinyUUID
         */
        @JvmStatic
        fun fromString(id: String): TinyUUID {
            return TinyUUID(toUUID(id))
        }

        private fun toUUID(id: String): UUID {
            return when (id.length) {
                22 -> {
                    val base64 = id.replace('-', '+').replace('_', '/')
                    val bytes = Base64.getDecoder().decode(base64.toByteArray(StandardCharsets.UTF_8))
                    toUUID(bytes)
                }
                25 -> {
                    val n = BigInteger(id, Character.MAX_RADIX)
                    toUUID(n)
                }
                else -> try {
                    UUID.fromString(id)
                } catch (ex: IllegalArgumentException) {
                    throw InvalidValueException(id, "UUID")
                }
            }
        }

        private fun toUUID(number: BigInteger): UUID {
            return UUID.fromString(toString(number))
        }

        private fun toUUID(bytes: ByteArray): UUID {
            return UUID.fromString(toString(verify(bytes)))
        }

        private fun verify(bytes: ByteArray): ByteArray {
            if (bytes.size != 16) {
                throw LocalizedIllegalArgumentException(bytes, 16)
            }
            return bytes
        }

        /**
         * Dies ist das Gegenstueck zur [UUID.randomUUID], nur dass hier
         * bereits eine [TinyUUID] erzeugt wird.
         *
         * @return zufaellige UUID
         */
        @JvmStatic
        fun randomUUID(): TinyUUID {
            return TinyUUID(UUID.randomUUID())
        }

    }

}