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

}
