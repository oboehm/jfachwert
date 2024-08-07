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
 * (c)reated 26.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math;

import de.jfachwert.FachwertTest;
import org.junit.jupiter.api.Test;
import patterntesting.runtime.util.Converter;

import java.io.NotSerializableException;
import java.math.BigInteger;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link Nummer}-Klasse.
 *
 * @author oboehm
 */
public final class NummerTest extends FachwertTest {

    @Override
    protected Nummer createFachwert() {
        return new Nummer(42);
    }

    /**
     * Fehlerhafte Argumente sollten mit einer {@link IllegalArgumentException}
     * zurueckgewiesen werden.
     */
    @Test
    public void testNummerInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Nummer("TEST"));
    }

    @Test
    public void testShortValue() {
        Nummer nummer = new Nummer(42);
        assertEquals((short) 42, nummer.shortValue());
    }

    /**
     * Test-Methode fuer {@link Nummer#intValue()}.
     */
    @Test
    public void testIntValue() {
        Nummer nummer = new Nummer("4711");
        assertEquals(4711, nummer.intValue());
    }

    /**
     * Hier wird die Groesse des Objekts getestet. Als String abgespeichert
     * verbraucht eine vierstellig Zahl 4 Bytes (ohne Verwaltung-Overhead).
     * Dies gilt es zu unterbinden.
     * 
     * @throws NotSerializableException sollte nicht passieren
     */
    @Test
    public void testObjectSize() throws NotSerializableException {
        Nummer einstellig = new Nummer("1");
        Nummer fuenfstellig = new Nummer("70839");
        long n = Converter.serialize(fuenfstellig).length;
        long diff = n - Converter.serialize(einstellig).length;
        assertThat(diff, lessThanOrEqualTo(4L));
    }

    /**
     * Hier testen wir, ob die of-Method tatsaechlich immer dieselbe Zahl
     * zurueckliefert.
     */
    @Test
    public void testOf() {
        Nummer n1 = Nummer.of(1);
        Nummer n2 = Nummer.of("1");
        Nummer n3 = Nummer.of(BigInteger.ONE);
        assertSame(n1, n2);
        assertSame(n2, n3);
    }

    @Test
    public void testCompareTo() {
        Nummer eins = Nummer.of(1);
        Nummer zwei = Nummer.of(2);
        assertThat(eins.compareTo(zwei), lessThan(0));
        assertThat(zwei.compareTo(eins), greaterThan(0));
    }

}
