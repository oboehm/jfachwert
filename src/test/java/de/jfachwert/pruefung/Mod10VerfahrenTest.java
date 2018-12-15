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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link Mod10Verfahren}-Klasse.
 *
 * @author oboehm
 * @since x.x (15.12.2018)
 */
public class Mod10VerfahrenTest {

    @Test
    public void isValid() {
        Mod10Verfahren leitcode = new Mod10Verfahren(4, 9);
        assertTrue(leitcode.isValid("23669012012305"));
    }

}
