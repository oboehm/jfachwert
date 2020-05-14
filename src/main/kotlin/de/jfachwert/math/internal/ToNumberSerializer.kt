/*
 * Copyright (c) 2018-2020 by Oliver Boehm
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
 * (c)reated 27.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import de.jfachwert.math.AbstractNumber
import java.io.IOException

/**
 * Die Klasse serialisiert Zahlen, die von [AbstractNumber] abgeleitet
 * sind, nach und von JSON. Damit die Serialisierung funktioniert, muss
 * <pre>
 * &lt;groupId&gt;com.fasterxml.jackson.core&lt;/groupId&gt;
 * &lt;artifactId&gt;jackson-databind&lt;/artifactId&gt;
 * </pre>
 * als Abhaengigkeit eingebunden werden.
 *
 * @author oboehm
 * @since 1.0
 */
class ToNumberSerializer @JvmOverloads constructor(t: Class<AbstractNumber> = AbstractNumber::class.java) : StdSerializer<AbstractNumber>(t) {

    /**
     * Fuer die Serialisierung wird die uebergebene Nummer in eine
     * [java.math.BigDecimal] gewandelt.
     *
     * @param number uebergebene Nummer
     * @param jgen Json-Generator
     * @param provider Provider
     * @throws IOException sollte nicht auftreten
     */
    @Throws(IOException::class)
    override fun serialize(number: AbstractNumber, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeNumber(number.toBigDecimal())
    }

}