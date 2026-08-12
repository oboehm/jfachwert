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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 12.08.2026 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.validation

import jakarta.validation.ClockProvider
import jakarta.validation.ConstraintValidatorFactory
import jakarta.validation.MessageInterpolator
import jakarta.validation.ParameterNameProvider
import jakarta.validation.TraversableResolver
import jakarta.validation.ValidationException
import jakarta.validation.Validator
import jakarta.validation.ValidatorContext
import jakarta.validation.ValidatorFactory

/**
 * Eine ValidatorFactory, die [FachwertValidator]-Instanzen erzeugt.
 * Sie ist als Alternative zu einer vollstaendigen Bean-Validation-
 * Implementierung (z.B. Hibernate Validator) gedacht.
 *
 * @author oboehm
 * @since 6.8.1
 */
class FachwertValidatorFactory : ValidatorFactory {

    override fun getValidator(): Validator = FachwertValidator()

    override fun usingContext(): ValidatorContext = FachwertValidatorContext()

    override fun getMessageInterpolator(): MessageInterpolator = FachwertMessageInterpolator()

    override fun getTraversableResolver(): TraversableResolver = FachwertTraversableResolver()

    override fun getConstraintValidatorFactory(): ConstraintValidatorFactory = FachwertConstraintValidatorFactory()

    override fun getParameterNameProvider(): ParameterNameProvider = FachwertParameterNameProvider()

    override fun getClockProvider(): ClockProvider = FachwertClockProvider()

    override fun <T : Any> unwrap(type: Class<T>): T {
        if (type.isInstance(this)) {
            @Suppress("UNCHECKED_CAST")
            return this as T
        }
        throw ValidationException("unwrap($type) nicht unterstuetzt")
    }

    override fun close() {
    }

    override fun toString(): String = "FachwertValidatorFactory"

}
