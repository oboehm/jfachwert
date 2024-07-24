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
package de.jfachwert.formular;

import de.jfachwert.FachwertTest;
import de.jfachwert.pruefung.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link Geschlecht}.
 */
public class GeschlechtTest extends FachwertTest {

    @Override
    protected Geschlecht createFachwert() {
        return Geschlecht.UNBEKANNT;
    }

    /**
     * Test-Methode fuer {@link Geschlecht#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("m\u00e4nnlich", Geschlecht.MAENNLICH.toString());
    }

    /**
     * Test-Methode fuer {@link Geschlecht#of(int)}
     */
    @Test
    public void testOfInt() {
        assertEquals(Geschlecht.JURISTISCHE_PERSON, Geschlecht.of(0));
    }

    /**
     * Test-Methode fuer {@link Geschlecht#of(int)}
     */
    @Test
    public void testOfInvalidInt() {
        assertThrows(ValidationException.class, () -> Geschlecht.of(-1));
    }

    /**
     * Test-Methode fuer {@link Geschlecht#of(char)}
     */
    @Test
    public void testOfChar() {
        assertEquals(Geschlecht.WEIBLICH, Geschlecht.of('w'));
    }

    /**
     * Test-Methode fuer {@link Geschlecht#of(char)}
     */
    @Test
    public void testOfInvalidChar() {
        assertEquals(Geschlecht.UNBEKANNT, Geschlecht.of('x'));
    }

}
