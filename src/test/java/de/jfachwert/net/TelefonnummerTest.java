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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 04.09.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.*;
import org.junit.*;
import patterntesting.runtime.junit.*;

import javax.validation.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link Telefonnummer}-Klasse.
 *
 * @author oboehm
 */
public final class TelefonnummerTest extends AbstractFachwertTest {

    /** Telefonnumer aus Spider Murphy's "Skandal im Sperrbezirik". */
    private Telefonnummer rosi = new Telefonnummer("+49 (0)811 32 16 8");

    /**
     * Zum Testen nehmen wir eine fiktive Telefonnummer (aus Wikipedia).
     *
     * @return "+49 30 12345-67"
     */
    @Override
    protected Telefonnummer createFachwert() {
        return new Telefonnummer("+49 30 12345-67");
    }

    /**
     * Eine falsche Telefonnummer sollte zurueckgewiesen werden.
     */
    @Test(expected = ValidationException.class)
    public void testInvalidTelefonnummer() {
        new Telefonnummer("12345-ABC");
    }

    /**
     * Auch wenn Telefonnummern unterschiedlich formattiert sind, sollten
     * gleiche Nummern als gleich erkannt werden.
     */
    @Test
    public void testEquals() {
        Telefonnummer sameRosi = new Telefonnummer("+49 811/32168");
        ObjectTester.assertEquals(rosi, sameRosi);
    }

    /**
     * Testmethode fuer {@link Telefonnummer#getLaenderkennzahl()}
     */
    @Test
    public void testGetLandeskennzahl() {
        assertEquals("+49", rosi.getLaenderkennzahl());
    }

}
