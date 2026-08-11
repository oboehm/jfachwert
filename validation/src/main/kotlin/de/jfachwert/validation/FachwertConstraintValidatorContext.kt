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
 * (c)reated 11.08.2026 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.validation

import jakarta.validation.ClockProvider
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder
import java.time.Clock

/**
 * Eine minimale Implementierung des ConstraintValidatorContext-Interface von
 * Jakarta. Die gesetzten Violation-Templates koennen ueber [templates]
 * abgefragt werden.
 *
 * @author oboehm
 * @since 6.8.1
 */
class FachwertConstraintValidatorContext(
    private val defaultMessageTemplate: String = ""
) : ConstraintValidatorContext {

    private var defaultViolationDisabled = false
    private val violationTemplates = mutableListOf<String>()

    override fun disableDefaultConstraintViolation() {
        defaultViolationDisabled = true
    }

    override fun getDefaultConstraintMessageTemplate(): String = defaultMessageTemplate

    override fun getClockProvider(): ClockProvider = ClockProvider { Clock.systemDefaultZone() }

    override fun buildConstraintViolationWithTemplate(messageTemplate: String): ConstraintViolationBuilder =
        FachwertConstraintViolationBuilder(messageTemplate)

    override fun <T : Any> unwrap(type: Class<T>): T {
        throw UnsupportedOperationException("unwrap($type) nicht unterstuetzt")
    }

    val isDefaultConstraintViolationDisabled: Boolean
        get() = defaultViolationDisabled

    val templates: List<String>
        get() = violationTemplates.toList()

    private inner class FachwertConstraintViolationBuilder(
        private val messageTemplate: String
    ) : ConstraintViolationBuilder {

        override fun addConstraintViolation(): ConstraintValidatorContext {
            violationTemplates.add(messageTemplate)
            return this@FachwertConstraintValidatorContext
        }

        @Deprecated("Deprecated in Java")
        override fun addNode(name: String): ConstraintViolationBuilder.NodeBuilderDefinedContext =
            throw UnsupportedOperationException("addNode nicht unterstuetzt")

        override fun addPropertyNode(name: String): ConstraintViolationBuilder.NodeBuilderCustomizableContext =
            throw UnsupportedOperationException("addPropertyNode nicht unterstuetzt")

        override fun addBeanNode(): ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext =
            throw UnsupportedOperationException("addBeanNode nicht unterstuetzt")

        override fun addContainerElementNode(
            name: String,
            containerType: Class<*>,
            typeArgumentIndex: Int?
        ): ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext =
            throw UnsupportedOperationException("addContainerElementNode nicht unterstuetzt")

        override fun addParameterNode(index: Int): ConstraintViolationBuilder.NodeBuilderDefinedContext =
            throw UnsupportedOperationException("addParameterNode nicht unterstuetzt")

    }

}
