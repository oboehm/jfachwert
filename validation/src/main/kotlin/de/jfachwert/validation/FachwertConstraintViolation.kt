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
 * (c)reated 17.07.26 by oliver (ob@jfachwert.de)
 */
package de.jfachwert.validation

import de.jfachwert.Fachwert
import jakarta.validation.ConstraintViolation
import jakarta.validation.Path
import jakarta.validation.metadata.ConstraintDescriptor

/**
 * Ein ConstraintViolation fuer einen ungueltigen Fachwert.
 *
 * @param rootBean der ungueltige Fachwert
 * @param message die Violation-Meldung
 * @author oboehm
 * @since 6.8
 */
class FachwertConstraintViolation @JvmOverloads constructor(
    private val rootBean: Fachwert,
    private val message: String = rootBean.toLongString()
) : ConstraintViolation<Fachwert> {

    override fun getMessage(): String = message

    override fun getMessageTemplate(): String = ""

    override fun getRootBean(): Fachwert = rootBean

    override fun getRootBeanClass(): Class<Fachwert> = Fachwert::class.java

    override fun getLeafBean(): Any = rootBean

    override fun getPropertyPath(): Path = PathImpl

    override fun getInvalidValue(): Any = rootBean

    override fun getConstraintDescriptor(): ConstraintDescriptor<*>? = null

    override fun getExecutableParameters(): Array<Any?> = emptyArray()

    override fun getExecutableReturnValue(): Any? = null

    override fun <U : Any> unwrap(type: Class<U>): U {
        throw UnsupportedOperationException("unwrap($type) nicht unterstuetzt")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FachwertConstraintViolation) return false
        return rootBean == other.rootBean && message == other.message
    }

    override fun hashCode(): Int {
        var result = rootBean.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }

    override fun toString(): String {
        return "FachwertConstraintViolation(rootBean=$rootBean, message='$message')"
    }

    private object PathImpl : Path {

        override fun iterator(): MutableIterator<Path.Node> = object : MutableIterator<Path.Node> {
            override fun hasNext(): Boolean = false
            override fun next(): Path.Node = throw NoSuchElementException()
            override fun remove() {}
        }

        override fun toString(): String = ""
    }

}
