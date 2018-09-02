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
 * (c)reated 01.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank;

import org.junit.Before;
import org.junit.Test;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import javax.money.NumberValue;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link GeldbetragFactory}-Klasse.
 *
 * @author oboehm
 */
public final class GeldbetragFactoryTest {
    
    private final GeldbetragFactory factory = new GeldbetragFactory();
    
    @Before
    public void setUpFactory() {
        factory.setCurrency(Waehrung.DEFAULT);
    }

    /**
     * Tested das Anlegen eines Geldbetrags ueber die GeldbetragFactory.
     */
    @Test
    public void testCreate() {
        Geldbetrag zero = factory.create();
        assertEquals(Geldbetrag.ZERO, zero);
    }

    /**
     * Tested das Setzen eines Geldbetrags.
     */
    @Test
    public void testSetNumber() {
        factory.setNumber(1).setCurrency("CHF");
        assertEquals(Geldbetrag.valueOf("1 CHF"), factory.create());
    }
    
    /**
     * Testet das Setzen eines Geldbetrags.
     */
    @Test
    public void testSetNumberDouble() {
        factory.setNumber(0.25).setCurrency("EUR");
        assertEquals(Geldbetrag.valueOf("0.25 EUR"), factory.create());
    }

    /**
     * Falls eine Nummer mit hoeherer Genauigkeit als der Default gesetzt wird,
     * sollte der Context entsprechend angepasst werden.
     */
    @Test
    public void testSetContextByNumber() {
        Geldbetrag betrag = factory.setNumber(0.123456789).setCurrency("EUR").create();
        assertEquals(9, betrag.getContext().getMaxScale());
    }

    /**
     * Hier wird der Min-/Max-Bereich eines Geldbetrags geprueft.
     */
    @Test
    public void testMinMaxNumber() {
        NumberValue minNumber = factory.getMinNumber();
        NumberValue maxNumber = factory.getMaxNumber();
        assertThat(minNumber + " < " + maxNumber, minNumber.compareTo(maxNumber), lessThan(0));
    }

    /**
     * Testmethode fuer {@link GeldbetragFactory#getAmountType()}.
     */
    @Test
    public void testGetAmountType() {
        assertEquals(Geldbetrag.class, factory.getAmountType());
    }

    /**
     * Testmethode fuer {@link GeldbetragFactory#getDefaultMonetaryContext()}.
     */
    @Test
    public void testGetDefaultMonetaryContext() {
        MonetaryContext context = factory.getDefaultMonetaryContext();
        assertNotNull(context);
    }

    /**
     * Hier testen wir das Setzen der Waehrung
     */
    @Test
    public void testSetCurrency() {
        CurrencyUnit cu = Waehrung.of("XXX");
        factory.setCurrency(cu);
        Geldbetrag geldbetrag = factory.create();
        assertEquals(cu, geldbetrag.getCurrency());
    }

    /**
     * Hier testen wir das Setzen des {@link MonetaryContext}es.
     */
    @Test
    public void testSetContext() {
        MonetaryContext context = MonetaryContextBuilder.of(Geldbetrag.class).setPrecision(5).build();
        factory.setContext(context);
        Geldbetrag geldbetrag = factory.create();
        assertEquals(context, geldbetrag.getContext());
    }

}
