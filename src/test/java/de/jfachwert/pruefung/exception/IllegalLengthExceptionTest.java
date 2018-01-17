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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.pruefung.exception.IllegalLengthException;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Unit-Teests fuer de.jfachwert.pruefung.exception.IllegalLengthException.
 *
 * @author oboehm
 * @since 0.2 (21.04.2017)
 */
public final class IllegalLengthExceptionTest {

    /**
     * Die Message sollte die uebergebenen Parameter beinhalten.
     */
    @Test
    public void getMessage() {
        IllegalLengthException exception = new IllegalLengthException("hello", 2, 3);
        checkMessage(exception.getMessage(), 2, 3);
    }

    /**
     * Die LocalizedMessage sollte die uebergebenen Parameter beinhalten und
     * die Ausgabe sollten in der Landessprache erfolgen.
     */
    @Test
    public void getLocalizedMessage() {
        IllegalLengthException exception = new IllegalLengthException("world", 6, 7);
        String message = exception.getLocalizedMessage();
        checkMessage(message);
        if ("de".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            assertThat(message, containsString("ist nicht zwischen"));
        }
    }

    private static void checkMessage(String message, int... args) {
        for(int arg : args) {
            assertThat(message, containsString(Integer.toString(arg)));
        }
    }

}
