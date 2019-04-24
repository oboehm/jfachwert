package de.jfachwert.util;/*
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit-Tests fuer {@link SmallUUID}-Klasse.
 *
 * @author oboehm
 * @since x.x (23.04.2019)
 */
public class SmallUUIDTest extends TinyUUIDTest {

    /**
     * Erzeugt eine TinyUUID zum Testen.
     *
     * @param uuid UUID
     * @return eine TinyUUID
     */
    @Override
    protected SmallUUID createTinyUUID(String uuid) {
        return new SmallUUID(uuid);
    }

    @Test
    public void testCharacters() {
        SmallUUID uuid = SmallUUID.randomUUID();
        String s = uuid.toString();
        for (char c : s.toCharArray()) {
            assertTrue("invalid char in '" + s + "': " + c, Character.isLetterOrDigit(c));
        }
    }

    /**
     * Test-Methode fuer {@link SmallUUID#toShortString()}.
     */
    @Test
    public void testToShortString2() {
        SmallUUID uuid = SmallUUID.randomUUID();
        checkToShortString(uuid);
    }

    /**
     * Test-Methode fuer {@link SmallUUID#toShortString()}.
     */
    @Test
    public void testToShortStringMin() {
        assertEquals("0000000000000000000000000", SmallUUID.MIN.toString());
    }

    /**
     * Test-Methode fuer {@link SmallUUID#toShortString()}.
     */
    @Test
    public void testToShortStringMax() {
        checkToShortString(SmallUUID.MAX);
    }

    @Override
    protected void checkToShortString(TinyUUID id) {
        checkToShortString(id, 25);
    }

}
