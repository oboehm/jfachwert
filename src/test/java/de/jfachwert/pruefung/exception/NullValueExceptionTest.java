package de.jfachwert.pruefung.exception;/*
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
 * (c)reated 17.01.2018 by oboehm (ob@oasd.de)
 */

import org.junit.Test;

import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link NullValueException}-Klasse.
 *
 * @author oboehm
 */
public final class NullValueExceptionTest {
    
    private final NullValueException exception = new NullValueException();

    /**
     * Hier pruefen wir, ob das richtige {@link ResourceBundle} gezogen wird.
     */
    @Test
    public void getLocalizedMessage() {
        ResourceBundle bundle = ResourceBundle.getBundle("de.jfachwert.messages");
        assertEquals(bundle.getString("null_values_not_allowed"), exception.getLocalizedMessage());
    }

}
