/*
 * Copyright (c) 2023-2024 by Oli B.
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
 * (c)reated 23.12.23 by oboehm
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.pruefung.NullValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer die {@link Versichertennummer}.
 *
 * @author oboehm
 * @since 5.1 (23.12.23)
 */
public final class VersichertennummerTest extends AbstractFachwertTest<String, Versichertennummer> {

    @Override
    protected Versichertennummer createFachwert(String code) {
        return Versichertennummer.of(code);
    }

    /**
     * Liefert eine Test-Versichtennummer als String.
     *
     * @return "A123456780"
     */
    @Override
    protected String getCode() {
        return "A123456780";
    }

    @Test
    void testWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> new Versichertennummer("X1234"));
    }

    @Test
    void testTooMuchLetters() {
        assertThrows(IllegalArgumentException.class, () -> Versichertennummer.of("XX12345678"));
    }

    @Test
    void testWrongPruefziffer() {
        assertThrows(IllegalArgumentException.class, () -> Versichertennummer.of("A123456789"));
    }

    @Test
    void testPruefziffer() {
        Versichertennummer.of("X234567891");
    }

    @Test
    void testInvalid() {
        Versichertennummer invalid = new Versichertennummer(getInvalidCode(), new NullValidator<>());
        assertFalse(invalid.isValid());
    }

}
