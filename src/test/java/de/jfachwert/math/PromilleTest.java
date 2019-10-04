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
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Unit-Test fuer {@link Promille}-Klasse.
 *
 * @author oboehm
 */
public final class PromilleTest extends FachwertTest {

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
    public void testNoDuplicate() {
        Promille one = Promille.of("1");
        Promille sameOne = Promille.of("1 °/oo");
        assertSame(one, sameOne);
    }

    @Test
    public void testToBigDecimal() {
        assertEquals(BigDecimal.ONE, Promille.of(1000).toBigDecimal());
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

}
