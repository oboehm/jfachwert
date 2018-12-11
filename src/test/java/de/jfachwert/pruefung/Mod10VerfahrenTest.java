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
 * (c)reated 11.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import de.jfachwert.PruefzifferVerfahren;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit-Tests fuer {@link Mod10Verfahren}-Klasse. Die Ergebnisse wurden dabei
 * mit http://www.ee.unb.ca/cgi-bin/tervo/luhn.pl ueberprueft.
 *
 * @author oboehm
 */
public final class Mod10VerfahrenTest extends AbstractPruefzifferVerfahrenTest<Integer> {

    private static final PruefzifferVerfahren<Integer> MOD10 = new Mod10Verfahren();

    /**
     * Hierueber wird das Pruefziffer-Verfahren fuer den Test erwartet.
     *
     * @return Pruefzifferverfahren zum Testen
     */
    protected PruefzifferVerfahren<Integer> getPruefzifferVerfahren() {
        return MOD10;
    }

    /**
     * Zum Testen des Pruefzifferverfahrens brauchen wir einen Wert, der
     * gueltig sein sollte.
     *
     * @return ein gueltiger Wert
     */
    @Override
    protected Integer getValidWert() {
        return 260326822;
    }

    @Test
    public void testIsInvalid() {
        assertFalse(MOD10.isValid(500));
    }

    @Test
    public void testPruefzifferOnly() {
        assertFalse(MOD10.isValid(5));
    }

    /**
     * Dieser Testfalls stammt aus der Pruefung der KassenkIK mit der IK-Nummer
     * "108018132" (AOK Baden Wuerttemberg).
     */
    @Test
    public void testGetPruefziffer() {
        int nummer = 8018132;
        assertEquals(2, MOD10.getPruefziffer(nummer).intValue());
        assertTrue(MOD10.isValid(nummer));
    }

}
