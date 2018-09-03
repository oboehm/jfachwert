/*
 * Copyright (c) 2017 by Oliver Boehm
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
 * (c)reated 06.07.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Fachwert;
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link Bankverbindung}-Klasse.
 */
public final class BankverbindungTest extends AbstractFachwertTest {

    @Override
    protected Fachwert createFachwert() {
        return new Bankverbindung("Max Muster", new IBAN("DE41300606010006605605"), new BIC("GENODEF1JEV"));
    }

    /**
     * Hier testen wir die equals-Methode mit einer Bankverbindung ohne BIC.
     */
    @Test
    public void testEqualsOhneBic() {
        Bankverbindung one = new Bankverbindung("Ohne Bic", new IBAN("DE41300606010006605605"));
        Bankverbindung anotherOne = new Bankverbindung("Ohne Bic", new IBAN("DE41300606010006605605"));
        ObjectTester.assertEquals(one, anotherOne);
    }

    /**
     * Test-Methode fuer {@link Bankverbindung#Bankverbindung(String)} (Gut-Fall).
     */
    @Test
    public void testBankverbindungString() {
        Bankverbindung bankverbindung = new Bankverbindung("Max Muster, IBAN DE41300606010006605605");
        assertEquals("Max Muster", bankverbindung.getKontoinhaber());
        assertEquals(new IBAN("DE41300606010006605605"), bankverbindung.getIban());
        assertFalse("no BIC expected for " + bankverbindung, bankverbindung.getBic().isPresent());
    }

    /**
     * Test-Methode fuer {@link Bankverbindung#Bankverbindung(String)} (Gut-Fall).
     */
    @Test
    public void testBankverbindungStringMitBIC() {
        Bankverbindung bankverbindung = new Bankverbindung("Max Muster, IBAN DE41300606010006605605, BIC GENODEF1JEV");
        assertEquals(new BIC("GENODEF1JEV"), bankverbindung.getBic().get());
    }

    /**
     * Test-Methode fuer {@link Bankverbindung#Bankverbindung(String)} (Fehler-Fall).
     */
    @Test
    public void testBankverbindungFehler() {
        try {
            new Bankverbindung("Max Muster");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            String message = expected.getLocalizedMessage();
            assertThat(message, containsString("Max Muster"));
        }
    }

}
