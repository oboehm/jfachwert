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
 * (c)reated 04.08.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import javax.money.CurrencyUnit;

/**
 * Unit-Tests fuer {@link Waehrung}-Klasse.
 */
public final class WaehrungTest extends AbstractFachwertTest {

    /**
     * Zum Testen nehmen wir die Euro-Waehrung.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Waehrung createFachwert() {
        return new Waehrung("EUR");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWaehrungInvalid() {
        new Waehrung("Taler");
    }

    /**
     * Hier wird {@link Waehrung#compareTo(CurrencyUnit)} ueberprueft.
     */
    @Test
    public void testCompareTo() {
        Waehrung one = Waehrung.of("CHF");
        Waehrung anotherOne = Waehrung.of("CHF");
        ObjectTester.assertEquals(one, anotherOne);
        Waehrung two = Waehrung.of("EUR");
        ObjectTester.assertNotEquals(one, two);
    }

}
