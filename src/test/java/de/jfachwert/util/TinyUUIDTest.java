package de.jfachwert.util;/*
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

import org.junit.Test;

import java.math.BigInteger;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link TinyUUID}-Klasse.
 *
 * @author oboehm
 */
public final class TinyUUIDTest {

    /**
     * Test-Methode fuer {@link TinyUUID#toNumber()}.
     */
    @Test
    public void toNumberTen() {
        TinyUUID id = new TinyUUID(new UUID(0, 10));
        assertEquals(BigInteger.TEN, id.toNumber());
    }

    /**
     * Test-Methode fuer {@link TinyUUID#toNumber()}.
     */
    @Test
    public void toNumberBig() {
        BigInteger big = new BigInteger("98765432100000000000");
        TinyUUID id = new TinyUUID(big);
        assertEquals(big, id.toNumber());
    }

    /**
     * Test-Methode fuer {@link TinyUUID#getLeastSignificantBits()} und
     * {@link TinyUUID#getMostSignificantBits()}
.     */
    @Test
    public void testGetSignificantBits() {
        long lower = 42;
        long upper = 21;
        TinyUUID id = new TinyUUID(lower, upper);
        assertEquals(lower, id.getLeastSignificantBits());
        assertEquals(upper, id.getMostSignificantBits());
    }

}
