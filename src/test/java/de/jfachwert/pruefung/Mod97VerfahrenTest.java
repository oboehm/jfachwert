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
import org.junit.Test;

import javax.validation.ValidationException;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Test fuer {@link Mod97Verfahren}-Klasse.
 *
 * @author oboehm
 */
public final class Mod97VerfahrenTest extends AbstractPruefzifferVerfahrenTest<String> {

    private static final PruefzifferVerfahren<String> MOD97 = Mod97Verfahren.getInstance();
    private final String iban = getValidWert();

    /**
     * Hierueber liefern wird das Modulo-97-Verfahren zum Testen.
     *
     * @return Pruefzifferverfahren zum Testen
     */
    protected PruefzifferVerfahren<String> getPruefzifferVerfahren() {
        return MOD97;
    }

    /**
     * Zum Testen des Pruefzifferverfahrens liefern wir eine gueltige IBAN.
     *
     * @return ein gueltiger Wert
     */
    protected String getValidWert() {
        return "DE68210501700012345678";
    }

    /**
     * Test-Methode fuer {@link Mod97Verfahren#getPruefziffer(String)}.
     */
    @Test
    public void getPruefziffer() {
        assertEquals("68", MOD97.getPruefziffer(iban));
    }

    /**
     * Test-Methode fuer {@link Mod97Verfahren#getPruefziffer(String)}.
     */
    @Test
    public void berechnePruefziffer() {
        assertEquals("68", MOD97.berechnePruefziffer(iban));
    }

    /**
     * Testmethode fuer {@link Mod97Verfahren#validate(Serializable)}.
     */
    @Test(expected = ValidationException.class)
    public void testValidateWithException() {
        MOD97.validate("DE99210501700012345678");
    }

}
