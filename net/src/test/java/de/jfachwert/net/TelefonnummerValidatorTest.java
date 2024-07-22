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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 27.06.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.KSimpleValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.jfachwert.pruefung.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link Telefonnummer.Validator}-Klasse.
 *
 * @author oboehm
 */
public class TelefonnummerValidatorTest {

    private final KSimpleValidator<String> validator = new Telefonnummer.Validator();

    @ParameterizedTest
    @ValueSource(strings = {"+49 30 12345-67", "+49 30 1234567", "+49 (30) 12345 - 67", "+49-30-1234567",
            "+49 (0)30 12345-67", "030 12345-67", "(030) 12345 67", "0900 5 123456", "0 30 / 12 34 56",
            "+43 1 58058-0", "01 58058-0", "026 324 11 13", "+41 26 324 11 13", "+49 30 12345678910",
            "+49 (0)30 / 12 34 5 - 67 89 10"})
    public void testValidNummern(String telefonnummer) {
        String validated = validator.validate(telefonnummer);
        assertEquals(telefonnummer, validated);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Buchstaben", "012abc", "+49 30 123456789101"})
    public void testInvalidNummern(String telefonnummer) {
        assertThrows(ValidationException.class, () -> validator.validate(telefonnummer));
    }

}
