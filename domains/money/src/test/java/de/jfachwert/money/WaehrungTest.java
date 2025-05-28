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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 04.08.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.money;

import de.jfachwert.FachwertTest;
import de.jfachwert.KSimpleValidator;
import org.javamoney.tck.TestUtils;
import org.javamoney.tck.tests.internal.TestCurrencyUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import patterntesting.runtime.annotation.RunTestOn;
import patterntesting.runtime.junit.ObjectTester;
import patterntesting.runtime.junit.extension.SmokeTestExtension;

import javax.money.CurrencyUnit;
import javax.money.UnknownCurrencyException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link Waehrung}-Klasse.
 */
@ExtendWith(SmokeTestExtension.class)
class WaehrungTest extends FachwertTest {

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden. Und zwar muss jedesmal
     * der gleiche Fachwert erzeugt werden, weil sonst der equals-Test nicht
     * funktioniert.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Waehrung createFachwert() {
        return Waehrung.of("EUR");
    }

    @Test
    public void testWaehrungInvalid() {
        assertThrows(UnknownCurrencyException.class, () -> new Waehrung("Taler"));
    }

    /**
     * Hier wird {@link Waehrung#compareTo(CurrencyUnit)} ueberprueft.
     */
    @Test
    public void testCompareTo() {
        Waehrung one = Waehrung.of("CHF");
        Waehrung anotherOne = Waehrung.of("CHF");
        ObjectTester.assertEquals(one, anotherOne);
        Waehrung two = Waehrung.of("EUR");
        ObjectTester.assertNotEquals(one, two);
    }

    @Test
    public void testOf() {
        CurrencyUnit cu = new TestCurrencyUnit("AUD");
        Waehrung aud = Waehrung.of(cu);
        assertEquals(0, aud.compareTo(cu));
    }

    @Test
    public void testOfCaching() {
        Currency cu = Currency.getInstance("EUR");
        Waehrung w1 = Waehrung.of(cu);
        Waehrung w2 = Waehrung.of(cu);
        assertSame(w1, w2);
    }

    @Test
    public void testOfEuroMitBetrag() {
        Waehrung euro = Waehrung.of("EUR10.50");
        assertEquals(Waehrung.EUR, euro);
    }

    @Test
    public void testOfUnknownCurrency() {
        assertThrows(UnknownCurrencyException.class, () -> Waehrung.of("shj"));
    }

    @Test
    public void testToCurrency() {
        Currency eur = Currency.getInstance("GBP");
        assertEquals(eur, Waehrung.toCurrency(eur.getSymbol()));
    }

    @Test
    public void testToCurrencyEurozeichen() {
        Currency eur = Currency.getInstance("EUR");
        assertEquals(eur, Waehrung.toCurrency("\u20ac"));
    }

    @Test
    public void testValidate() {
        KSimpleValidator<String> validator = new Waehrung.Validator();
        Object validated = validator.validateObject("EUR");
        assertEquals("EUR", validated);
    }

    @Test
    public void testGetCurrency() {
        Waehrung euro = Waehrung.of("EUR");
        assertEquals(euro.getCurrencyCode(), euro.getCurrency().getCurrencyCode());
    }

    /**
     * Zum Testen erzeugen wir hier zwei gleiche, aber nicht diesselben
     * Strings. Daraus sollten zwei gleiche Fachwerte mit demselben internen
     * Code erzeugt werden.
     */
    @Test
    public void testNoDuplicate() {
        Waehrung w1 = Waehrung.of("EUR");
        Waehrung w2 = Waehrung.of("EUR");
        assertSame(w1.getCode(), w2.getCode());
        assertSame(w1, w2);
    }

    /**
     * Hier verwenden wir das TCK zum Testen, ob Waehrung Immutable ist.
     * Dieser Test fuehrte (im Gegensatz zum ImmutableTester) zu einem
     * Fehler. Dies lag aber an der verwendeten MutabilityDetector-Lib,
     * deren Version inzwischen angehoben wurde. Damit trat dieser
     * Fehler nicht mehr auf.
     */
    @Test
    @RunTestOn(javaVersion = "11")
    public void testImmutableWithTck() {
        TestUtils.testImmutable("4.2.1", Waehrung.class);
    }

}
