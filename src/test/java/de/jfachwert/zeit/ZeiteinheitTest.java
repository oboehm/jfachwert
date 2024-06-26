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
 * (c)reated 01.03.24 by oboehm
 */
package de.jfachwert.zeit;

import de.jfachwert.FachwertTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer Zeiteinheit.
 *
 * @author oboehm
 * @since 5-4 (01.03.24)
 */
class ZeiteinheitTest extends FachwertTest {

    @Override
    protected Zeiteinheit createFachwert() {
        return Zeiteinheit.DAYS;
    }

    @Test
    void ofTimeUnit() {
        assertEquals(Zeiteinheit.HOURS, Zeiteinheit.of(TimeUnit.HOURS));
    }

    @Test
    void ofTemporalUnit() {
        assertEquals(Zeiteinheit.ERAS, Zeiteinheit.of(ChronoUnit.ERAS));
    }

    @Test
    void toTimeUnit() {
        TimeUnit unit = TimeUnit.DAYS;
        Zeiteinheit einheit = Zeiteinheit.of(unit);
        assertEquals(unit, einheit.toTimeUnit());
    }

    @Test
    void toNanos() {
        long d = 7;
        assertEquals(TimeUnit.DAYS.toNanos(d), Zeiteinheit.DAYS.toNanos(d).longValue());
    }

    @Test
    void toNanosWeek() {
        assertEquals(Zeiteinheit.WEEKS.toNanos(1), Zeiteinheit.DAYS.toNanos(7));
    }

    @Test
    void toNanosCentury() {
        assertEquals(Zeiteinheit.CENTURIES.toNanos(10), Zeiteinheit.MILLENNIA.toNanos(1));
    }

    @Test
    void toMicros() {
        assertEquals(BigInteger.valueOf(1000), Zeiteinheit.MILLISECONDS.toMicros(1));
    }

    @Test
    void toMillis() {
        assertEquals(BigInteger.valueOf(1000), Zeiteinheit.SECONDS.toMillis(1));
    }

    @Test
    void toSeconds() {
        assertEquals(BigInteger.valueOf(60), Zeiteinheit.MINUTES.toSeconds(1));
    }

    @Test
    void toMinutes() {
        assertEquals(BigInteger.valueOf(60), Zeiteinheit.HOURS.toMinutes(1));
    }

    @Test
    void toHours() {
        assertEquals(BigInteger.valueOf(24), Zeiteinheit.DAYS.toHours(1));
    }

    @Test
    void toDays() {
        assertEquals(BigInteger.valueOf(7), Zeiteinheit.WEEKS.toDays(1));
    }

    @Test
    void toWeeks() {
        assertEquals(BigInteger.valueOf(4), Zeiteinheit.MONTHS.toWeeks(1));
    }

    @Test
    void toMonths() {
        assertEquals(BigInteger.valueOf(12), Zeiteinheit.YEARS.toMonths(1));
    }

    @Test
    void toYears() {
        assertEquals(BigInteger.valueOf(10), Zeiteinheit.DECADES.toYears(1));
    }

    @Test
    void toDecades() {
        assertEquals(BigInteger.valueOf(10), Zeiteinheit.CENTURIES.toDecades(1));
    }

    @Test
    void toCenturies() {
        assertEquals(BigInteger.valueOf(10), Zeiteinheit.MILLENNIA.toCenturies(1));
    }

    @Test
    void toMillenia() {
        assertEquals(BigInteger.valueOf(1_000), Zeiteinheit.ERAS.toMillenia(1));
        assertEquals(BigInteger.valueOf(1_000_000), Zeiteinheit.ERAS.toYears(1));
    }

    @Test
    void toJahrmillionen() {
        assertEquals(BigInteger.valueOf(1), Zeiteinheit.ERAS.toJahrmillionen(1));
    }

    @Test
    void getDuration() {
        Duration eras = Zeiteinheit.ERAS.getDuration();
        Duration forever = Zeiteinheit.FOREVER.getDuration();
        assertThat(forever.compareTo(eras), greaterThan(0));
    }

    @Test
    void isDurationEstimated() {
        assertTrue(Zeiteinheit.MINUTES.isDurationEstimated());
        assertFalse(Zeiteinheit.FOREVER.isDurationEstimated());
    }

    @Test
    void isTimeBased() {
        assertTrue(Zeiteinheit.HALF_DAYS.isTimeBased());
        assertFalse(Zeiteinheit.DAYS.isTimeBased());
        assertFalse(Zeiteinheit.FOREVER.isTimeBased());
    }

    @Test
    void isDateBased() {
        assertFalse(Zeiteinheit.HALF_DAYS.isDateBased());
        assertTrue(Zeiteinheit.DAYS.isDateBased());
        assertFalse(Zeiteinheit.FOREVER.isDateBased());
    }

    @Test
    void addToLocalDate() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrorw = today.plusDays(1);
        assertEquals(tomorrorw, Zeiteinheit.DAYS.addTo(today, 1));
    }

    @Test
    void addToLocalDateTime() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrorw = today.plusDays(1);
        assertEquals(tomorrorw, Zeiteinheit.DAYS.addTo(today, 1));
    }

    @Test
    void between() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrorw = today.plusDays(1);
        assertEquals(24, Zeiteinheit.HOURS.between(today, tomorrorw));
        assertEquals(-24, Zeiteinheit.HOURS.between(tomorrorw, today));
    }

}
