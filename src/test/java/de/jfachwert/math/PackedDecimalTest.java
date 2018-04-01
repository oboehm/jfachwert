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
 * (c)reated 29.03.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import javax.validation.ValidationException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Unit-Tests fuer {@link PackedDecimal}-Klasse.
 *
 * @author oboehm
 */
public final class PackedDecimalTest extends AbstractFachwertTest {

    /**
     * Zum Testen nehmen wir eine Zahl mit fuehrender Null.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected PackedDecimal createFachwert() {
        return new PackedDecimal("0123456789");
    }

    /**
     * Bei der toString-Methode sollte das gleiche wieder rauskommen, was man
     * reingesteckt hat.
     */
    @Test
    public void testToString() {
        PackedDecimal agent = new PackedDecimal("007");
        assertEquals("007", agent.toString());
    }

    /**
     * Zahlen mit null Inhalt sollen erlaubt sein.
     */
    @Test
    public void testEmptyCtor() {
        PackedDecimal nix = PackedDecimal.EMPTY;
        assertEquals("", nix.toString());
    }

    /**
     * Ungueltige Werte sollten nicht akzeptiert werden.
     */
    @Test
    public void testCtorWithInvalidString() {
        try {
            new PackedDecimal("hello world");
        } catch (ValidationException expected) {
            String msg = expected.getMessage();
            if (!msg.contains("hello world")) {
                throw new IllegalStateException(msg + " does not contain 'hello world'", expected);
            }
        }
    }

    /**
     * Testmethode fuer {@link PackedDecimal#valueOf(BigDecimal)}.
     */
    @Test
    public void testValueOf() {
        PackedDecimal number = PackedDecimal.valueOf(42);
        assertEquals(new PackedDecimal(42), number);
    }

    /**
     * Testmethode fuer {@link PackedDecimal#valueOf(BigDecimal)}.
     */
    @Test
    public void testValueOfDouble() {
        PackedDecimal number = PackedDecimal.valueOf(3.14);
        assertEquals(new PackedDecimal(3.14), number);
    }

    /**
     * Da die Zahl "0" relativ haeufig vorkommt, sollte nicht jedesmal eine
     * neue "0" produziert werden, sondern jedesmal die gleiche "0"
     * zurueckgegeben werden.
     */
    @Test
    public void testValueOfZero() {
        assertSame(PackedDecimal.ZERO, PackedDecimal.valueOf("0"));
        assertSame(PackedDecimal.ZERO, PackedDecimal.valueOf(0));
        assertSame(PackedDecimal.ZERO, PackedDecimal.valueOf(BigDecimal.ZERO));
    }

    /**
     * Testmethode fuer {@link PackedDecimal#toBigDecimal()}.
     */
    @Test
    public void testToBigDecimal() {
        PackedDecimal ten = PackedDecimal.valueOf(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, ten.toBigDecimal());
    }

}
