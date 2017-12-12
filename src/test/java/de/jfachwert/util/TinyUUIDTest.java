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

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Fachwert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link TinyUUID}-Klasse.
 *
 * @author oboehm
 */
public final class TinyUUIDTest extends AbstractFachwertTest {

    /**
     * Erzeugt eine Test-UUID zum Testen.
     *
     * @return immer die gleiche {@link TinyUUID}
     */
    @Override
    protected Fachwert createFachwert() {
        return new TinyUUID(0x12345678, 0x9abcdef0);
    }

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
        BigInteger big = new BigInteger("ffffffffeeeeeeeeddddddddcccccccc", 16);
        TinyUUID id = new TinyUUID(big);
        assertEquals(big, id.toNumber());
    }

    /**
     * Test-Methode fuer {@link TinyUUID#toBytes()}.
     */
    @Test
    public void testToBytes() {
        TinyUUID ten = new TinyUUID(BigInteger.TEN);
        byte[] bytes = ten.toBytes();
        assertEquals(16, bytes.length);
        assertEquals(10, bytes[15]);
        assertEquals(ten, new TinyUUID(bytes));
    }

    /**
     * Test-Methode fuer {@link TinyUUID#getUUID()}.
     */
    @Test
    public void testGetRandomUUID() {
        UUID uuid = UUID.randomUUID();
        checkGetUUID(uuid);
    }

    /**
     * Test-Methode fuer {@link TinyUUID#getUUID()}.
     */
    @Test
    public void testGetUUID() {
        UUID uuid = UUID.fromString("4e8108fa-e517-41bd-8372-a828843030ba");
        assertEquals(uuid, UUID.fromString("4e8108fa-e517-41bd-8372-a828843030ba"));
        checkGetUUID(uuid);
    }

    private void checkGetUUID(UUID uuid) {
        TinyUUID tiny = new TinyUUID(uuid);
        assertEquals(uuid, tiny.getUUID());
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

    /**
     * Da {@link TinyUUID} als Ersatz fuer die {@link UUID}-Klasse gedacht ist,
     * sollte sie bei der toString()-Methode das gleiche Ergebnis rauskommen.
     */
    @Override
    @Test
    public void testToString() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid.toString(), new TinyUUID(uuid).toString());
    }

}
