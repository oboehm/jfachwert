/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 01.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.math;

import de.jfachwert.FachwertTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Test fuer {@link Promille}-Klasse.
 *
 * @author oboehm
 */
public final class PromilleTest extends FachwertTest {

    private static final Logger LOG = Logger.getLogger(PromilleTest.class.getName());

    /**
     * Zum Testen verwenden wir 0,8 Promille.
     *
     * @return 0.8 Promille
     */
    @Override
    protected Promille createFachwert() {
        return Promille.of("0.8");
    }

    @Test
    public void testOf() {
        Promille p = Promille.of("2 °/oo");
        assertEquals(Promille.of(BigDecimal.valueOf(2)), p);
    }

    @Test
    public void testOfDouble() {
        Promille p1 = Promille.of(1.25);
        Promille p2 = Promille.of("1.25");
        assertEquals(p1, p2);
    }

    @Test
    public void testOfCaching() {
        BigDecimal n = new BigDecimal("0.8");
        Promille p1 = Promille.of(n);
        Promille p2 = Promille.of(n);
        assertEquals(p1, p2);
        assertSame(p1, p2);
        if (forceGC()) {
            Promille p3 = Promille.of(n);
            assertNotSame(p1, p3);
            assertEquals(p1, p3);
        } else {
            LOG.info("GC wurde nicht durchgefuehrt.");
        }
    }

    @Test
    public void testOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Prozent.of("1.2.3"));
    }

    @Test
    public void testNoDuplicate() {
        Promille one = Promille.of("1");
        Promille sameOne = Promille.of("1 °/oo");
        assertSame(one, sameOne);
    }

    @Test
    public void testToBigDecimal() {
        assertEquals(BigDecimal.ONE, Promille.THOUSAND.toBigDecimal());
    }

    @Test
    public void testIntValue() {
        assertEquals(2, Promille.of("2000 °/oo").intValue());
    }

    @Test
    public void testLongValue() {
        assertEquals(3L, Promille.of("3000 \u2030").longValue());
    }

    @Test
    public void testDoubleValue() {
        assertEquals(0.01, Promille.TEN.doubleValue(), 0.0001);
    }

    @Test
    public void testMultiply() {
        Promille p = Promille.of(2);
        assertEquals(BigDecimal.valueOf(0.006), p.multiply(3));
    }

    @Test
    public void testToString() {
        Promille p = Promille.of(30.25);
        String s = p.toString();
        assertThat(s, containsString("30"));
        assertThat(s, endsWith(String.valueOf(Promille.ZEICHEN)));
        switch (Locale.getDefault().getLanguage()) {
            case "de":
                assertEquals("30,25" + Promille.ZEICHEN, s);
                break;
            case "en":
                assertEquals("30.25" + Promille.ZEICHEN, s);
                break;
        }
    }

}
