/*
 * Copyright (c) 2017-2020 by Oliver Boehm
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
 * (c)reated 14.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.pruefung.NullValidator
import java.io.Serializable
import java.util.*

/**
 * Die meisten Fachwerte sind nur ein ganz duenner Wrapper um ein Attribut vom
 * Typ 'String' (oder allgemein vom Typ 'T'). Fuer diese Fachwerte duerfte
 * diese Implementierung ausreichen.
 *
 * @author oboehm
 * @since 14.03.2017
 * @since 0.0.2
 */
@JsonSerialize(using = ToStringSerializer::class)
abstract class AbstractFachwert<T : Serializable, S : AbstractFachwert<T, S>> protected constructor(code: T, validator: KSimpleValidator<T> = NullValidator()) : KFachwert, Comparable<S> {

    /**
     * Liefert die interne Praesentation fuer die abgeleiteten Klassen. Sie
     * ist nicht fuer den direkten Aufruf vorgesehen, weswegen die Methode auch
     * 'final' ist.
     *
     * @return die interne Repraesentation
     */
    val code: T

    init {
        this.code = validator.verify(code)
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

    /**
     * Zwei Fachwerte sind nur dann gleich, wenn sie vom gleichen Typ sind und
     * den gleichen Wert besitzen.
     *
     * @param other zu vergleichender Fachwert
     * @return true bei Gleichheit
     * @see java.lang.Object.equals
     */
    override fun equals(other: Any?): Boolean {
        if (other !is AbstractFachwert<*, *> || !this.javaClass.isAssignableFrom(other.javaClass)) {
            return false
        }
        return code == other.code
    }

    /**
     * Fuer die meisten Fachwerte reicht es, einfach den internen Code als
     * String auszugeben.
     *
     * @return den internen code
     */
    override fun toString(): String {
        return Objects.toString(code)
    }

    /**
     * Liefert die einzelnen Attribute eines Fachwertes als Map. Bei einem
     * einzelnen Wert wird als Default-Implementierung der Klassenname und
     * die toString()-Implementierung herangezogen.
     *
     * @return Attribute als Map
     */
    override fun toMap(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map[this.javaClass.simpleName.lowercase()] = toString()
        return map
    }

    /**
     * Dient zum Vergleich und Sortierung zweier Fachwerte.
     *
     * @param other der andere Fachwert
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     * @since 3.0
     */
    override fun compareTo(other: S): Int {
        if (this == other) {
            return 0
        }
        val otherCode = other.code
        return if (otherCode is Comparable<*>) {
            val thisValue = code as S
            val otherValue = otherCode as S
            thisValue.compareTo(otherValue)
        } else {
            throw UnsupportedOperationException("not implemented for " + this.javaClass)
        }
    }

}