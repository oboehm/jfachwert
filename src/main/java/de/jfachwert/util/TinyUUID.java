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

import java.math.BigInteger;
import java.util.UUID;

/**
 * Die Klasse TinyUUID ist ein einfacher Wrapper um UUID mit dem Ziel, eine
 * kuerzere Praesentation als das Original zur Verfuegung zu stellen. Die
 * Original-UUID hat eine Laenge von 35 Zeichen, belegt aber intern nur
 * 128 Bits oder 16 Bytes. Damit laeest sich der Speicheraufwand um ueber 50%
 * reduzieren.
 *
 * <p>
 * Die Klasse implementiert besitzt die wichtigsten Methoden und Konstruktoren
 * der {@link UUID}-Klasse, sodass sie als Ersatz fuer diese Klasse verwendet
 * werden kann.
 * </p>
 *
 * @author oboehm
 * @since 0.6+ (11.12.2017)
 */
public class TinyUUID extends AbstractFachwert<BigInteger> {

    private static final BigInteger LIMIT_INT = BigInteger.valueOf(0x100000000L);
    private static final BigInteger LIMIT_LONG = LIMIT_INT.multiply(LIMIT_INT);

    /**
     * Instantiiert eine neue TinyUUID.
     *
     * @param uuid gueltige UUID
     */
    public TinyUUID(UUID uuid) {
        this(new BigInteger(uuid.toString().replaceAll("-", ""), 16));
    }

    /**
     * Instantiiert eine neue TinyUUID.
     *
     * @param number 128-Bit-Zahl
     */
    public TinyUUID(BigInteger number) {
        super(number);
    }

    /**
     *Instantiiert eine neue TinyUUID.
     *
     * @param lower die unteren 64 Bits
     * @param upper die oberen 64 Bits
     */
    public TinyUUID(long lower, long upper) {
        this(BigInteger.valueOf(upper).multiply(LIMIT_LONG).add(BigInteger.valueOf(lower)));
    }

    /**
     * Liefert die UUID als 128-Bit-Zahl zurueck.
     *
     * @return Zahl
     */
    public BigInteger toNumber() {
        return this.getCode();
    }

    /**
     * Liefert die {@link UUID} zurueck. Darueber kann man auf die weniger
     * wichtigen Methoden von {@link UUID} zurueckgreifen, die in dieser Klasse
     * fehlen.
     *
     * @return die {@link UUID}
     */
    public UUID getUUID() {
        BigInteger number = this.getCode();
//        byte[] bytes = number.toByteArray();
//        String expanded = String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
//                bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7],
//                bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15]);
//        String s = String.format("%032x", number);// number.toString(16);
//        String expanded = s.substring(0, 7) + "-" + s.substring(7, 11) + "-" + s.substring(11, 15) + "-" + s.substring(15, 19) + "-" + s.substring(19);
//        return UUID.fromString(expanded);
        return new UUID(number.divide(LIMIT_LONG).longValue(), number.mod(LIMIT_LONG).longValue());
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
     * Da {@link TinyUUID} als Ersatz fuer die {@link UUID}-Klasse gedacht ist,
     * liefert sie das gleiche Ergebnis wie die {@link UUID}-Klasse.
     *
     * @return z.B. "d9b67549-3c53-42f2-a394-33f990e697ce"
     */
    @Override
    public String toString() {
        return this.getUUID().toString();
    }

}
