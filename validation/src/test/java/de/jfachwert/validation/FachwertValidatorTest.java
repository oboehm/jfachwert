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
 * (c)reated 20.07.2026 by oboehm (ob@oasd.de)
 */
package de.jfachwert.validation;

import de.jfachwert.Fachwert;
import de.jfachwert.Text;
import de.jfachwert.bank.IBAN;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.validation.executable.FachwertExecutableValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Die Klasse FachwertValidatorTest ...
 *
 * @author oboehm
 */
public class FachwertValidatorTest {

    private final FachwertValidator validator = new FachwertValidator();

    @Test
    void testValidate() {
        Fachwert fachwert = Text.of("Test");
        Set<ConstraintViolation<Fachwert>> violations = validator.validate(fachwert);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidateWithViolations() {
        Fachwert invalid = new IBAN("DE4711", new NullValidator<>());
        assertFalse(invalid.isValid());
        Set<ConstraintViolation<Fachwert>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidateGenericWithInvalidFachwert() {
        Fachwert invalid = new IBAN("DE4711", new NullValidator<>());
        Set<ConstraintViolation<Object>> violations = validator.validate((Object) invalid);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidateGenericWithPlainObject() {
        Set<ConstraintViolation<Object>> violations = validator.validate(new Object());
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidateProperty() {
        Set<ConstraintViolation<Object>> violations = validator.validateProperty(new Object(), "value");
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidateValue() {
        Set<ConstraintViolation<Object>> violations = validator.validateValue(Object.class, "value", "test");
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGetConstraintsForClass() {
        BeanDescriptor descriptor = validator.getConstraintsForClass(IBAN.class);
        assertEquals(IBAN.class, descriptor.getElementClass());
        assertFalse(descriptor.hasConstraints());
        assertFalse(descriptor.isBeanConstrained());
    }

    @Test
    void testUnwrap() {
        assertSame(validator, validator.unwrap(FachwertValidator.class));
    }

    @Test
    void testUnwrapUnsupportedType() {
        assertThrows(ValidationException.class, () -> validator.unwrap(String.class));
    }

    @Test
    void testForExecutables() {
        ExecutableValidator executableValidator = validator.forExecutables();
        assertNotNull(executableValidator);
        assertInstanceOf(FachwertExecutableValidator.class, executableValidator);
    }

}
