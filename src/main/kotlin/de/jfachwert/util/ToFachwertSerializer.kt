/*
 * Copyright (c) 2018 by Oliver Boehm
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
package de.jfachwert.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.jfachwert.Fachwert;

import java.io.IOException;
import java.util.Map;

/**
 * Die Klasse serialisiert Fachwerte nach und von JSON. Damit die
 * Serialisierung funktioniert, muss
 * <pre>
 *       &lt;groupId&gt;com.fasterxml.jackson.core&lt;/groupId&gt;
 *       &lt;artifactId&gt;jackson-databind&lt;/artifactId&gt;
 * </pre>
 * als Abhaengigkeit eingebunden werden.
 *
 * @author oboehm
 * @since 1.0
 */
public class ToFachwertSerializer extends StdSerializer<Fachwert> {

    public ToFachwertSerializer() {
        this(Fachwert.class);
    }

    public ToFachwertSerializer(Class<Fachwert> t) {
        super(t);
    }

    /**
     * Fuer die Serialisierung wird der uebergebenen Fachwert nach seinen
     * einzelnen Elementen aufgeteilt und serialisiert.
     *
     * @param fachwert Fachwert
     * @param jgen Json-Generator
     * @param provider Provider
     * @throws IOException sollte nicht auftreten
     */
    @Override
    public void serialize(Fachwert fachwert, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        serialize(fachwert.toMap(), jgen, provider);
    }

    private void serialize(final Map<String, Object> map, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jgen.writeObjectField(entry.getKey(), entry.getValue());
        }
        jgen.writeEndObject();
    }

}
