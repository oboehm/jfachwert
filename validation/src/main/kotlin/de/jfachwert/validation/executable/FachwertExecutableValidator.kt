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
package de.jfachwert.validation.executable

import jakarta.validation.ConstraintViolation
import jakarta.validation.executable.ExecutableValidator
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Eine minimale Implementierung des ExecutableValidator-Interface von Jakarta.
 * Da Fachwerte ihre Gueltigkeit intern pruefen und keine Methoden- oder
 * Konstruktor-Constraints deklarieren, liefert dieser Validator aktuell keine
 * Violations.
 *
 * @author oboehm
 * @since 6.8.1
 */
open class FachwertExecutableValidator : ExecutableValidator {

    override fun <T : Any> validateParameters(
        `object`: T,
        method: Method,
        parameterValues: Array<Any?>,
        vararg groups: Class<*>
    ): Set<ConstraintViolation<T>> = emptySet()

    override fun <T : Any> validateReturnValue(
        `object`: T,
        method: Method,
        returnedValue: Any?,
        vararg groups: Class<*>
    ): Set<ConstraintViolation<T>> = emptySet()

    override fun <T : Any> validateConstructorParameters(
        constructor: Constructor<out T>,
        parameterValues: Array<Any?>,
        vararg groups: Class<*>
    ): Set<ConstraintViolation<T>> = emptySet()

    override fun <T : Any> validateConstructorReturnValue(
        constructor: Constructor<out T>,
        constructedObject: T,
        vararg groups: Class<*>
    ): Set<ConstraintViolation<T>> = emptySet()

}
