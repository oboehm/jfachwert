package de.jfachwert.formular;/*
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
 * (c)reated 28.08.2017 by oboehm (ob@oasd.de)
 */

import org.junit.jupiter.api.Test;

import de.jfachwert.pruefung.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link Anrede}-Klasse.
 *
 * @author oboehm
 * @since x.x (28.08.2017)
 */
public final class AnredeTest {

    /**
     * Test-Methode fuer {@link Anrede#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("Herr", Anrede.HERR.toString());
    }

    /**
     * Test-Methode fuer {@link Anrede#toString()}.
     */
    @Test
    public void testToStringHerrUndFrau() {
        assertEquals("Herr und Frau", Anrede.HERR_UND_FRAU.toString());
    }

    /**
     * Test-Methode fuer {@link Anrede#of(int)}.
     */
    @Test
    public void testOf() {
        assertEquals(Anrede.OHNE_ANREDE, Anrede.of(0));
    }

    /**
     * Test-Methode fuer {@link Anrede#of(int)}.
     */
    @Test
    public void testOfLast() {
        assertEquals(Anrede.VEREINIGUNG, Anrede.of(6));
    }

    /**
     * Test-Methode fuer {@link Anrede#of(int)} mit fehlerhaftem Argument.
     */
    @Test
    public void testOfInvalidArgument() {
        assertThrows(ValidationException.class, () -> Anrede.of(7));
    }

}
