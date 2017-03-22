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
 * (c)reated 22.03.2017 by oboehm (ob@jfachwert.de)
 */

import de.jfachwert.PruefzifferVerfahren;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit-Test fuer {@link Mod11Verfahren -Klasse.
 *
 * @author oboehm
 */
public final class Mod11VerfahrenTest {

    private static final PruefzifferVerfahren<String> MOD11 = Mod11Verfahren.getInstance();
    private static final String STEUERNUMMER = "12345678000";

    /**
     * Testmethode fuer {@link Mod11Verfahren#getPruefziffer(String)}.
     */
    @Test
    public void getPruefziffer() {
        assertEquals("0", MOD11.getPruefziffer(STEUERNUMMER));
    }

    /**
     * Testmethode fuer {@link Mod11Verfahren#isValid(String)}.
     */
    @Test
    public void isValid() {
        assertTrue("should be valid: " + STEUERNUMMER, MOD11.isValid(STEUERNUMMER));
    }

    /**
     * Testmethode fuer {@link Mod11Verfahren#berechnePruefziffer(String)}.
     */
    @Test
    public void berechnePruefziffer() {
        assertEquals("0", MOD11.berechnePruefziffer(STEUERNUMMER));
    }

}
