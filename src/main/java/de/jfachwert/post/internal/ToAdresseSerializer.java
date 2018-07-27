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
package de.jfachwert.post.internal;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.jfachwert.post.Adresse;

import java.io.IOException;
import java.util.Map;

public class ToAdresseSerializer extends StdSerializer<Adresse> {

    public ToAdresseSerializer() {
        this(Adresse.class);
    }

    public ToAdresseSerializer(Class<Adresse> t) {
        super(t);
    }

    @Override
    public void serialize(Adresse adresse, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        serialize(adresse.toMap(), jgen, provider);
    }

    private void serialize(final Map<String, Object> map, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jgen.writeObjectField(entry.getKey(), entry.getValue());
        }
        jgen.writeEndObject();
    }

}
