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
 * Unit-Tests fuer de.jfachwert.pruefung.EMailValidator. Die Werte stammen aus
 * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
 *
 * @author oboehm
 */
public class EMailAdresseValidatorTest {

    private final KSimpleValidator<String> validator = new EMailAdresse.Validator();

    @ParameterizedTest
    @ValueSource(strings = {"mkyong@yahoo.com", "mkyong-100@yahoo.com", "mkyong.100@yahoo.com",
            "mkyong111@mkyong.com", "mkyong-100@mkyong.net", "mkyong.100@mkyong.com.au", "mkyong@1.com",
            "mkyong@gmail.com.com", "mkyong+100@gmail.com", "mkyong-100@yahoo-test.com"})
    public void testValidEmailAdresse(String emailAdresse) {
        String validated = validator.validate(emailAdresse);
        assertEquals(emailAdresse, validated);
    }

    @ParameterizedTest
    @ValueSource(strings = {"mkyong", "mkyong@.com.my", "mkyong123@gmail.a", "mkyong123@.com",
            "mkyong123@.com.com", ".mkyong@mkyong.com", "mkyong()*@gmail.com", "mkyong@%*.com",
            "mkyong..2002@gmail.com", "mkyong.@gmail.com", "mkyong@mkyong@gmail.com", "mkyong@gmail.com.1a"})
    public void testInvalidEmailAdresse(String emailAdresse) {
        assertThrows(ValidationException.class, () -> validator.validate(emailAdresse));
    }


}
