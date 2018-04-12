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
 * (c)reated 05.04.2018 by oboehm (ob@oasd.de)
 */

package de.jfachwert.math;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link Primzahl}-Klasse.
 *
 * @author oboehm
 */
public final class PrimzahlTest extends AbstractFachwertTest {

    /**
     * Zum Testen nehmen wir die erste Primzahl.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Primzahl createFachwert() {
        return Primzahl.ZWEI;
    }

    /**
     * Testmethode fuer {@link Primzahl#after(int)}.
     */
    @Test
    public void testAfter() {
        assertEquals(11, Primzahl.after(7).intValue());
    }

    /**
     * Testmethode fuer {@link Primzahl#next()}.
     */
    @Test
    public void testNext() {
        Primzahl dreizehn = Primzahl.after(12);
        assertEquals(17, dreizehn.next().intValue());
    }

    /**
     * Dieser Test dient nur dazu, um den Rechner zu beschaeftigen.
     * Der Test dauert auf einem Fujitsu-Notebook mit i7 und 2.5 GHz 
     * ca. 3 Sekunden. Mit 10_000_000 sind es bereits 3 Minuten und
     * mit 20_000_000 12 Minuten.
     */
    //@Test
    public void testBigPrimzahl() {
        Primzahl big = Primzahl.after(1_000_000);
        assertEquals(1000003, big.intValue());
    }

    /**
     * Testmethoden fuer abstrakte Methoden aus der {@link Number}-Klasse.
     */
    @Test
    public void testNumberMethods() {
        assertEquals(2, Primzahl.ZWEI.intValue());
        assertEquals(2L, Primzahl.ZWEI.longValue());
        assertEquals(2.0F, Primzahl.ZWEI.floatValue(), 0.001);
        assertEquals(2.0, Primzahl.ZWEI.doubleValue(), 0.001);
    }

}
