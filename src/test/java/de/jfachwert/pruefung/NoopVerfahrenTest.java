package de.jfachwert.pruefung;/*
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
 * (c)reated 26.03.2017 by oboehm (ob@jfachwert.de)
 */

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit-Test fuer {@link NoopVerfahren}-Klasse.
 *
 * @author oboehm
 */
public final class NoopVerfahrenTest {

    private final NoopVerfahren noop = new NoopVerfahren();

    /**
     * Mit diesem Verfahren sollte jeder Input gueltig sein.
     */
    @Test
    public void testIsValid() {
        String hello = "world";
        assertTrue("all input should be valid", noop.isValid(hello));
    }

    /**
     * Mit diesem Verfahren sollte jeder Input gueltig sein und unveraendert
     * von der {@link NoopVerfahren#validate(Serializable)}-Methode ausgespuckt
     * werden.
     */
    @Test
    public void testValdate() {
        Integer answer = 42;
        assertEquals(answer, noop.validate(answer));
    }

}
