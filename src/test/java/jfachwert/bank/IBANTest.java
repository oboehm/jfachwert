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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 10.03.2017 by oboehm (ob@jfachwert.de)
 */
package jfachwert.bank;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Test fuer {@link IBAN}-Klasse.
 *
 * @author oboehm
 */
public final class IBANTest {

    private final IBAN iban = new IBAN("DE41300606010006605605");

    /**
     * Test method for {@link IBAN#getFormatted()}.
     */
    @Test
    public void testGetFormatted() {
        assertEquals("DE41 3006 0601 0006 6056 05", iban.getFormatted());
    }

    /**
     * Test method for {@link IBAN#getFormatted()}.
     */
    @Test
    public void testGetFormattedFormatted() {
        assertEquals("DE41 3006 0601 0006 6056 05", new IBAN("DE41 3006 0601 0006 6056 05").getFormatted());
    }

    /**
     * Test method for {@link IBAN#getFormatted()}.
     */
    @Test
    public void testGetFormattedUppercase() {
        assertEquals("DE41 3006 0601 0006 6056 05", new IBAN("de41300606010006605605").getFormatted());
    }

}
