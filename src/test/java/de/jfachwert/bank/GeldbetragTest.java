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
 * (c)reated 18.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Ignore;
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import javax.money.MonetaryAmount;
import javax.money.MonetaryException;
import javax.money.NumberValue;
import javax.validation.ValidationException;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link Geldbetrag}-Klasse.
 *
 * @author oboehm
 */
public final class GeldbetragTest extends AbstractFachwertTest {

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden. Und zwar muss jedesmal
     * der gleiche Fachwert erzeugt werden, weil sonst der equals-Test nicht
     * funktioniert.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Geldbetrag createFachwert() {
        return new Geldbetrag(BigDecimal.ONE);
    }

    /**
     * Illegale Betraege sollte nicht akzeptiert werden.
     */
    @Test(expected = ValidationException.class)
    public void testInvalidGeldbetrag() {
        new Geldbetrag("falscher Fuffzger");
    }

    /**
     * Rundungsdifferenzen beim Vergleich im 1/10-Cent-Bereich sollten keine
     * Rolle fuer den Vergleich spielen.
     */
    @Test
    public void testEqualsGerundet() {
        Geldbetrag one = new Geldbetrag(1);
        Geldbetrag hundredCents = new Geldbetrag(0.9999);
        assertEquals(one, hundredCents);
    }

    /**
     * Bei 0 sollte das Vorzeichen keine Rolle spielen.
     */
    @Test
    public void testEqualsZero() {
        Geldbetrag plus = new Geldbetrag("0.0049");
        Geldbetrag minus = new Geldbetrag("-0.0049");
        assertEquals(plus, minus);
    }

    /**
     * Gleiche Betraege, aber mit unterschiedlichen Waehrungen, sind nicht
     * gleich. Dies wird hier getestet.
     */
    @Test
    public void testNotEquals() {
        Geldbetrag one = new Geldbetrag(1.0);
        Geldbetrag anotherOne = new Geldbetrag(1.0);
        Geldbetrag oneDM = new Geldbetrag(1.0).withCurrency("DEM");
        ObjectTester.assertEquals(one, anotherOne);
        assertNotEquals(one, oneDM);
    }

    /**
     * Betraege mit unterschiedlichen Waehrungen sollten bei
     * {@link javax.money.MonetaryAmount#isEqualTo(MonetaryAmount)} zu einer
     * {@link MonetaryException} fuehren.
     */
    @Test(expected = MonetaryException.class)
    public void testIsEqualsTo() {
        Geldbetrag oneEuro = new Geldbetrag(1.0);
        Geldbetrag oneDM = new Geldbetrag(1.0).withCurrency("DEM");
        oneEuro.isEqualTo(oneDM);
    }

    /**
     * Die Addition von 0 sollte wieder den Betrag selber ergeben.
     * Dieser Test entstand vor der Implementierung auf dem Hackergarden
     * Stuttgart am 19. Juli 2018. 
     */
    @Test
    public void testAddZero() {
        Geldbetrag base = new Geldbetrag(11L);
        Geldbetrag sum = base.add(Geldbetrag.ZERO);
        assertEquals("Summe von x + 0 sollte x sein", base, sum);
    }

    /**
     * Um die Anzahl an Objekte gering zu halten, sollten bei der Addition
     * von 0 keine neuen Objekte entstehen.
     */
    @Test
    public void testAddZeroSameBetrag() {
        Geldbetrag betrag = new Geldbetrag(47.11);
        assertSame(betrag, betrag.add(Geldbetrag.ZERO));
        assertSame(betrag, Geldbetrag.ZERO.add(betrag));
    }

    @Test
    public void testAddOperationLeadsToNewObject() {
        Geldbetrag base = new Geldbetrag("12.3456");
        Geldbetrag one = new Geldbetrag(1L);
        Geldbetrag sum = base.add(one);
        assertNotSame(base, sum);
        assertNotSame(one, sum);
        assertEquals(Geldbetrag.valueOf("13.3456"), sum);
    }

