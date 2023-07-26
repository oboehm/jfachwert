/*
 * Copyright (c) 2023 by Oli B.
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
 * (c)reated 18.07.23 by oboehm
 */
package de.jfachwert.zeit;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.AbstractFachwertTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Tests fuer {@link Zeitpunkt}-Klasse.
 *
 * @author oboehm
 */
public final class ZeitpunktTest extends AbstractFachwertTest<BigInteger, Zeitpunkt> {

    @Override
    protected AbstractFachwert<BigInteger, Zeitpunkt> createFachwert(String code) {
        return Zeitpunkt.of(BigInteger.ZERO);
    }

    @Test
    void stringCtor() {
        Zeitpunkt now = Zeitpunkt.now();
        Zeitpunkt jetzt = new Zeitpunkt(now.toString());
        assertEquals(now, jetzt);
    }

    @Test
    void plusMinus() {
        Zeitpunkt one = Zeitpunkt.of(BigInteger.ONE);
        Zeitpunkt nine = Zeitpunkt.of(BigInteger.valueOf(9));
        Zeitpunkt ten = Zeitpunkt.of(BigInteger.TEN);
        assertEquals(ten, nine.plus(one));
        assertEquals(ten, ten.minus(one).plus(one));
    }

    @Test
    @Override
    public void testToString() {
        Zeitpunkt now = Zeitpunkt.of(BigInteger.ZERO);
        String s = now.toString();
        assertEquals("1970-01-01 00:00:00.000000000", s);
    }

}