/*
 * Copyright (c) 2023-2024 by Oli B.
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Tests fuer {@link Zeitdauer}-Klasse.
 *
 * @author oboehm
 */
public final class ZeitdauerTest extends FachwertTest {

    private static final Logger log = Logger.getLogger(ZeitdauerTest.class.getName());

    @Override
    protected Zeitdauer createFachwert() {
        return Zeitdauer.of(Zeitpunkt.EPOCH, Zeitpunkt.of(LocalDate.of(2024, 3, 9)));
    }

    @Test
    void start() {
        Zeitdauer zeitdauer = Zeitdauer.start();
        log.log(Level.INFO, "Zeitdauer = " + zeitdauer);
        assertThat(zeitdauer.getZaehler().longValue(), greaterThan(0L));
    }

    @Test
    void stop() {
        Zeitdauer start = Zeitdauer.start();
        Zeitdauer zeitdauer = start.stop();
        log.log(Level.INFO, "Zeitdauer = " + zeitdauer);
        assertThat(zeitdauer.getTimeInNanos(), greaterThan(BigInteger.ZERO));
        assertThat(zeitdauer.getTimeInNanos(), lessThan(start.getTimeInNanos()));
    }

    @Test
    void units() {
        for (Zeiteinheit unit : Zeiteinheit.getEntries()) {
            if (unit == Zeiteinheit.FOREVER) {
                break;
            }
            Zeitdauer zeitdauer = Zeitdauer.of(6, unit);
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
            Zeitdauer zeitdauer = new Zeitdauer(5, unit);
            String s = zeitdauer.toString();
            assertThat("5 " + unit, s, containsString("5"));
        }
    }

    @Test
    void testTimeInNanos() {
        Zeitdauer zeitdauer = Zeitdauer.of(2, TimeUnit.MILLISECONDS);
        assertEquals(BigInteger.valueOf(2_000_000), zeitdauer.getTimeInNanos());
    }

    @Test
    void testTimeInMillis() {
        Zeitdauer zeitdauer = Zeitdauer.of(3, TimeUnit.MILLISECONDS);
        assertEquals(3L, zeitdauer.getTimeInMillis());
    }

    @Test
    void testGetZaehler() {
        Zeitdauer zeitdauer = Zeitdauer.of(1, TimeUnit.DAYS);
        assertEquals(BigInteger.valueOf(24), zeitdauer.getZaehler(TimeUnit.HOURS));
    }

    @Test
    void get() {
        Zeitdauer zeitdauer = Zeitdauer.of(4, TimeUnit.SECONDS);
        assertEquals(4, zeitdauer.get(Zeiteinheit.SECONDS));
        assertEquals(4_000, zeitdauer.get(Zeiteinheit.MILLISECONDS));
        assertEquals(4_000_000_000L, zeitdauer.get(Zeiteinheit.NANOSECONDS));
    }

    @Test
    void getEra() {
        Zeitdauer zeitdauer = Zeitdauer.of(5, Zeiteinheit.ERAS);
        assertEquals(5, zeitdauer.get(Zeiteinheit.ERAS));
        assertEquals(5_000, zeitdauer.get(Zeiteinheit.MILLENNIA));
        assertEquals(5_000_000, zeitdauer.get(Zeiteinheit.YEARS));
    }

    @Test
    void getUnitsNanos() {
        Zeitdauer zeitdauer = Zeitdauer.of(6, Zeiteinheit.NANOSECONDS);
        assertEquals(1, zeitdauer.getUnits().size());
    }

    @Test
    void getUnitsEras() {
        Zeitdauer zeitdauer = Zeitdauer.of(7, Zeiteinheit.ERAS);
        assertThat(zeitdauer.getUnits().size(), greaterThan(1));
    }

    @Test
    void addToLocalDate() {
        LocalDate today = LocalDate.now();
        Zeitdauer days = Zeitdauer.of(8, Zeiteinheit.DAYS);
        assertEquals(today.plusDays(8), days.addTo(today));
    }

    @Test
    void addToLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        Zeitdauer hours = Zeitdauer.of(9, Zeiteinheit.HOURS);
        assertEquals(now.plusHours(9), hours.addTo(now));
    }

}
