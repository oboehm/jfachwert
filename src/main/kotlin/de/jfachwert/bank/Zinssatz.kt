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
 * (c)reated 10.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.Fachwert
import de.jfachwert.math.Prozent

/**
 * Der Zinssatz (auch: Zinsfuss) wird in Prozent ausgedrueckt, mit dem der Zins
 * fuer verliehenes Kapital berechnet wird.
 *
 * @author oboehm
 * @since 4.0
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Zinssatz(val prozent: Prozent) : Fachwert {

    constructor(satz: String) : this(Prozent.of(satz))

    override fun toString(): String {
        return prozent.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Zinssatz) return false
        return prozent == other.prozent
    }

    override fun hashCode(): Int {
        return prozent.hashCode()
    }

}