    /**
     * Laut API-Doc sollte bei unterschiedlichen Waehrungen eine
     * {@link MonetaryException} geworfen werden.
     * 
     */
    @Test(expected = MonetaryException.class)
    public void testAddWithDifferentCurrency() {
        Geldbetrag oneEuro = new Geldbetrag(1.0).withCurrency("EUR");
        Geldbetrag oneDM = new Geldbetrag(1.0).withCurrency("DEM");
        oneEuro.add(oneDM);
    }
    
    @Test
    public void testAddKleineBetraege() {
        Geldbetrag a = new Geldbetrag(0.004);
        Geldbetrag b = new Geldbetrag(0.006);
        assertEquals(new Geldbetrag(0.01), a.add(b));
    }

    @Test(expected = ValidationException.class)
    public void testPrecision() {
        new Geldbetrag(0.00001);
    }

    @Test
    @Ignore // Ergebnis noch unklar
    public void testPrecisionOfFiveZerosAfterComma() {
        new Geldbetrag(new BigDecimal("3.00000"));
    }

    @Test(expected = ValidationException.class)
    public void testPrecisionOfZeroInFifthAfterCommaPosition() {
        new Geldbetrag(new BigDecimal("0.00010"));
    }
    /**
     * Test-Methode fuer {@link Geldbetrag#subtract(MonetaryAmount)}.
     */

    @Test
    public void testSubtract() {
        MonetaryAmount guthaben = new Geldbetrag(42);
        MonetaryAmount schulden = new Geldbetrag(50);
        assertEquals(new Geldbetrag(-8), guthaben.subtract(schulden));
    }

    /**
     * Hier testen wir die Rundung, indem wir 2x einen kleinen Betrag abziehen.
     */
    @Test
    public void testSubtractKleinerBetrag() {
        Geldbetrag einEuro = new Geldbetrag(1);
        Geldbetrag zinsen = einEuro.divide(200);
        assertEquals(Geldbetrag.valueOf("0.99"), einEuro.subtract(zinsen).subtract(zinsen));
    }

    /**
     * Hier sollte eigentlich in den allermeisten Faellen "EUR" zurueckgegeben
     * werden. Falls nicht, stimmt die Locale nicht.
     */
    @Test
    public void testGetCurrency() {
        assertEquals(Geldbetrag.getDefaultCurrency().toString(), Geldbetrag.ZERO.getCurrency().getCurrencyCode());
    }

    /**
     * Manchmal muessen wir auch die Waehrung setzen koennen muessen. Dies
     * erfolgt ueber {@link Geldbetrag#withCurrency(String)}.
     */
    @Test
    public void testWithCurrencyEUR() {
        Geldbetrag betrag = Geldbetrag.ZERO.withCurrency("EUR");
        assertEquals("EUR", betrag.getCurrency().getCurrencyCode());
    }

    /**
     * Manchmal brauchen wir vielleicht noch die gute alte DM.
     */
    @Test
    public void testWithCurrencyDM() {
        Geldbetrag betrag = Geldbetrag.ZERO.withCurrency("DM");
        assertEquals("DEM", betrag.getCurrency().getCurrencyCode());
    }

    /**
     * Testmethode fuer {@link Geldbetrag#fromCent(long)}.
     */
    @Test
    public void testFromCent() {
        assertEquals(Geldbetrag.fromCent(52), Geldbetrag.valueOf(0.52));
    }

    /**
     * Testmethode fuer {@link Geldbetrag#isGreaterThan(MonetaryAmount)} und
     * {@link Geldbetrag#isGreaterThanOrEqualTo(MonetaryAmount)}.
     */
    @Test
    public void testIsGreaterThan() {
        Geldbetrag oneCent = Geldbetrag.fromCent(1);
        Geldbetrag anotherCent = Geldbetrag.fromCent(1);
        Geldbetrag twoCent = Geldbetrag.fromCent(2);
        assertThat(oneCent.isGreaterThan(twoCent), is(Boolean.FALSE));
        assertThat(twoCent.isGreaterThan(oneCent), is(Boolean.TRUE));
        assertThat(oneCent.isGreaterThan(anotherCent), is(Boolean.FALSE));
        assertThat(oneCent.isGreaterThanOrEqualTo(anotherCent), is(Boolean.TRUE));
    }

