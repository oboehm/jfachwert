/*
 * Copyright (c) 2018 by Oliver Boehm
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
 * (c)reated 12.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link LANR}-Klasse.
 *
 * @author oboehm
 */
public class LANRTest extends AbstractFachwertTest {

    /**
     * Zum Testen verwenden wir die Pseudo-Nummer fuer Bundeswehraerzte,
     * Zahnaerzte und Hebammen.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected LANR createFachwert() {
        return LANR.PSEUDO_NUMMER;
    }

    @Test(expected = IllegalArgumentException.class)
    public void test8stelligeLANR() {
        LANR.of(12345678);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test10stelligeLANR() {
        LANR.of(1234567890);
    }

    @Test
    public void testGetArztnummer() {
        assertEquals(1234567, LANR.of(123456789).getArztnummer());
    }

    @Test
    public void testGetPruefziffer() {
        assertEquals(7, LANR.of(123456789).getPruefziffer());
    }

    @Test
    public void testGetFachgruppe() {
        assertEquals(21, LANR.of(987654321).getFachgruppe());
    }

}
