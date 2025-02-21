/*
 * Copyright (c) 2025 by Oliver Boehm
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
 * (c)reated 21.02.2025 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.pruefung.NullValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link LEGS}-Klasse.
 */
public class LEGSTest extends AbstractFachwertTest<String, LEGS> {

    /**
     * Erzeugt eine LEGS zum Testen.
     *
     * @param nr LEGS-Nummer
     * @return Test-Objekt zum Testen
     */
    @Override
    protected LEGS createFachwert(String nr) {
        return LEGS.of(nr);
    }

    /**
     * Das Beispiel dazu stammt aus <a href=
     * "https://de.wikipedia.org/wiki/Leistungserbringergruppenschl%C3%BCssel"
     * >Leistungserbringungsschluessel</a>.
     *
     * @return "2213700"
     */
    @Override
    protected String getCode() {
        return "2213700";
    }

    @Test
    public void test8stelligeLEGS() {
        assertThrows(IllegalArgumentException.class, () -> LEGS.of("12345678"));
    }

    /**
     * LEGS koennen zur Formattierung Leerzeichen oder '/' enthalten.
     */
    @Test
    public void testOf() {
        LEGS a = LEGS.of("3202435");
        LEGS b = LEGS.of("32/02/435");
        LEGS c = LEGS.of("32 02 435");
        assertEquals(a, b);
        assertEquals(a, c);
        assertEquals(b, c);
    }

    @Test
    public void testTK() {
        LEGS legs = LEGS.of("15 02 B05");
        assertEquals("B05", legs.getTK());
    }

    @Test
    public void testInvalid() {
        LEGS bsnr = new LEGS("12345678", new NullValidator<>());
        assertFalse(bsnr.isValid());
    }

}
