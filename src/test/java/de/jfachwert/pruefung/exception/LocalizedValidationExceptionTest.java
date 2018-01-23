/*
 * Copyright (c) 2017 by Oliver Boehm
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
 * (c)reated 13.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception;

import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.pruefung.exception.LocalizedValidationException;
import org.junit.*;
import patterntesting.runtime.junit.*;
import patterntesting.runtime.util.*;

import java.io.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link LocalizedValidationException}-Klasse.
 *
 * @author oboehm
 */
public final class LocalizedValidationExceptionTest {

    private final LocalizedValidationException exception = new InvalidValueException("number");

    /**
     * Hier testen wir, ob eine lokalisierte Message zurueckkommt.
     */
    @Test
    public void getLocalizedString() {
        String s = exception.getLocalizedMessage();
        assertThat(s, not(isEmptyOrNullString()));
    }

    /**
     * Da Exceptions serialisierbar sind, testen wir hier das. Ein Problem
     * tauchte dabei mit dem transienten bundle-Attribut, das nach der
     * Deserialisierung nicht initalisiert wurde. Das ist aber inzwischen
     * behoben.
     *
     * @throws NotSerializableException falls Exception nicht serialisierbar
     * @throws ClassNotFoundException   sollte nicht auftreten
     */
    @Test
    public void testSerializable() throws NotSerializableException, ClassNotFoundException {
        SerializableTester.assertSerialization(exception);
        LocalizedValidationException deserialized =
                (LocalizedValidationException) Converter.deserialize(Converter.serialize(exception));
        assertEquals(exception.getLocalizedMessage(), deserialized.getLocalizedMessage());
    }

}
