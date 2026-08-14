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
package de.jfachwert.validation.metadata

import jakarta.validation.metadata.*
import java.lang.annotation.ElementType

/**
 * Eine minimale Implementierung des BeanDescriptor-Interface von Jakarta.
 * Fachwerte deklarieren ihre Constraints nicht ueber Annotationen, sondern
 * validieren sich intern selbst. Deshalb liefert dieser Descriptor keine
 * Constraints, wohl aber die Element-Klasse.
 *
 * @author oboehm
 * @since 6.8.1
 */
open class FachwertBeanDescriptor(
    private val elementClass: Class<*>
) : BeanDescriptor {

    override fun isBeanConstrained(): Boolean = false

    override fun getConstraintsForProperty(propertyName: String): PropertyDescriptor? = null

    override fun getConstrainedProperties(): Set<PropertyDescriptor> = emptySet()

    override fun getConstraintsForMethod(name: String, vararg parameterTypes: Class<*>): MethodDescriptor? = null

    override fun getConstrainedMethods(methodType: MethodType, vararg methodTypes: MethodType): Set<MethodDescriptor> =
        emptySet()

    override fun getConstraintsForConstructor(vararg parameterTypes: Class<*>): ConstructorDescriptor? = null

    override fun getConstrainedConstructors(): Set<ConstructorDescriptor> = emptySet()

    override fun hasConstraints(): Boolean = false

    override fun getElementClass(): Class<*> = elementClass

    override fun getConstraintDescriptors(): Set<ConstraintDescriptor<*>> = emptySet()

    override fun findConstraints(): ElementDescriptor.ConstraintFinder =
        object : ElementDescriptor.ConstraintFinder {
            override fun unorderedAndMatchingGroups(vararg groups: Class<*>): ElementDescriptor.ConstraintFinder = this
            override fun lookingAt(scope: Scope): ElementDescriptor.ConstraintFinder = this
            override fun declaredOn(vararg elementTypes: ElementType): ElementDescriptor.ConstraintFinder = this
            override fun getConstraintDescriptors(): Set<ConstraintDescriptor<*>> = emptySet()
            override fun hasConstraints(): Boolean = false
        }

    override fun toString(): String = "FachwertBeanDescriptor(elementClass=$elementClass)"

}
