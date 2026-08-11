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

import jakarta.validation.executable.ExecutableValidator;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

/**
 * Unit-Tests fuer {@link FachwertExecutableValidator}.
 *
 * @author oboehm
 */
class FachwertExecutableValidatorTest {

    private final ExecutableValidator validator = new FachwertExecutableValidator();

    @Test
    void validateParameters() throws NoSuchMethodException {
        assertThat(validator.validateParameters(new Object(), Object.class.getMethod("toString"), new Object[0]), empty());
    }

    @Test
    void validateReturnValue() throws NoSuchMethodException {
        assertThat(validator.validateReturnValue(new Object(), Object.class.getMethod("toString"), "value"), empty());
    }

    @Test
    void validateConstructorParameters() throws NoSuchMethodException {
        assertThat(validator.validateConstructorParameters(Object.class.getConstructor(), new Object[0]), empty());
    }

    @Test
    void validateConstructorReturnValue() throws NoSuchMethodException {
        assertThat(validator.validateConstructorReturnValue(Object.class.getConstructor(), new Object()), empty());
    }

}
