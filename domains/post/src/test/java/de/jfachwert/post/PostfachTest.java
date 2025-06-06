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

import de.jfachwert.FachwertTest;
import de.jfachwert.pruefung.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer die {@link Postfach}-Klasse.
 *
 * @author oboehm
 */
public final class PostfachTest extends FachwertTest {

    /**
     * Zum Testen generieren wir ein normales Postfach mit Nummer und PLZ.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Postfach createFachwert() {
        return new Postfach(815, new Ort(new PLZ("09876"), "Nirwana"));
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
        Postfach postfach = Postfach.of(1234567890L, new Ort(new PLZ("12345"), "Irgendwo"));
        assertEquals("12 34 56 78 90", postfach.getNummerFormatted());
    }

    /**
     * Postfaeche muessen immer eine positive Zahl sein. Andere Zahlen sollten
     * abgelehnt werden.
     */
    @Test
    public void testInvalidPostfach() {
        assertThrows(IllegalArgumentException.class, () -> new Postfach(-1, new Ort(new PLZ("04711"), "Dufte")));
    }

    /**
     * Hier wird das Anlegen eines Postfachs ohne Postfachnummer getestet.
     */
    @Test
    public void testPostfachOhneNummer() {
        Postfach postfach = new Postfach(new Ort(new PLZ("66666"), "NumberOfTheBeast"));
        assertThat(postfach.toString(), not(containsString("null")));
    }

    /**
     * Test-Methode fuer {@link Postfach#validate(String)}.
     */
    @Test
    public void testValidate() {
        String postfach = "Postfach 12 34 56\n12350 Musterdorf";
        Postfach.validate(postfach);
    }

    /**
     * Testfall fuer #30.
     */
    @Test
    public void testValidate6stellig() {
        Postfach.validate("123456");
    }

    /**
     * Test-Methode fuer {@link Postfach#validate(String)}.
     */
    @Test
    public void testValidateInvalidNummer() {
        String postfach = "Postfach abc\n12350 Musterdorf";
        assertThrows(ValidationException.class, () -> Postfach.validate(postfach));
    }
    
    @Test
    public void testPostfachString() {
        Postfach postfach = Postfach.of("01234 Irgendwo");
        assertEquals(new PLZ("01234"), postfach.getPLZ());
        assertEquals(postfach.getNummer(), Optional.empty());
    }

    @Test
    public void testPostfach6stellig() {
        Postfach postfach = Postfach.of("123456");
        assertThat(postfach.toString().replaceAll(" ", ""), containsString("123456"));
    }

}
