/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 23.04.2019 by oboehm (ob@oasd.de)
 */
package de.jfachwert.util;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Im Gegensatz zur {@link TinyUUID} verwendet die SmallUUID nur Zahlen und
 * Kleinbuchstaben. Damit kann diese Klasse auch zur Bildung von Dateinamen
 * herangezogen werden.
 *
 * @author oboehm
 * @since 2.3 (23.04.2019)
 */
public class SmallUUID extends TinyUUID {

    /** Minimale UUID. */
    public static final SmallUUID MIN = new SmallUUID("00000000-0000-0000-0000-000000000000");

    /** Maximale UUID (die aber als Nummer negativ ist). */
    public static final SmallUUID MAX = new SmallUUID("ffffffff-ffff-ffff-ffff-ffffffffffff");

    /**
     * Instantiiert eine neue SmallUUID.
     *
     * @param uuid gueltige UUID
     */
    public SmallUUID(UUID uuid) {
        super(uuid);
    }

    /**
     * Instantiiert eine eine neue SmallUUID anhand eines Strings. Dieser kann
     * sowohl in Form einer UUID ("4e8108fa-e517-41bd-8372-a828843030ba") als
     * auch in Form ohne Trennzeichen ("4e8108fae51741bd8372a828843030ba")
     * angegeben werden.
     *
     * @param uuid z.B. "4e8108fa-e517-41bd-8372-a828843030ba"
     */
    public SmallUUID(String uuid) {
        super(uuid);
    }

    /**
     * Instantiiert eine neue SmallUUID. Die uebergebene Zahl wird dabei auf
     * 128 Bit normalisiert, damit es beim Vergleich keine Ueberraschungen
     * wegen unterschiedlichem Vorzeichen gibt.
     *
     * @param number 128-Bit-Zahl
     */
    public SmallUUID(BigInteger number) {
        super(number);
    }

    /**
     * Instantiiert eine neue SmallUUID.
     *
     * @param bytes 16 Bytes
     */
    public SmallUUID(byte[] bytes) {
        super(bytes);
    }

    /**
     * Dies ist das Gegenstueck zur {@link UUID#randomUUID()}, nur dass hier
     * bereits eine {@link SmallUUID} erzeugt wird.
     *
     * @return zufaellige UUID
     */
    public static SmallUUID randomUUID() {
        return new SmallUUID(UUID.randomUUID());
    }

    /**
     * Liefert eine verkuerzte Darstellung einer UUID als String. Die Laenge
     * reduziert sich dadurch auf 25 Zeichen. Diese kann z.B. dazu genutzt
     * werden, um eine UUID als Dateinamen zu verwenden.
     *
     * @return 25 Zeichen, z.B. "12srde28kvwih41tdis7vz5sx"
     */
    @Override
    public String toShortString() {
        BigInteger id = toNumber();
        String s = "000000000000000000000000" + id.toString(Character.MAX_RADIX);
        return s.substring(s.length() - 25);
    }

}