    /**
     * Testmethode fuer {@link Geldbetrag#isGreaterThan(MonetaryAmount)} und
     * {@link Geldbetrag#isGreaterThanOrEqualTo(MonetaryAmount)}.
     */
    @Test
    public void testIsLessThan() {
        Geldbetrag oneCent = Geldbetrag.fromCent(1);
        Geldbetrag anotherCent = Geldbetrag.fromCent(1);
        Geldbetrag twoCent = Geldbetrag.fromCent(2);
        assertThat(oneCent.isLessThan(twoCent), is(Boolean.TRUE));
        assertThat(twoCent.isLessThan(oneCent), is(Boolean.FALSE));
        assertThat(oneCent.isLessThan(anotherCent), is(Boolean.FALSE));
        assertThat(oneCent.isLessThanOrEqualTo(anotherCent), is(Boolean.TRUE));
    }

    /**
     * Ueberpruefung der Mulitipliation anhand der Mehrwersteuerberechnung.
     */
    @Test
    public void testMultiplyMwst() {
        Geldbetrag fuffzger = Geldbetrag.fromCent(50);
        BigDecimal mwst = BigDecimal.valueOf(0.19);
        assertEquals(Geldbetrag.fromCent(10), fuffzger.multiply(mwst));
    }

    /**
     * Pruefung der Division.
     */
    @Test
    public void testDivide() {
        Geldbetrag einCent = Geldbetrag.fromCent(1);
        Geldbetrag halberCent = einCent.divide(BigDecimal.valueOf(2.0));
        assertEquals(0.005, halberCent.doubleValue(), 0.0001);
        assertEquals(einCent, halberCent.add(halberCent));
    }

    /**
     * Test-Methode fuer {@link Geldbetrag#compareTo(MonetaryAmount)}.
     */
    @Test
    public void testCompareToEquals() {
        Geldbetrag einEuro = new Geldbetrag(1);
        Geldbetrag hundertCent = Geldbetrag.fromCent(100);
        assertEquals(einEuro, hundertCent);
        assertEquals(0, einEuro.compareTo(hundertCent));
    }

    /**
     * Test-Methode fuer {@link Geldbetrag#compareTo(MonetaryAmount)}.
     */
    @Test
    public void testCompareTo() {
        Geldbetrag fiftyCents = Geldbetrag.fromCent(50);
        Geldbetrag fiftyoneCents = Geldbetrag.fromCent(51);
        assertThat(fiftyoneCents.compareTo(fiftyCents), greaterThan(0));
        assertThat(fiftyCents.compareTo(fiftyoneCents), lessThan(0));
    }

    /**
     * Test-Methode fuer {@link Geldbetrag#compareTo(Number)}.
     */
    @Test
    public void testCompareToBigDecimal() {
        assertEquals(0, Geldbetrag.ZERO.compareTo(BigDecimal.ZERO));
    }

    /**
     * Hier testen wir die Rundung von {@link Geldbetrag#getNumber()}.
     */
    @Test
    public void testToNumber() {
        Geldbetrag betrag = Geldbetrag.fromCent(1234);
        NumberValue number = betrag.getNumber();
        assertEquals(Geldbetrag.valueOf("12.34"), new Geldbetrag(number));
    }

    /**
     * Test der {@link Geldbetrag#abs()}-Funktion.
     */
    @Test
    public void testAbs() {
        Geldbetrag schulden = new Geldbetrag(-10);
        assertEquals(10, schulden.abs().getNumber().intValueExact());
    }
    
}
