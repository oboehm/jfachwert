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
 * (c)reated 12.08.2026 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.validation;

import jakarta.validation.ClockProvider;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link FachwertValidatorFactory}.
 *
 * @author oboehm
 */
class FachwertValidatorFactoryTest {

    private final ValidatorFactory factory = new FachwertValidatorFactory();

    @Test
    void getValidator() {
        Validator validator = factory.getValidator();
        assertInstanceOf(FachwertValidator.class, validator);
    }

    @Test
    void usingContext() {
        Validator validator = factory.usingContext().clockProvider(new FachwertClockProvider()).getValidator();
        assertInstanceOf(FachwertValidator.class, validator);
    }

    @Test
    void getMessageInterpolator() {
        assertThat(factory.getMessageInterpolator().interpolate("Hallo {name}", null), equalTo("Hallo {name}"));
    }

    @Test
    void getTraversableResolver() {
        assertNotNull(factory.getTraversableResolver());
    }

    @Test
    void getConstraintValidatorFactory() {
        ConstraintValidatorFactory cvFactory = factory.getConstraintValidatorFactory();
        assertInstanceOf(FachwertConstraintValidator.class, cvFactory.getInstance(FachwertConstraintValidator.class));
    }

    @Test
    void getParameterNameProvider() throws Exception {
        assertThat(
                factory.getParameterNameProvider().getParameterNames(FachwertConstraintValidator.class.getMethod("isValid", de.jfachwert.Fachwert.class, jakarta.validation.ConstraintValidatorContext.class)),
                equalTo(java.util.List.of("arg0", "arg1")));
    }

    @Test
    void getClockProvider() {
        ClockProvider clockProvider = factory.getClockProvider();
        Clock clock = clockProvider.getClock();
        assertNotNull(clock);
    }

    @Test
    void unwrap() {
        assertSame(factory, factory.unwrap(FachwertValidatorFactory.class));
    }

    @Test
    void unwrapUnsupportedType() {
        assertThrows(ValidationException.class, () -> factory.unwrap(String.class));
    }

    @Test
    void close() {
        factory.close();
        assertNotNull(factory.getValidator());
    }

    @Test
    void toStringContainsClassName() {
        assertThat(factory.toString(), containsString("FachwertValidatorFactory"));
    }

}
