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
 * @author oboehm
 * @since 0.6+ (11.12.2017)
 */
public class TinyUUID extends AbstractFachwert<UUID> {

    public TinyUUID(UUID uuid) {
        super(uuid);
    }

    /**
     * Liefert die UUID als 128-Bit-Zahl zurueck.
     *
     * @return Zahl
     */
    public BigInteger toNumber() {
        BigInteger lowerPart = BigInteger.valueOf(this.getCode().getLeastSignificantBits());
        BigInteger upperPart = BigInteger.valueOf(this.getCode().getMostSignificantBits());
        BigInteger faktor = BigInteger.valueOf(0x100000000L);
        faktor = faktor.multiply(faktor);
        return upperPart.multiply(faktor).add(lowerPart);
    }

}
