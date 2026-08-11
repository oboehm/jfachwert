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

import de.jfachwert.bank.IBAN;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.MethodType;
import jakarta.validation.metadata.Scope;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link FachwertBeanDescriptor}.
 *
 * @author oboehm
 */
class FachwertBeanDescriptorTest {

    private final BeanDescriptor descriptor = new FachwertBeanDescriptor(IBAN.class);

    @Test
    void getElementClass() {
        assertThat(descriptor.getElementClass(), equalTo(IBAN.class));
    }

    @Test
    void hasConstraints() {
        assertFalse(descriptor.hasConstraints());
        assertFalse(descriptor.findConstraints().hasConstraints());
    }

    @Test
    void getConstraintDescriptors() {
        assertThat(descriptor.getConstraintDescriptors(), empty());
        assertThat(descriptor.findConstraints().getConstraintDescriptors(), empty());
    }

    @Test
    void isBeanConstrained() {
        assertFalse(descriptor.isBeanConstrained());
    }

    @Test
    void getConstrainedProperties() {
        assertThat(descriptor.getConstrainedProperties(), empty());
        assertNull(descriptor.getConstraintsForProperty("value"));
    }

    @Test
    void getConstrainedMethods() {
        assertThat(descriptor.getConstrainedMethods(MethodType.GETTER), empty());
        assertNull(descriptor.getConstraintsForMethod("getValue"));
    }

    @Test
    void getConstrainedConstructors() {
        assertThat(descriptor.getConstrainedConstructors(), empty());
        assertNull(descriptor.getConstraintsForConstructor());
    }

    @Test
    void findConstraintsIsFluent() {
        assertNotNull(descriptor.findConstraints().lookingAt(Scope.HIERARCHY));
        assertNotNull(descriptor.findConstraints().declaredOn(ElementType.FIELD));
        assertNotNull(descriptor.findConstraints().unorderedAndMatchingGroups());
    }

    @Test
    void toStringContainsElementClass() {
        assertTrue(descriptor.toString().contains("IBAN"));
    }

}
