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
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Die Klasse FachwertValidatorTest ...
 *
 * @author oboehm
 */
public class FachwertValidatorTest {

    @Test
    void testValidate() {
        FachwertValidator validator = new FachwertValidator();
        Fachwert fachwert = Text.of("Test");
        Set<ConstraintViolation<Fachwert>> violations = validator.validate(fachwert);
        assertTrue(violations.isEmpty());
    }

}
