/*
 * Copyright 2021 Optica Abrechnungszentrum Dr. Gueldener GmbH
 */
package de.jfachwert.bank.internal;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link Zahlenwert}.
 *
 * @author oboehm
 * @since 4.0 (02.05.21)
 */
public final class ZahlenwertTest {

    @Test
    public void testIntValue() {
        BigDecimal expected = BigDecimal.valueOf(0.5);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.intValue(), zahlenwert.intValue());
    }

    @Test
    public void testIntValueExact() {
        BigDecimal expected = BigDecimal.valueOf(0x12345678L);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.intValueExact(), zahlenwert.intValueExact());
    }

    @Test
    public void testLongValue() {
        BigDecimal expected = BigDecimal.valueOf(1234567890.0987654321);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.longValue(), zahlenwert.longValue());
    }

    @Test
    public void testLongValueExact() {
        BigDecimal expected = BigDecimal.valueOf(0x1234567890L);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.longValueExact(), zahlenwert.longValueExact());
    }

    @Test
    public void testFloat() {
        BigDecimal expected = BigDecimal.valueOf(1.23F);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.floatValue(), zahlenwert.floatValue(), 0.0001);
    }

    @Test
    public void testDouble() {
        BigDecimal expected = BigDecimal.valueOf(9876.54321);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.doubleValue(), zahlenwert.doubleValue(), 0.0001);
    }

    @Test
    public void testDoubleValueExact() {
        BigDecimal expected = BigDecimal.valueOf(1234567.89);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.doubleValue(), zahlenwert.doubleValueExact(), 0.0001);
    }

    @Test
    public void testGetScale() {
        BigDecimal expected = BigDecimal.valueOf(1.25);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.scale(), zahlenwert.getScale());
    }

    @Test
    public void testGetPrecision() {
        BigDecimal expected = BigDecimal.valueOf(1.25);
        Zahlenwert zahlenwert = new Zahlenwert(expected);
        assertEquals(expected.precision(), zahlenwert.getPrecision());
    }

}
