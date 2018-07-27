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
import org.junit.Test;

import javax.validation.ValidationException;
import java.math.BigInteger;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Unit-Tests fuer {@link TinyUUID}-Klasse.
 *
 * @author oboehm
 */
public final class TinyUUIDTest extends AbstractFachwertTest {

    private final TinyUUID tinyUUID = TinyUUID.randomUUID();

    /**
     * Erzeugt eine Test-UUID zum Testen.
     *
     * @return immer die gleiche {@link TinyUUID}
     */
    @Override
    protected TinyUUID createFachwert() {
        return new TinyUUID("12345678-9abc-def0-0fed-cba987654321");
    }

    /**
     * Aus einem gegebenen TinyUUID-String sollte sich die TinyUUID wieder
     * rekonstruieren lassen.
     */
    @Test
    public void testTinyUUIDString() {
        String tiny = tinyUUID.toShortString();
        TinyUUID id = new TinyUUID(tiny);
        assertEquals(tinyUUID, id);
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
     * Test-Methode fuer {@link TinyUUID#TinyUUID(byte[])}. Wenn hier keine
     * 16 Bytes uebergeben werden, sollte eine {@link ValidationException}
     * kommen.
     */
    @Test(expected = ValidationException.class)
    public void testTinyUUIDInvalidBytes() {
        new TinyUUID(new byte[1]);
    }

    /**
     * Test-Methode fuer {@link TinyUUID#toNumber()}.
     */
    @Test
    public void toNumberBig() {
        BigInteger big = new BigInteger("7fffffffeeeeeeeeddddddddcccccccc", 16);
        TinyUUID id = new TinyUUID(big);
        assertEquals(big, id.toNumber());
        assertEquals(big.bitLength(), id.toNumber().bitLength());
    }

    /**
     * Test-Methode fuer {@link TinyUUID#toBytes()}.
     */
    @Test
    public void testToBytes() {
        TinyUUID ten = new TinyUUID(BigInteger.TEN);
        byte[] bytes = checkToBytes(ten);
        assertEquals(10, bytes[15]);
    }

    /**
     * Test-Methode fuer {@link TinyUUID#toBytes()}.
     */
    @Test
    public void testToBytesWithBigUUID() {
        TinyUUID big = new TinyUUID(new BigInteger("294001587467270389503940796937694345183"));
        checkToBytes(big);
    }

    private static byte[] checkToBytes(TinyUUID id) {
        byte[] bytes = id.toBytes();
        assertEquals(16, bytes.length);
        assertEquals(id, new TinyUUID(bytes));
        return bytes;
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

    /**
     * Mit dieser UUID aus {@link #testGetRandomUUID()} gab es Probleme.
     */
    @Test
    public void testNegativeUUID() {
        UUID uuid = UUID.fromString("ceb95a8d-ae7a-491a-ba6f-ee6ca45b46a8");
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
        byte[] bytes = new byte[16];
        bytes[15] = (byte) lower;
        bytes[7] = (byte) upper;
        TinyUUID id = new TinyUUID(bytes);
        assertEquals(lower, id.getLeastSignificantBits());
        assertEquals(upper, id.getMostSignificantBits());
    }

    /**
     * Da {@link TinyUUID} als Ersatz fuer die {@link UUID}-Klasse gedacht ist,
     * sollte sie bei der toLongString()-Methode das gleiche Ergebnis rauskommen.
     */
    @Test
    public void testToLongString() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid.toString(), new TinyUUID(uuid).toLongString());
    }

    /**
     * Test-Methode fuer {@link TinyUUID#toShortString()}. Der resultierende
     * String sollte auch kein "/" und "+" enthalten um URL-safe zu sein
     * (s. http://toddfredrich.com/ids-in-rest-api.html).
     */
    @Test
    public void testToShortString() {
        checkToShortString(tinyUUID);
    }

    /**
     * Test-Methode fuer {@link TinyUUID#toShortString()}.
     */
    @Test
    public void testToShortStringMin() {
        checkToShortString(TinyUUID.MIN);
    }

    /**
     * Test-Methode fuer {@link TinyUUID#toShortString()}.
     */
    @Test
    public void testToShortStringMax() {
        checkToShortString(TinyUUID.MAX);
    }

    private void checkToShortString(TinyUUID id) {
        String s = id.toShortString();
        assertThat(s.length(), is(lessThan(id.toLongString().length())));
        assertThat(s, s.length(), is(22));
        assertThat(s, not(containsString("/")));
        assertThat(s, not(containsString("+")));
        assertEquals(id, TinyUUID.fromString(s));
    }

    /**
     * Test-Methode fuer {@link TinyUUID#fromString(String)}.
     */
    @Test
    public void testFromString() {
        UUID uuid = UUID.randomUUID();
        assertEquals(new TinyUUID(uuid), TinyUUID.fromString(uuid.toString()));
    }

    /**
     * Weitere test-Methode fuer {@link TinyUUID#fromString(String)}.
     */
    @Test
    public void testFromStringEncoded() {
        String s = "01234_6789-abcd-fghi_w";
        TinyUUID tinyUUID = TinyUUID.fromString(s);
        assertEquals(s, tinyUUID.toShortString());
    }

    /**
     * Weitere test-Methode fuer {@link TinyUUID#fromString(String)} zum
     * Abklopfen der unteren Grenze.
     */
    @Test
    public void testFromStringMin() {
        checkFromString(TinyUUID.MIN);
    }

    /**
     * Weitere test-Methode fuer {@link TinyUUID#fromString(String)} zum
     * Abklopfen der oberen Grenze.
     */
    @Test
    public void testFromStringMax() {
        checkFromString(TinyUUID.MAX);
    }

    /**
     * Weitere test-Methode fuer {@link TinyUUID#fromString(String)}, in der
     * wir eine Reihe von Werten zur Ueberpruefung generieren.
     */
    @Test
    public void testFromStringFromMin() {
        checkLastByte("00000000-0000-0000-0000-0000000000");
    }

    /**
     * Weitere test-Methode fuer {@link TinyUUID#fromString(String)}, in der
     * wir eine Reihe von Werten zur Ueberpruefung generieren.
     */
    @Test
    public void testFromStringFromMax() {
        checkLastByte("ffffffff-ffff-ffff-ffff-ffffffffff");
    }

    private static void checkLastByte(String prefix) {
        for (int i = 1; i < 256; i++) {
            String s = String.format("%s%02x", prefix, i);
            checkFromString(TinyUUID.fromString(prefix));
        }
    }

    private static void checkFromString(TinyUUID tinyUUID) {
        assertEquals(tinyUUID, TinyUUID.fromString(tinyUUID.toLongString()));
        assertEquals(tinyUUID, TinyUUID.fromString(tinyUUID.toShortString()));
    }

    /**
     * Dieser Test stellt sicher, dass die Rueckwaerts-Kombatibilitaet zu
     * frueheren Versionen gewahrt bleibt und noch der gleiche Wert rauskommt
     * wie anfangs.
     */
    @Test
    public void testBackwardCompatibility() {
        TinyUUID expected = TinyUUID.fromString("00108310-0042-0fff-b3cb-1c3ff7cefafb");
        TinyUUID tinyUUID = TinyUUID.fromString("ABCDEABCD_-zyxw_9876-w");
        assertEquals(expected, tinyUUID);
    }

}
