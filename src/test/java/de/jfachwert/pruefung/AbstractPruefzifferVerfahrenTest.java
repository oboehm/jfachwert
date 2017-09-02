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
 * (c)reated 22.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.pruefung;

import de.jfachwert.PruefzifferVerfahren;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

/**
 * In AbstractPruefzifferVerfahrenTest sind die Gemeinsamkeiten fuer den Test
 * der verschiedenen Pruefziffer-Verfahren zusammengefasst.
 *
 * @author oboehm
 */
public abstract class AbstractPruefzifferVerfahrenTest<T extends Serializable> {

    private PruefzifferVerfahren<T> verfahren;
    private T wert;

    /**
     * Hierueber wird das Pruefziffer-Verfahren fuer den Test erwartet.
     *
     * @return Pruefzifferverfahren zum Testen
     */
    protected abstract PruefzifferVerfahren<T> getPruefzifferVerfahren();

    /**
     * Zum Testen des Pruefzifferverfahrens brauchen wir einen Wert, der
     * gueltig sein sollte.
     *
     * @return ein gueltiger Wert
     */
    protected abstract T getValidWert();

    /**
     * Hier setzen wir das Verfahren und den Wert zum Testen auf.
     */
    @Before
    public void setUpPruefzifferVerfahren() {
        verfahren = getPruefzifferVerfahren();
        wert = getValidWert();
    }

    /**
     * Testmethode fuer {@link PruefzifferVerfahren#isValid(Serializable)}.
     */
    @Test
    public void isValid() {
        assertTrue("should be valid: " + wert, verfahren.isValid(wert));
    }

    /**
     * Testmethode fuer {@link PruefzifferVerfahren#validate(Serializable)}.
     */
    @Test
    public void testValidate() {
        verfahren.validate(wert);
    }

}
