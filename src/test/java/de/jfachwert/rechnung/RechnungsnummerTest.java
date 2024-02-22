/*
 * Copyright (c) 2017-2022 by Oliver Boehm
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
 * (c)reated 10.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.rechnung;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Text;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link Rechnungsnummer}-Klasse.
 *
 * @author oboehm
 */
public class RechnungsnummerTest extends AbstractFachwertTest<String, Text> {

    /**
     * Erzeugt eine Test-Rechnungsnummer.
     *
     * @param nr z.B. "000002835042" (aus einer Musterrechnung)
     * @return Test-Rechnungsnummer
     */
    @Override
    protected Rechnungsnummer createFachwert(String nr) {
        return Rechnungsnummer.of(nr);
    }

    /**
     * Eine leere Rechnungsnummer macht keinen Sinn und sollte deswegen nicht
     * angelegt werden koennen.
     */
    @Test
    public void testNotEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Rechnungsnummer.of(""));
    }

    /**
     * Testfall fuer/aus Issue #22.
     */
    @Test
    public void testToLong() {
        Rechnungsnummer nr = Rechnungsnummer.of(4711);
        assertEquals(4711, nr.toLong());
    }

}
