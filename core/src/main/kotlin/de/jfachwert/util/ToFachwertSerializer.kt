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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 27.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.util

import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.std.StdSerializer
import de.jfachwert.KFachwert

/**
 * Die Klasse serialisiert Fachwerte nach und von JSON. Damit die
 * Serialisierung funktioniert, muss jackson-databind als
 * Abhaengigkeit eingebunden werden.
 *
 * @author oboehm
 * @since 1.0
 */
class ToFachwertSerializer @JvmOverloads constructor(t: Class<KFachwert> = KFachwert::class.java) : StdSerializer<KFachwert>(t) {

    override fun serialize(fachwert: KFachwert, jgen: JsonGenerator, provider: SerializationContext) {
        serialize(fachwert.toMap(), jgen, provider)
    }

    private fun serialize(map: Map<String, Any>, jgen: JsonGenerator, provider: SerializationContext) {
        jgen.writeStartObject()
        for ((key, value) in map) {
            jgen.writePOJOProperty(key, value)
        }
        jgen.writeEndObject()
    }

}