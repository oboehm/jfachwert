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
 * (c)reated 11.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Text;
import org.junit.Test;

import javax.validation.ValidationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit-Tests fuer die {@link BIC}-Klasse.
 */
public final class BICTest extends AbstractFachwertTest<String, Text> {

    private final BIC bic = new BIC("GENODEF1JEV");

    /**
     * Hierueber stellen wir fuer die Oberklasse eine Test-BIC zur Verfuegung.
     *
     * @param code den Code zum Erstellen des Test-Objekts
     * @return Test -Objekt zum Testen
     */
    @Override
    protected BIC createFachwert(String code) {
        return BIC.of(code);
    }

    /**
     * Zum Testen verwenden wir eine Volksbank.
     *
     * @return "GENODEF1JEV"
     */
    @Override
    protected String getCode() {
        return "GENODEF1JEV";
    }

    /**
     * Hier testen wir den Konstruktor mit einer gueltigen BIC.
     */
    @Test
    public void testBICvalid() {
        BIC bic = BIC.of("GENODEF1JEVxxx");
        assertNotNull(bic);
    }

    /**
     * Hier testen wir den Konstruktor mit einer ungueltigen BIC.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBICinvalid() {
        new BIC("GENODEF1J");
    }

    /**
     * Leerzeichen sollen bei der Validierung nicht beruecksichtigt werden.
     */
    @Test(expected = ValidationException.class)
    public void testValidate() {
        new BIC.Validator().validate("GENODEF1J  ");
    }

    /**
     * Die toString-Methode sollte die BIC im Klartext ausgeben.
     */
    @Override
    @Test
    public void testToString() {
        assertEquals("GENODEF1JEV", bic.toString());
    }

}
