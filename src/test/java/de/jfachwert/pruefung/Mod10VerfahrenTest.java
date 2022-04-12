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
 * (c)reated 15.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link Mod10Verfahren}-Klasse. Die Beispiele dazu stammen
 * aus https://www.activebarcode.de/codes/checkdigit/modulo10.html.
 *
 * @author oboehm
 * @since 1.1 (15.12.2018)
 */
public class Mod10VerfahrenTest {

    @Test
    public void testEAN13() {
        assertTrue(Mod10Verfahren.EAN13.isValid("4007630000116"));
    }

    @Test
    public void testCode25() {
        assertTrue(Mod10Verfahren.CODE25.isValid("123457"));
    }

    @Test
    public void testLeitcode() {
        assertTrue(Mod10Verfahren.LEITCODE.isValid("23669012012305"));
    }

}
