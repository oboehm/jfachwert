/*
 * Copyright (c) 2017 by Oliver Boehm
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
package de.jfachwert.util;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.pruefung.exception.InvalidLengthException;
import de.jfachwert.pruefung.exception.InvalidValueException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * Die Klasse TinyUUID ist ein einfacher Wrapper um UUID mit dem Ziel, eine
 * kuerzere Praesentation als das Original zur Verfuegung zu stellen. Die
 * Original-UUID hat eine Laenge von 35 Zeichen, belegt aber intern nur
 * 128 Bits oder 16 Bytes. Damit laeest sich der Speicheraufwand um ueber 50%
 * reduzieren.
 *
 * <p>
 * Die Klasse implementiert die wichtigsten Methoden und Konstruktoren
 * der {@link UUID}-Klasse, sodass sie als Ersatz fuer diese Klasse verwendet
 * werden kann.
 * </p>
 *
 * @author oboehm
 * @since 0.6+ (11.12.2017)
 */
public class TinyUUID extends AbstractFachwert<UUID> {

    /** Minimale UUID. */
    public static final TinyUUID MIN = new TinyUUID("00000000-0000-0000-0000-000000000000");

    /** Maximale UUID (die aber als Nummer negativ ist). */
    public static final TinyUUID MAX = new TinyUUID("ffffffff-ffff-ffff-ffff-ffffffffffff");

    /**
     * Instantiiert eine neue TinyUUID.
     *
     * @param uuid gueltige UUID
     */
    public TinyUUID(UUID uuid) {
        super(uuid);
    }

    /**
     * Instantiiert eine eine neue TinyUUID anhand eines Strings. Dieser kann
     * sowohl in Form einer UUID ("4e8108fa-e517-41bd-8372-a828843030ba") als
     * auch in Form ohne Trennzeichen ("4e8108fae51741bd8372a828843030ba")
     * angegeben werden.
     *
     * @param uuid z.B. "4e8108fa-e517-41bd-8372-a828843030ba"
     */
    public TinyUUID(String uuid) {
        this(toUUID(uuid));
    }

    /**
     * Instantiiert eine neue TinyUUID. Die uebergebene Zahl wird dabei auf
     * 128 Bit normalisiert, damit es beim Vergleich keine Ueberraschungen
     * wegen unterschiedlichem Vorzeichen gibt.
     *
     * @param number 128-Bit-Zahl
     */
    public TinyUUID(BigInteger number) {
        this(UUID.fromString(toString(number)));
    }

    /**
     * Instantiiert eine neue TinyUUID.
     *
     * @param bytes 16 Bytes
     */
    public TinyUUID(byte[] bytes) {
        this(toUUID(bytes));
    }

    private static UUID toUUID(byte[] bytes) {
        return UUID.fromString(toString(validate(bytes)));
    }

    private static byte[] validate(byte[] bytes) {
        if (bytes.length != 16) {
            throw new InvalidLengthException(bytes, 16);
        }
        return bytes;
    }

    private static byte[] to16Bytes(BigInteger number) {
        return to16Bytes(number.toByteArray());
    }

    private static byte[] to16Bytes(byte[] bytes) {
        if (bytes.length > 15) {
            return Arrays.copyOfRange(bytes, bytes.length - 16, bytes.length);
        } else {
            byte[] bytes16 = new byte[16];
            System.arraycopy(bytes, 0, bytes16, 16 - bytes.length, bytes.length);
            return bytes16;
        }
    }

    /**
     * Liefert die UUID als 128-Bit-Zahl zurueck. Diese kann auch negativ
     * sein.
     *
     * @return Zahl
     */
    public BigInteger toNumber() {
        UUID uuid = this.getCode();
        return toBigInteger(uuid.toString());
    }

    private static BigInteger toBigInteger(String uuid) {
        try {
            return new BigInteger(uuid.replaceAll("-", ""), 16);
        } catch (NumberFormatException nfe) {
            throw new InvalidValueException(uuid, "UUID");
        }
    }

