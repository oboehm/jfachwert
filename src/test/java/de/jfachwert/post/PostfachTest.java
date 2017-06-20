package de.jfachwert.post;/*
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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.*;
import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer die {@link Postfach}-Klasse.
 *
 * @author oboehm
 */
public final class PostfachTest extends AbstractFachwertTest {

    /**
     * Zum Testen generieren wir ein normales Postfach mit Nummer und PLZ.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Postfach createFachwert() {
        return new Postfach(815, new PLZ("09876"), new Ort("Nirwana"));
    }

    /**
     * Hier ueberpruefen wir, ob die toString-Implementierung ueberschrieben
     * ist.
     */
    @Override
    @Test
    public void testToString() {
        Postfach postfach = createFachwert();
        assertEquals("Postfach 8 15, 09876 Nirwana", postfach.toString());
    }

    /**
     * Testmethode fuer {@link Postfach#getNummerFormatted()}.
     */
    @Test
    public void testGetNummerFormatted() {
        Postfach postfach = new Postfach(1234567890L, new PLZ("12345"), new Ort("Irgendwo"));
        assertEquals("12 34 56 78 90", postfach.getNummerFormatted());
    }

}
