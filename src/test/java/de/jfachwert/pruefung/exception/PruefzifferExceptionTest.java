package de.jfachwert.pruefung.exception;/*
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 23.03.2017 by oboehm (ob@jfachwert.de)
 */

import de.jfachwert.pruefung.exception.PruefzifferException;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Unit-Test fuer {@link PruefzifferException}-Klasse.
 *
 * @author oboehm
 */
public final class PruefzifferExceptionTest {

    private final PruefzifferException exception = new PruefzifferException("123456", "78", "90");

    /**
     * Die Message sollte die uebergebenen Parameter beinhalten.
     */
    @Test
    public void getMessage() {
        checkMessage(exception.getMessage());
    }

    /**
     * Die LocalizedMessage sollte die uebergebenen Parameter beinhalten und
     * die Ausgabe sollten in der Landessprache erfolgen.
     */
    @Test
    public void getLocalizedMessage() {
        String message = exception.getLocalizedMessage();
        checkMessage(message);
        if ("de".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            assertEquals("123456: Pr\u00FCfziffer=78 erwartet, aber \"90\" vorgefunden", message);
        }
    }

    private static void checkMessage(String message) {
        assertThat(message, containsString("123456"));
        assertThat(message, containsString("78"));
        assertThat(message, containsString("90"));
    }

}
