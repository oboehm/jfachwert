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

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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

    @Test(expected = IllegalArgumentException.class)
    public void testBruchInvalid() {
        new Bruch("TE/ST");
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
    public void testEqualsUngekuerzt() {
        ObjectTester.assertEquals(Bruch.of(2, 3), Bruch.of(6, 9));
    }

    /**
     * Testmethode fuer {@link Bruch#multiply(Bruch)}.
     */
    @Test
    public void testMultiply() {
        assertEquals(Bruch.of("2/5"), Bruch.of("3/4").multiply(Bruch.of("8/15")));
    }

    /**
     * Testmethode fuer {@link Bruch#divide(Bruch)}.
     */
    @Test
    public void testDivide() {
        assertEquals(Bruch.of("45/32"), Bruch.of("3/4").divide(Bruch.of("8/15")));
    }

    /**
     * Testmethode fuer {@link Bruch#add(Bruch)}.
     */
    @Test
    public void testAdd() {
        assertEquals(Bruch.of("4/3"), Bruch.of("1/2").add(Bruch.of("5/6")));
    }

    /**
     * Testmethode fuer {@link Bruch#subtract(Bruch)}.
     */
    @Test
    public void testSubtract() {
        assertEquals(Bruch.of("-1/3"), Bruch.of("1/2").subtract(Bruch.of("5/6")));
    }

    /**
     * Dezimalzahlen lassen sich auch in Brueche wandeln. Dies wird hier
     * getestet.
     */
    @Test
    public void testOfBigDecimal() {
        assertEquals(Bruch.of("5/4"), Bruch.of("1.25"));
    }

    /**
     * Testfall fuer {@link Bruch#Bruch(BigDecimal)}.
     */
    @Test
    public void testBruchBigDecimal() {
        assertEquals(Bruch.of("1/2"), new Bruch(new BigDecimal("0.5")));
    }

    /**
     * Testfall fuer {@link Bruch#compareTo(Bruch)}.
     */
    @Test
    public void testComparable() {
        Bruch kleinererBruch = Bruch.of("1/3");
        Bruch groessererBruch = Bruch.of("1/2");
        assertThat(kleinererBruch.compareTo(groessererBruch), lessThan(0));
        assertThat(groessererBruch.compareTo(kleinererBruch), greaterThan(0));
    }

    /**
     * Testfall fuer {@link Bruch#compareTo(Bruch)}.
     */
    @Test
    public void testComparableEquals() {
        Bruch halb = Bruch.of("1/2");
        Bruch zweiViertel = Bruch.of("2/4");
        assertEquals(0, halb.compareTo(zweiViertel));
    }

    /**
     * Testmethode fuer {@link Bruch#toBigDecimal()}.
     */
    @Test
    public void testToBigDecimal() {
        Bruch dreiviertel = Bruch.of("3/4");
        assertEquals(new BigDecimal("0.75"), dreiviertel.toBigDecimal());
    }

    /**
     * Testmethoden fuer abstrakte Methoden aus der {@link Number}-Klasse.
     */
    @Test
    public void testNumberMethods() {
        AbstractNumber half = Bruch.of("1/2");
        BigDecimal expected = new BigDecimal("0.5");
        assertEquals(expected.intValue(), half.intValue());
        assertEquals(expected.longValue(), half.longValue());
        assertEquals(expected.floatValue(), half.floatValue(), 0.001);
        assertEquals(expected.doubleValue(), half.doubleValue(), 0.001);
    }

}
