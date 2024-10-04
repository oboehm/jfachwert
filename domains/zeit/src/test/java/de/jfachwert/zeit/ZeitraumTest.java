/*
 * Copyright (c) 2024 by Oli B.
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
 * (c)reated 19.01.24 by oboehm
 */
package de.jfachwert.zeit;

import de.jfachwert.FachwertTest;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer die {@link Zeitraum}-Klasse.
 *
 * @author oboehm
 * @since 5.2 (19.01.24)
 */
final class ZeitraumTest extends FachwertTest {

    @Override
    protected Zeitraum createFachwert() {
        return Zeitraum.of("2018-01-24 - 2024-01-19");
    }

    @Test
    void getZeitdauer() {
        Zeitdauer zeitdauer = Zeitraum.of("2024-01-01 - 2025-01-01").getZeitdauer();
        assertEquals(366, zeitdauer.getZaehler(TimeUnit.DAYS).intValue());
    }

    @Test
    void ofInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Zeitraum.of("invalid"));
    }

    @Test
    void ofDate() {
        Date epoch = new Date(0);
        Date today = new Date();
        Zeitraum zeitraum = Zeitraum.of(epoch, today);
        assertEquals(today.getTime(), zeitraum.getZeitdauer().getTimeInMillis());
    }

    @Test
    void ofTimestamp() {
        Timestamp t1 = new Timestamp(1);
        Timestamp t2 = new Timestamp(2);
        Zeitraum zeitraum = Zeitraum.of(t1, t2);
        assertEquals(t2.getTime() - t1.getTime(), zeitraum.getZeitdauer().getTimeInMillis());
    }

    @Test
    void ofLocalDate() {
        LocalDate d1 = LocalDate.EPOCH;
        LocalDate d2 = LocalDate.now();
        Zeitraum zeitraum = Zeitraum.of(d1, d2);
        assertEquals(d2.toEpochDay(), zeitraum.getZeitdauer().getZaehler(TimeUnit.DAYS).longValue());
    }

    @Test
    void ofLocalDateTime() {
        LocalDateTime t1 = LocalDateTime.now();
        LocalDateTime t2 = LocalDateTime.now();
        Zeitraum zeitraum = Zeitraum.of(t1, t2);
        assertThat(zeitraum.getZeitdauer().getTimeInNanos().longValue(), greaterThan(0L));
    }

    @Test
    void offenerZeitraumVon() {
        Zeitpunkt now = Zeitpunkt.now();
        Zeitraum zeitraum = Zeitraum.of(now, null);
        assertEquals(Zeitpunkt.MAX, zeitraum.getBis());
        Zeitraum von = Zeitraum.von(now);
        assertEquals(zeitraum, von);
    }

    @Test
    void offenerZeitraumBis() {
        Zeitpunkt now = Zeitpunkt.now();
        Zeitraum zeitraum = Zeitraum.of(null, now);
        assertEquals(Zeitpunkt.MIN, zeitraum.getVon());
        Zeitraum bis = Zeitraum.bis(now);
        assertEquals(zeitraum, bis);
    }

    @Test
    void offenerZeitraumLocalDate() {
        Zeitraum zeitraum = Zeitraum.of(null, LocalDate.now());
        assertEquals(Zeitpunkt.MIN, zeitraum.getVon());
    }

    @Test
    void offenerZeitraumDate() {
        Zeitraum zeitraum = Zeitraum.of(null, new Date());
        assertEquals(Zeitpunkt.MIN, zeitraum.getVon());
    }

    @Test
    void offenerZeitraumTimestamp() {
        Zeitraum zeitraum = Zeitraum.of(null, new Timestamp(0L));
        assertEquals(Zeitpunkt.MIN, zeitraum.getVon());
    }

    @Test
    void testErdzeitalter() {
        assertTrue(Zeitraum.PRAEKAMBRIUM.isBefore(Zeitraum.PALAEOZOLIKUM));
        assertTrue(Zeitraum.PALAEOZOLIKUM.isBefore(Zeitraum.MESOZOIKUM));
        assertTrue(Zeitraum.MESOZOIKUM.isBefore(Zeitraum.KAENOZOIKUM));
        assertFalse(Zeitraum.PRAEKAMBRIUM.isAfter(Zeitraum.PALAEOZOLIKUM));
        assertFalse(Zeitraum.PALAEOZOLIKUM.isAfter(Zeitraum.MESOZOIKUM));
        assertFalse(Zeitraum.MESOZOIKUM.isAfter(Zeitraum.KAENOZOIKUM));
    }

}
