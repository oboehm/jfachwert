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
 * (c)reated 10.07.23 by oboehm
 */
package de.jfachwert.zeit;

import de.jfachwert.FachwertTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Tests fuer {@link Zeitdauer}-Klasse.
 *
 * @author oboehm
 */
public final class ZeitdauerTest extends FachwertTest {

    private static final Logger log = Logger.getLogger(ZeitdauerTest.class.getName());

    @Test
    void defaultCtor() {
        Zeitdauer zeitdauer = new Zeitdauer();
        log.log(Level.INFO, "Zeitdauer = " + zeitdauer);
        assertThat(zeitdauer.getZaehler().longValue(), greaterThan(0L));
    }

    @Test
    void units() {
        for (TimeUnit unit : TimeUnit.values()) {
            Zeitdauer zeitdauer = new Zeitdauer(10, unit);
            assertEquals(unit, zeitdauer.getEinheit());
        }
    }

    @Test
    @Override
    public void testToString() {
        Zeitdauer zeitdauer = new Zeitdauer(5, TimeUnit.DAYS);
        String s = zeitdauer.toString();
        if (Locale.getDefault().getLanguage().equalsIgnoreCase("DE")) {
            assertEquals("5 Tage", s);
        } else {
            assertEquals("5 d", s);
        }
    }

    @Test
    void testToStringAllUnits() {
        for (TimeUnit unit : TimeUnit.values()) {
            Zeitdauer zeitdauer = new Zeitdauer(42, unit);
            String s = zeitdauer.toString();
            assertThat("42 " + unit, s, containsString("42"));
        }
    }

    @Test
    void testTimeInNanos() {
        Zeitdauer zeitdauer = new Zeitdauer(2, TimeUnit.MILLISECONDS);
        assertEquals(BigInteger.valueOf(2_000_000), zeitdauer.getTimeInNanos());
    }

    @Test
    void testTimeInMillis() {
        Zeitdauer zeitdauer = new Zeitdauer(3, TimeUnit.MILLISECONDS);
        assertEquals(3L, zeitdauer.getTimeInMillis());
    }

}
