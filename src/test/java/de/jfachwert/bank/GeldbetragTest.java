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
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryException;
import javax.money.NumberValue;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.Currency;

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
    
    private static final GeldbetragFactory FACTORY = new GeldbetragFactory();

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
     * Hier wird getestet, ob die Waehrung richtig erkannt wird.
     */
    @Test
    public void testGeldbetragString() {
        Geldbetrag fuffzger = new Geldbetrag("50 CHF");
        assertEquals(Geldbetrag.valueOf(BigDecimal.valueOf(50), Currency.getInstance("CHF")), fuffzger);
    }

    /**
     * Hier wird getestet, ob die Waehrung richtig erkannt wird.
     */
    @Test
    public void testGeldbetragEuro() {
        Geldbetrag fuffzger = new Geldbetrag("50\u20AC");
        assertEquals(Geldbetrag.valueOf(BigDecimal.valueOf(50), Currency.getInstance("EUR")), fuffzger);
    }

    /**
     * Rundungsdifferenzen beim Vergleich im Millionstel-Cent-Bereich sollten
     * keine Rolle fuer den Vergleich spielen.
     */
    @Test
    public void testEqualsGerundet() {
        Geldbetrag one = new Geldbetrag(1);
        Geldbetrag hundredCents = new Geldbetrag(0.9999999999999999);
        assertEquals(one, hundredCents);
    }

    /**
     * Bei 0 sollte das Vorzeichen keine Rolle spielen.
     */
    @Test
    public void testEqualsZero() {
        Geldbetrag plus = new Geldbetrag("0.0000000000000049");
        Geldbetrag minus = new Geldbetrag("-0.0000000000000049");
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
    public void testIsEqualsToDifferentCurrencies() {
        Geldbetrag oneEuro = new Geldbetrag(1.0);
        Geldbetrag oneDM = new Geldbetrag(1.0).withCurrency("DEM");
        oneEuro.isEqualTo(oneDM);
    }

    /**
     * Rundungsdifferenzen spielen fuer das TCK eine Rolle.
     */
    @Test
    public void testIsEqualsToRounding() {
        Geldbetrag zero = FACTORY.setNumber(BigDecimal.valueOf(0d)).create();
        Geldbetrag fastNix = FACTORY.setNumber(BigDecimal.valueOf(0.00001d)).create();
        assertFalse(zero.isEqualTo(fastNix));
        assertFalse(fastNix.isEqualTo(zero));
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
        Geldbetrag base = new Geldbetrag("12.3456 CHF");
        Geldbetrag one = new Geldbetrag("1 CHF");
        Geldbetrag sum = base.add(one);
        assertNotSame(base, sum);
        assertNotSame(one, sum);
        assertEquals(Geldbetrag.valueOf("13.3456 CHF"), sum);
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
        assertEquals(Waehrung.DEFAULT_CURRENCY.toString(), Geldbetrag.ZERO.getCurrency().getCurrencyCode());
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
     * Test der verschiedenen Multiplikationsmethoden.
     */
    @Test
    public void testMultiply() {
        Geldbetrag einEuroFunefzig = Geldbetrag.valueOf("1.50 EUR");
        Geldbetrag dreiEuro = Geldbetrag.valueOf("3 EUR");
        assertEquals(dreiEuro, einEuroFunefzig.multiply(2));
        assertEquals(dreiEuro, einEuroFunefzig.multiply(2.0));
        assertEquals(dreiEuro, einEuroFunefzig.multiply(BigDecimal.valueOf(2)));
    }

    /**
     * Ueberpruefung der Mulitipliation anhand der Mehrwersteuerberechnung.
     */
    @Test
    public void testMultiplyMwst() {
        Geldbetrag fuffzger = Geldbetrag.fromCent(500);
        BigDecimal mwst = BigDecimal.valueOf(0.19);
        assertEquals(Geldbetrag.fromCent(95), fuffzger.multiply(mwst));
    }

    /**
     * Test fuer die verschieden divide-Methoden.
     */
    @Test
    public void testDivide() {
        Geldbetrag oneEuro = Geldbetrag.valueOf("1 EUR");
        Geldbetrag fiftyCent = Geldbetrag.fromCent(50);
        assertEquals(fiftyCent, oneEuro.divide(2));
        assertEquals(fiftyCent, oneEuro.divide(BigDecimal.valueOf(2)));
        assertEquals(fiftyCent, oneEuro.divide(2.0));
    }

    /**
     * Pruefung der Division.
     */
    @Test
    public void testDivideEinCent() {
        Geldbetrag einCent = Geldbetrag.fromCent(1);
        Geldbetrag halberCent = einCent.divide(BigDecimal.valueOf(2.0));
        assertEquals(0.005, halberCent.doubleValue(), 0.0001);
        assertEquals(einCent, halberCent.add(halberCent));
    }

    /**
     * Testmethode fuer {@link Geldbetrag#divideAndRemainder(long)}.
     */
    @Test
    public void testDivideAndReminder() {
        Geldbetrag betrag = Geldbetrag.valueOf("7 USD");
        MonetaryAmount[] amounts = betrag.divideAndRemainder(2);
        assertEquals(betrag.divideToIntegralValue(2), amounts[0]);
        assertEquals(betrag.remainder(2), amounts[1]);
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
    public void testAbsNegativ() {
        Geldbetrag schulden = new Geldbetrag(-10);
        assertEquals(10, schulden.abs().getNumber().intValueExact());
    }

    /**
     * Test der {@link Geldbetrag#abs()}-Funktion.
     */
    @Test
    public void testAbsPosititv() {
        Geldbetrag guthaben = new Geldbetrag(10);
        assertEquals(guthaben, guthaben.abs());
    }

    /**
     * Testmethode fuer {@link Geldbetrag#plus()}.
     */
    @Test
    public void testPlus() {
        Geldbetrag plus = Geldbetrag.valueOf(56.78);
        Geldbetrag minus = plus.negate();
        assertEquals(plus, plus.plus());
        assertEquals(plus, minus.plus());
    }

    /**
     * Testmethode fuer {@link Geldbetrag#signum()}.
     */
    @Test
    public void testSignum() {
        assertEquals(0, Geldbetrag.ZERO.signum());
        assertEquals(1, Geldbetrag.valueOf(0.01).signum());
        assertEquals(-1, Geldbetrag.valueOf(-0.01).signum());
    }

    /**
     * Testmethode fuer {@link Geldbetrag#getFactory()}.
     */
    @Test
    public void testGetFactory() {
        GeldbetragFactory factory = Geldbetrag.ZERO.getFactory();
        assertNotNull(factory);
    }

    /**
     * Testmethode fuer {@link Geldbetrag#getContext()}.
     */
    @Test
    public void testGetContext() {
        MonetaryContext context = Geldbetrag.valueOf("1 AUD").getContext();
        assertNotNull(context);
        assertEquals(Geldbetrag.class, context.getAmountType());
    }

    /**
     * Hier testen wir, ob intern tatsaechlich die eingestellte Genauigekit
     * eingehalten wird.
     */
    @Test
    public void testTruncation() {
        BigDecimal expected = BigDecimal.valueOf(-1.1);
        Geldbetrag betrag = new GeldbetragFactory().setNumber(expected).create();
        BigDecimal number = (BigDecimal) betrag.getNumber().numberValue(BigDecimal.class); 
        assertEquals(expected, number.stripTrailingZeros());
    }

}
