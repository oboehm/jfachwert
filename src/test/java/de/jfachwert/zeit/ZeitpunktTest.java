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
 * (c)reated 18.07.23 by oboehm
 */
package de.jfachwert.zeit;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.AbstractFachwertTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link Zeitpunkt}-Klasse.
 *
 * @author oboehm
 */
public final class ZeitpunktTest extends AbstractFachwertTest<BigInteger, Zeitpunkt> {

    @Override
    protected AbstractFachwert<BigInteger, Zeitpunkt> createFachwert(String code) {
        return Zeitpunkt.of(code);
    }

    @Override
    protected String getCode() {
        return "0";
    }

    @Override
    protected String getInvalidCode() {
        return "xxx";
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
        Zeitpunkt epoch = Zeitpunkt.of(BigInteger.ZERO);
        String s = epoch.toLongString();
        assertThat(s, startsWith("1970-01-01"));
    }

    @Test
    public void testToLongString() {
        Zeitpunkt epoch = Zeitpunkt.of(BigInteger.ZERO);
        String s = epoch.toLongString();
        assertEquals("1970-01-01 00:00:00.000000000", s);
    }

    @Test
    public void testToShortString() {
        String s1 = "2024-01-18";
        String s2 = "2024-01-18 19:20";
        String s3 = "2024-01-18 19:20:21";
        String s4 = "2024-01-18 19:20:21.234";
        assertEquals(s1, Zeitpunkt.of(s1).toShortString());
        assertEquals(s2, Zeitpunkt.of(s2).toShortString());
        assertEquals(s3, Zeitpunkt.of(s3).toShortString());
        assertEquals(s4, Zeitpunkt.of(s4).toShortString());
    }

    @Test
    public void testEpoch() {
        Zeitpunkt epoch = Zeitpunkt.of(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
        assertEquals(Zeitpunkt.EPOCH, epoch);
        assertEquals(0L, epoch.getTimeInMillis());
    }

    @Test
    public void testToTimestamp() {
        Zeitpunkt t = Zeitpunkt.now();
        assertEquals(t.toTimestamp(), Timestamp.valueOf(t.toLocalDateTime()));
    }

    @Test
    void testToDate() {
        Date now = new Date();
        Zeitpunkt z = Zeitpunkt.of(now);
        assertEquals(now, z.toDate());
    }

    @Test
    void testToLocalDate() {
        LocalDate now = LocalDate.now();
        Zeitpunkt z = Zeitpunkt.of(now);
        assertEquals(now, z.toLocalDate());
    }

    @Test
    void testToLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        Zeitpunkt z = Zeitpunkt.of(now);
        assertEquals(now, z.toLocalDateTime());
    }

    @Test
    void testGetMillis() {
        long t0 = System.currentTimeMillis();
        long t1 = Zeitpunkt.now().getTimeInMillis();
        long t2 = System.currentTimeMillis();
        assertThat(t0, lessThanOrEqualTo(t1));
        assertThat(t1, lessThanOrEqualTo(t2));
    }

    @Test
    void toStringAndBack() {
        Zeitpunkt z = Zeitpunkt.of(BigInteger.ZERO);
        assertEquals(z, Zeitpunkt.of(z.toString()));
    }

    @Test
    void testOfDateTimeString() {
        Zeitpunkt epoch = Zeitpunkt.of("1970-01-01 00:00:00.000000000");
        assertEquals(Zeitpunkt.EPOCH, epoch);
    }

    @Test
    void testOfDateString() {
        String dateString = "2024-01-17";
        Zeitpunkt z = Zeitpunkt.of(dateString);
        assertEquals(dateString, z.toString());
    }

    @Test
    void testOfDate() {
        Date epoch = new Date(0);
        Zeitpunkt z = Zeitpunkt.of(epoch);
        assertEquals(Zeitpunkt.EPOCH, z);
    }

    @Test
    void testOfTimeUnit() {
        Zeitpunkt t1 = Zeitpunkt.of(BigInteger.ONE, TimeUnit.DAYS);
        assertEquals(LocalDate.of(1970, 1, 2), t1.toLocalDate());
    }

    @Test
    void testOfZeiteinheit() {
        BigInteger n = BigInteger.valueOf(365);
        assertEquals(Zeitpunkt.of(n, TimeUnit.DAYS), Zeitpunkt.of(n, Zeiteinheit.DAYS));
    }

    @Test
    void testMin() {
        String s = Zeitpunkt.MIN.toString();
        assertNotNull(s);
        if (Locale.GERMANY.equals(Locale.getDefault())) {
            assertEquals("vor 13,8 Mrd. Jahren", s);
        }
    }

    @Test
    void testMax() {
        String s = Zeitpunkt.MAX.toString();
        assertNotNull(s);
    }

    @Test
    void test500kYears() {
        Zeitpunkt z = Zeitpunkt.of(BigInteger.valueOf(500), Zeiteinheit.MILLENNIA);
        String s = z.toString();
        assertNotNull(s);
        if (Locale.GERMANY.equals(Locale.getDefault())) {
            assertEquals("in 500.000 Jahren", s);
        }
    }

    @Test
    void test5MioYears() {
        Zeitpunkt z = Zeitpunkt.of(BigInteger.valueOf(5000), Zeiteinheit.MILLENNIA);
        String s = z.toString();
        assertNotNull(s);
        if (Locale.GERMANY.equals(Locale.getDefault())) {
            assertEquals("in 5 Mio. Jahren", s);
        }
    }

    @Test
    void isBefore() {
        Zeitpunkt t1 = Zeitpunkt.of(1, Zeiteinheit.NANOSECONDS);
        Zeitpunkt t2 = Zeitpunkt.of(2, Zeiteinheit.NANOSECONDS);
        assertTrue(t1.isBefore(t2));
        assertFalse(t2.isBefore(t1));
    }

    @Test
    void isAfter() {
        Zeitpunkt t1 = Zeitpunkt.of(1, Zeiteinheit.MILLISECONDS);
        Zeitpunkt t2 = Zeitpunkt.of(2, Zeiteinheit.MILLISECONDS);
        assertFalse(t1.isAfter(t2));
        assertTrue(t2.isAfter(t1));
    }

    @Test
    void testCompareTo() {
        Zeitpunkt t0 = Zeitpunkt.EPOCH;
        Zeitpunkt t1 = Zeitpunkt.of(1, Zeiteinheit.NANOSECONDS);
        assertThat(t0.compareTo(t1), lessThan(0));
        assertThat(t1.compareTo(t0), greaterThan(0));
        assertEquals(0, t0.compareTo(Zeitpunkt.EPOCH));
    }

}