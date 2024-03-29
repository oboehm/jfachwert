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

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link Kundennummer}-Klasse.
 *
 * @author oboehm
 */
public class KundennummerTest extends AbstractFachwertTest<String, Text> {

    /**
     * Erzeugt eine Test-Kundennumer.
     *
     * @param nr z.B. "100.059" (aus einer Musterrechnung)
     * @return Test-Kundennummer
     */
    @Override
    protected Kundennummer createFachwert(String nr) {
        return Kundennummer.of(nr);
    }

    /**
     * Eine leere Kundennummer macht keinen Sinn und sollte deswegen nicht
     * angelegt werden koennen.
     */
    @Test
    public void testNotEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Kundennummer.of(""));
    }

}
