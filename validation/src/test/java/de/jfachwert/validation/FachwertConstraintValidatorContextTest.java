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
 * (c)reated 11.08.2026 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.validation;

import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link FachwertConstraintValidatorContext}.
 *
 * @author oboehm
 */
class FachwertConstraintValidatorContextTest {

    private final FachwertConstraintValidatorContext context = new FachwertConstraintValidatorContext();

    @Test
    void testGetDefaultConstraintMessageTemplate() {
        FachwertConstraintValidatorContext withTemplate = new FachwertConstraintValidatorContext("default message");
        assertThat(withTemplate.getDefaultConstraintMessageTemplate(), equalTo("default message"));
    }

    @Test
    void testGetDefaultConstraintMessageTemplateWithoutTemplate() {
        assertNotNull(context.getDefaultConstraintMessageTemplate());
    }

    @Test
    void testGetClockProvider() {
        Clock clock = context.getClockProvider().getClock();
        assertNotNull(clock);
    }

    @Test
    void testDisableDefaultConstraintViolation() {
        assertFalse(context.isDefaultConstraintViolationDisabled());
        context.disableDefaultConstraintViolation();
        assertTrue(context.isDefaultConstraintViolationDisabled());
    }

    @Test
    void testBuildConstraintViolationWithTemplate() {
        ConstraintViolationBuilder builder = context.buildConstraintViolationWithTemplate("violation message");
        assertNotNull(builder);
    }

    @Test
    void testAddConstraintViolation() {
        ConstraintViolationBuilder builder = context.buildConstraintViolationWithTemplate("violation message");
        assertSame(context, builder.addConstraintViolation());
        assertThat(context.getTemplates(), equalTo(java.util.List.of("violation message")));
    }

    @Test
    void testAddConstraintViolationMultiple() {
        context.buildConstraintViolationWithTemplate("first").addConstraintViolation();
        context.buildConstraintViolationWithTemplate("second").addConstraintViolation();
        assertThat(context.getTemplates(), equalTo(java.util.List.of("first", "second")));
    }

}
