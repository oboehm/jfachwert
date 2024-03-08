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

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Tests fuer Zeiteinheit.
 *
 * @author oboehm
 * @since 5-4 (01.03.24)
 */
class ZeiteinheitTest {

    @Test
    void ofTimeUnit() {
        assertEquals(Zeiteinheit.HOURS, Zeiteinheit.of(TimeUnit.HOURS));
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

}
