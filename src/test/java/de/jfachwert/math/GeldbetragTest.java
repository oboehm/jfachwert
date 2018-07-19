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
 * (c)reated 18.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit-Tests fuer {@link Geldbetrag}-Klasse.
 *
 * @author oboehm
 * @since x.x (18.07.2018)
 */
public final class GeldbetragTest extends AbstractFachwertTest {

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden. Und zwar muss jedesmal
     * der gleiche Fachwert erzeugt werden, weil sonst der equals-Test nicht
     * funktioniert.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Geldbetrag createFachwert() {
        return new Geldbetrag(BigDecimal.ONE);
    }

    /**
     * Rundungsdifferenzen beim Vergleich im 1/10-Cent-Bereich sollten keine
     * Rolle fuer den Vergleich spielen.
     */
    @Test
    public void testEqualsGerundet() {
        Geldbetrag one = new Geldbetrag(1);
        Geldbetrag hundredCents = new Geldbetrag(0.9999);
        assertEquals(one, hundredCents);
    }

    /**
     * Bei 0 sollte das Vorzeichen keine Rolle spielen.
     */
    @Test
    public void testEqualsZero() {
        Geldbetrag plus = new Geldbetrag("0.0049");
        Geldbetrag minus = new Geldbetrag("-0.0049");
        assertEquals(plus, minus);
    }

    /**
     * Gleiche Betraege, aber mit unterschiedlichen Waehrungen, sind nicht
     * gleich. Dies wird hier getestet.
     */
    @Test
    public void testNotEquals() {
        Geldbetrag one = new Geldbetrag(1.0);
        Geldbetrag anotherOne = new Geldbetrag(1.0);
        Geldbetrag oneDM = new Geldbetrag(1.0).withWaehrung("DEM");
        ObjectTester.assertEquals(one, anotherOne);
        assertFalse(one + " != " + oneDM, one.equals(oneDM));
    }

}
