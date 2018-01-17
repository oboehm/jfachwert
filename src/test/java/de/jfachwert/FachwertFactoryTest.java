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
 * (c)reated 13.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert;

import de.jfachwert.bank.BIC;
import de.jfachwert.bank.IBAN;
import org.junit.Test;

import javax.validation.ValidationException;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link FachwertFactory}-Klasse.
 *
 * @author oboehm
 * @since x.x (13.01.2018)
 */
public class FachwertFactoryTest {
    
    private static final FachwertFactory FACTORY = FachwertFactory.getInstance();

    /**
     * Test-Methode fuer {@link FachwertFactory#getFachwert(Class, Object...)}.
     */
    @Test
    public void getFachwertClass() {
        Fachwert iban = FACTORY.getFachwert(IBAN.class, "DE41300606010006605605");
        assertEquals(new IBAN("DE41300606010006605605"), iban);
    }

    /**
     * Test-Methode fuer {@link FachwertFactory#getFachwert(String, Object...)}
     */
    @Test
    public void getFachwertString() {
        Fachwert bic = FACTORY.getFachwert("BIC", "BELADEBEXXX");
        assertEquals(new BIC("BELADEBEXXX"), bic);
    }

    /**
     * Hier testen wir, ob auch "aehnliche" Namen gefunden werden, falls es den
     * angegebenen Namen nicht gibt. Die Test-BIC ist die Zentrale der
     * Deutschen Bundesbank.
     */
    @Test
    public void getSimilarFachwert() {
        Fachwert bic = FACTORY.getFachwert("BIC1", "MARKDEFF");
        assertEquals(new BIC("MARKDEFF"), bic);
    }

    /**
     * Test-Methode fuer {@link FachwertFactory#validate(Class, Object...)}.
     * Die Test-BIC stammt von der Raiffeisenbank Kitzbuehel.
     */
    @Test
    public void validateClass() {
        FACTORY.validate(BIC.class, "RZTIAT22263");
    }

    /**
     * Test-Methode fuer {@link FachwertFactory#validate(String, Object...)}.
     */
    @Test
    public void validateString() {
        FACTORY.validate("Bankverbindung", "Max Muster", new IBAN("DE41300606010006605605"),
                new BIC("GENODEF1JEV"));
    }
    
    /**
     * Test-Methode fuer {@link FachwertFactory#validate(Class, Object...)}.
     * Die Test-BIC stammt von der Raiffeisenbank Kitzbuehel.
     */
    @Test(expected = ValidationException.class)
    public void validateWithFailure() {
        FACTORY.validate(BIC.class, "AAA");
    }

    /**
     * Wenn ein unbekannter Name angegeben wird, der kaum Aehnlichkeit mit
     * vorhandenen Klasse aufweist, soll auf eine Fallback-Klasse ohne
     * Validierung (Text) zurueckgegriffen werden.
     */
    @Test
    public void validateUnknownName() {
        FACTORY.validate("irgendwas", "42");
    }

}