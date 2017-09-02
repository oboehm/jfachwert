package de.jfachwert.rechnung;/*
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 10.07.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.*;
import org.junit.*;

import javax.validation.*;

/**
 * Unit-Tests fuer {@link Kundennummer}-Klasse.
 *
 * @author oboehm
 */
public class KundennummerTest extends AbstractFachwertTest {

    /**
     * Die Test-Kundennummer stammmt aus
     * https://www.stadtwerke-schwabach.de/Fuer-Kunden/Musterrechnung/Musterrechnung/Musterrechnung-2015-2-.pdf
     * .
     *
     * @return Test-Kundennummer
     */
    @Override
    protected Fachwert createFachwert() {
        return new Kundennummer("100.059");
    }

    /**
     * Eine leere Kundennummer macht keinen Sinn und sollte deswegen nicht
     * angelegt werden koennen.
     */
    @Test(expected = ValidationException.class)
    public void testNotEmpty() {
        new Kundennummer("");
    }

}
