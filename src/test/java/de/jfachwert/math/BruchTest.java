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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 02.04.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.math;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Fachwert;
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link Bruch}-Klasse.
 *
 * @author oliver (ob@jfachwert.de)
 */
public final class BruchTest extends AbstractFachwertTest {

    /**
     * Zum Testen wird ein einfacher Bruch (1/2) verwendet.
     *
     * @return 1/2 als Bruch
     */
    @Override
    protected Fachwert createFachwert() {
        return new Bruch(1, 2);
    }

    /**
     * Test-Methode fuer {@link Bruch#kuerzen()}.
     */
    @Test
    public void testKuerzen() {
        Bruch gekuerzt = Bruch.of(6, 12).kuerzen();
        assertEquals(Bruch.of(1, 2), gekuerzt);
        assertEquals(BigInteger.ONE, gekuerzt.getZaehler());
        assertEquals(BigInteger.valueOf(2), gekuerzt.getNenner());
    }

    /**
     * Fuer equals soll gelten, dass der Wert verglichen wird. D.h. bei "2/3"
     * und "6/9" handelt es sich jeweils um die gleichen Brueche.
     */
    @Test
    public void testEqualsUnkgekuerzt() {
        ObjectTester.assertEquals(Bruch.of(2, 3), Bruch.of(6, 9));
    }

}