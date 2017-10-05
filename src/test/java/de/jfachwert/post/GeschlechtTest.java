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
 * (c)reated 05.10.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.post;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link Geschlecht}.
 */
public class GeschlechtTest {

    /**
     * Test-Methode fuer {@link Geschlecht#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("m\u00e4nnlich", Geschlecht.MAENNLICH.toString());
    }

}
