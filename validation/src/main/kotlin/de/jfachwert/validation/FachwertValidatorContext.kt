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
import jakarta.validation.Validator
import jakarta.validation.ValidatorContext
import jakarta.validation.valueextraction.ValueExtractor

/**
 * Eine Implementierung des ValidatorContext-Interface von Jakarta.
 * Die konfigurierten Komponenten werden gespeichert; der [FachwertValidator]
 * nutzt sie aktuell nicht.
 *
 * @author oboehm
 * @since 6.8.1
 */
class FachwertValidatorContext(
    private var messageInterpolator: MessageInterpolator? = null,
    private var traversableResolver: TraversableResolver? = null,
    private var constraintValidatorFactory: ConstraintValidatorFactory? = null,
    private var parameterNameProvider: ParameterNameProvider? = null,
    private var clockProvider: ClockProvider? = null
) : ValidatorContext {

    override fun messageInterpolator(messageInterpolator: MessageInterpolator): ValidatorContext {
        this.messageInterpolator = messageInterpolator
        return this
    }

    override fun traversableResolver(traversableResolver: TraversableResolver): ValidatorContext {
        this.traversableResolver = traversableResolver
        return this
    }

    override fun constraintValidatorFactory(constraintValidatorFactory: ConstraintValidatorFactory): ValidatorContext {
        this.constraintValidatorFactory = constraintValidatorFactory
        return this
    }

    override fun parameterNameProvider(parameterNameProvider: ParameterNameProvider): ValidatorContext {
        this.parameterNameProvider = parameterNameProvider
        return this
    }

    override fun clockProvider(clockProvider: ClockProvider): ValidatorContext {
        this.clockProvider = clockProvider
        return this
    }

    override fun addValueExtractor(valueExtractor: ValueExtractor<*>): ValidatorContext {
        return this
    }

    override fun getValidator(): Validator = FachwertValidator()

}
