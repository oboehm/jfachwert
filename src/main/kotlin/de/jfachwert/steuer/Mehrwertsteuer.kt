/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 11.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.Fachwert
import de.jfachwert.math.Prozent
import java.util.*
import java.util.function.Function

/**
 * Die Mehrwertsteuer wird auf den normalen (Netto-)Preis aufgeschlagen.
 * In Deutschland gibt es 2 Mehrwersteuer-Sätze: den Standardsatz von 19% und
 * den ermaessigten Steuersatz von 7%.
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Mehrwertsteuer (val prozent: Prozent) : Fachwert {

    constructor(satz: String) : this(Prozent.of(satz))

    companion object {

        private val WEAK_CACHE = WeakHashMap<Prozent, Mehrwertsteuer>()

        @JvmStatic
        fun of(satz: Prozent): Mehrwertsteuer {
            return WEAK_CACHE.computeIfAbsent(satz, Function(::Mehrwertsteuer))
        }

    }

    override fun toString(): String {
        return prozent.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Mehrwertsteuer
        return (prozent == other.prozent)
    }

    override fun hashCode(): Int {
        return prozent.hashCode()
    }

}