    /**
     * Liefert die 128-Bit-Zahl als Byte-Array zurueck.
     *
     * @return 16-stelliges Byte-Array
     */
    public byte[] toBytes() {
        byte[] bytes = this.toNumber().toByteArray();
        return to16Bytes(bytes);
    }

    /**
     * Liefert die {@link UUID} zurueck. Darueber kann man auf die weniger
     * wichtigen Methoden von {@link UUID} zurueckgreifen, die in dieser Klasse
     * fehlen.
     *
     * @return die {@link UUID}
     */
    public UUID getUUID() {
        return this.getCode();
    }

    /**
     * Liefert die unteren 64 Bits der 128-bittigen UUID.
     *
     * @return die ersten 64 Bits
     */
    public long getLeastSignificantBits() {
        return this.getUUID().getLeastSignificantBits();
    }

    /**
     * Liefert die oberen 64 Bits der 128-bittigen UUID.
     *
     * @return die oberen 64 Bits
     */
    public long getMostSignificantBits() {
        return this.getUUID().getMostSignificantBits();
    }

    /**
     * {@link TinyUUID} ist zwar als Ersatz fuer die {@link UUID}-Klasse gedacht,
     * liefert aber immer die Kurzform ueber die toString()-Methode zurueck.
     * D.h. das Ergebis entspricht der Ausgabe von {@link #toShortString()}.
     *
     * @return z.B. "ix9de14vQgGKwXZUaruCzw"
     */
    @Override
    public String toString() {
        return this.toShortString();
    }

    private static String toString(BigInteger number) {
        byte[] bytes = to16Bytes(number);
        return toString(bytes);
    }

    private static String toString(byte[] bytes) {
        return String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
                bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7],
                bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15]);
    }

    /**
     * Liefert eine verkuerzte Darstellung einer UUID als String. Die Laenge
     * reduziert sich dadurch auf 22 Zeichen. Diese kann z.B. dazu genutzt
     * werden, um eine UUID platzsparend abzuspeichern, wenn man dazu nicht
     * das Ergebnis aus {@link #toBytes()} (16 Bytes) verwenden will.
     * <p>
     * Damit der resultierende String auch URL-safe ist, werden die Zeichen
     * '/' und '+' durch '_' und '-' ersetzt.
     * </p>
     *
     * @return 22 Zeichen, z.B. "ix9de14vQgGKwXZUaruCzw"
     */
    public String toShortString() {
        String s = Base64.getEncoder().withoutPadding().encodeToString(toBytes());
        return s.replace('/', '_').replace('+', '-');
    }

    /**
     * Dies ist das Gegenstueck zu {@link #toShortString()}.
     *
     * @return z.B. "4e8108fa-e517-41bd-8372-a828843030ba"
     */
    public String toLongString() {
        return this.getUUID().toString();
    }

    /**
     * Aehnlich wie {@link UUID#fromString(String)} wird hier eine
     * {@link TinyUUID} anhand des uebergebenen Strings erzeugt.
     * Der uebergebene String kann dabei das Format einer UUID
     * besitzen, kann aber auch ein Ergebnis von {@link #toShortString()}
     * sein.
     *
     * @param id z.B. "ix9de14vQgGKwXZUaruCzw"
     * @return a TinyUUID
     */
    public static TinyUUID fromString(String id) {
        return new TinyUUID(toUUID(id));
    }
    
    private static UUID toUUID(String id) {
        switch (id.length()) {
            case 22:
                String base64 = id.replace('-', '+').replace('_', '/');
                byte [] bytes = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
                return toUUID(bytes);
            default:
                try {
                    return UUID.fromString(id);
                } catch (IllegalArgumentException ex) {
                    throw new InvalidValueException(id, "UUID");
                }
        }
    }

    /**
     * Dies ist das Gegenstueck zur {@link UUID#randomUUID()}, nur dass hier
     * bereits eine {@link TinyUUID} erzeugt wird.
     *
     * @return zufaellige UUID
     */
    public static TinyUUID randomUUID() {
        return new TinyUUID(UUID.randomUUID());
    }

}
