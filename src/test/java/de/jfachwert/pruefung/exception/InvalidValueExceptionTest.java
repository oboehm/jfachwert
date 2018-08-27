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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Unit-Teests fuer de.jfachwert.pruefung.exception.InvalidValueException.
 *
 * @author oboehm
 * @since 0.2.0 (26.04.2017)
 */
public final class InvalidValueExceptionTest {

    private final InvalidValueException exception = new InvalidValueException("xx", "country");

    /**
     * Test-Methode fuer {@link InvalidValueException#getMessage()}.
     */
    @Test
    public void testGetMessage() {
        assertEquals("invalid value for country: \"xx\"", exception.getMessage());
    }

    /**
     * Test-Methode fuer {@link InvalidValueException#getLocalizedMessage()}.
     */
    @Test
    public void testGetLocalizedMessage() {
        if ("de".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            assertEquals("ung\u00fcltiger Wert wegen Land: \"xx\"", exception.getLocalizedMessage());
        }
    }

    /**
     * Hier wird die Lokalisierung von mehrfachen Woertern wie "postal code"
     * getestet.
     */
    @Test
    public void testGetMessagePostalCode() {
        InvalidValueException ex = new InvalidValueException("zzz", "postal_code");
        assertEquals("invalid value for postal code: \"zzz\"", ex.getMessage());
        assertThat(ex.getLocalizedMessage(), containsString("zzz"));
    }

    /**
     * Bei Bezeichnungen, fuer die kein {@link java.util.ResourceBundle}
     * vorhanden ist, sollte die Bezeichnung direkt in der Message erscheinen.
     */
    @Test
    public void testUnbekannteBezeichnung() {
        String context = "unbekannte Bzeichnung";
        InvalidValueException ex = new InvalidValueException(42, context);
        assertThat(ex.getMessage(), containsString(context));
        assertThat(ex.getLocalizedMessage(), containsString(context));
    }

    /**
     * Hier testen wir, ob bei einem fehlenden Wert nicht der Wert noch mit
     * ausgegeben wird.
     */
    @Test
    public void testMissingValue() {
        InvalidValueException ex = new InvalidValueException("name");
        assertThat(ex.getLocalizedMessage(), not(containsString("null")));
    }

}
