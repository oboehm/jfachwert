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

import jakarta.validation.ParameterNameProvider
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Ein ParameterNameProvider, der die Parameter-Namen generisch als
 * "arg0", "arg1", ... liefert.
 *
 * @author oboehm
 * @since 6.8.1
 */
class FachwertParameterNameProvider : ParameterNameProvider {

    override fun getParameterNames(constructor: Constructor<*>): List<String> =
        parameterNames(constructor.parameterCount)

    override fun getParameterNames(method: Method): List<String> =
        parameterNames(method.parameterCount)

    private fun parameterNames(count: Int): List<String> =
        (0 until count).map { "arg$it" }

    override fun toString(): String = "FachwertParameterNameProvider"

}
