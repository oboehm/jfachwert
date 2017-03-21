package de.jfachwert.pruefung;/*
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
 * (c)reated 21.03.2017 by oboehm (ob@jfachwert.de)
 */

import de.jfachwert.PruefzifferVerfahren;
import de.jfachwert.bank.IBAN;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit-Test fuer {@link Modulo97Verfahren-Klasse.
 *
 * @author oboehm
 */
public final class Modulo97VerfahrenTest {

    private static final PruefzifferVerfahren<String> MODULO97 = Modulo97Verfahren.getInstance();
    private final IBAN iban = new IBAN("DE68 2105 0170 0012 3456 78");

    /**
     * Test-Methode fuer {@link Modulo97Verfahren#getPruefziffer(String)}.
     */
    @Test
    public void getPruefziffer() {
        assertEquals("68", MODULO97.getPruefziffer(iban.getUnformatted()));
    }

    /**
     * Test-Methode fuer {@link Modulo97Verfahren#isValid(String)}.
     */
    @Test
    public void isValid() throws Exception {
        assertTrue("should be valid: " + iban, MODULO97.isValid(iban.getUnformatted()));
    }

    /**
     * Test-Methode fuer {@link Modulo97Verfahren#getPruefziffer(String)}.
     */
    @Test
    public void berechnePruefziffer() throws Exception {
        assertEquals("68", MODULO97.berechnePruefziffer(iban.getUnformatted()));
    }

}
