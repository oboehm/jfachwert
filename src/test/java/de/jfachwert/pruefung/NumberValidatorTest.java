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
 * (c)reated 20.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link NumberValidator}-Klasse.
 *
 * @author oboehm
 */
public class NumberValidatorTest {
    
    private final NumberValidator validator = new NumberValidator();

    @Test(expected = ArithmeticException.class)
    public void testVerifyNumber() {
        validator.verifyNumber(Double.NEGATIVE_INFINITY);
    }
    
    @Test
    public void testValidate() {
        BigDecimal number = BigDecimal.valueOf(1000.23);
        String s = number.toString();
        assertEquals(s, validator.validate(s));
    }

    /**
     * Hier testen wir, ob {@link NumberValidator#validate(String)} auch mit
     * formattierten Zahlen klarkommt.
     */
    @Test
    public void testValidateFormattedEnglish() {
        checkValidateFormatted(Locale.ENGLISH);
    }

    /**
     * Hier testen wir, ob {@link NumberValidator#validate(String)} auch mit
     * deutsch formattierten Zahlen klarkommt.
     */
    @Test
    public void testValidateFormattedGerman() {
        checkValidateFormatted(Locale.GERMAN);
    }

    private void checkValidateFormatted(Locale locale) {
        BigDecimal number = BigDecimal.valueOf(2000.34);
        Object[] args = { number };
        String formatted = new MessageFormat("{0}", locale).format(args);
        String validated = validator.validate(formatted);
        assertEquals(number, new BigDecimal(validated));
    }

}
