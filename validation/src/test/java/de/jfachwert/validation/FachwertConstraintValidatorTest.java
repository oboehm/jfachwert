/*
 * Copyright (c) 2026 by Oliver Boehm
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
 * (c)reated 05.08.2026 by oboehm (ob@oasd.de)
 */
package de.jfachwert.validation;

import de.jfachwert.Fachwert;
import de.jfachwert.Text;
import de.jfachwert.bank.IBAN;
import de.jfachwert.pruefung.NullValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-Tests fuer {@link FachwertConstraintValidator}.
 *
 * @author oboehm
 */
class FachwertConstraintValidatorTest {

    private final FachwertConstraintValidator<Annotation> validator =
            new FachwertConstraintValidator<>();

    @Test
    void testIsValid() {
        Fachwert fachwert = Text.of("Test");
        assertTrue(validator.isValid(fachwert, context()));
    }

    @Test
    void testIsValidForInvalidValue() {
        Fachwert invalid = new IBAN("DE4711", new NullValidator<>());
        assertFalse(validator.isValid(invalid, context()));
    }

    private ConstraintValidatorContext context() {
        return new FachwertConstraintValidatorContext();
    }

}
