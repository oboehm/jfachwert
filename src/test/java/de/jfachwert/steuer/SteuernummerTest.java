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
 * (c)reated 14.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Fachwert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit-Tests fuer die {@link Steuernummer}-Klasse.
 */
public class SteuernummerTest extends AbstractFachwertTest {

    /**
     * Die Steuernummer aus diesem Beispiel stammt aus Wikipedia.
     *
     * @return eine Steuernummer aus Baden Wuerttemberg
     */
    protected Fachwert createFachwert() {
        return new Steuernummer("2893081508152");
    }

    /**
     * Das Beispiel fuer diesen Test stammt aus
     * https://de.wikipedia.org/wiki/Steuernummer.
     */
    @Test
    public void testGetPruefziffer() {
        Steuernummer nr = new Steuernummer("1121081508150");
        assertEquals(0, nr.getPruefziffer());
    }

    /**
     * Test-Methode fuer {@link Steuernummer#isValid()}.
     */
    @Test
    public void testIsValid() {
        Steuernummer nr = new Steuernummer("12345678000");
        assertTrue(nr + " should be valid", nr.isValid());
    }

    /**
     * Test-Methode fuer {@link Steuernummer#isValid()}.
     */
    @Test
    public void testIsNotValid() {
        Steuernummer nr = new Steuernummer("12345678001");
        assertFalse(nr + " should be not valid", nr.isValid());
    }

}
