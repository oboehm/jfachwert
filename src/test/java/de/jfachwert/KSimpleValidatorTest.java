/*
 * Copyright (c) 2023 by Oli B.
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
 * (c)reated 09.01.23 by oboehm
 */
package de.jfachwert;

import de.jfachwert.pruefung.NoopVerfahren;
import de.jfachwert.pruefung.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer KSimpleValidator.
 *
 * @author oboehm
 * @since 09.01.23
 */
public class KSimpleValidatorTest {

    private final KSimpleValidator<Long> validator = new TestValidator();

    @Test
    void testVerify() {
        assertThrows(IllegalArgumentException.class, () -> validator.verify(4711L));
    }

    public static class TestValidator extends NoopVerfahren<Long> {
        @Override
        public Long validate(Long wert) {
            throw new ValidationException("ups");
        }
    }

}
