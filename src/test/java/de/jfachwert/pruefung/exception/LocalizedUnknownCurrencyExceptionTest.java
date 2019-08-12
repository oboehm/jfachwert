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
 * (c)reated 10.08.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.pruefung.exception;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Unit-Test fuer {@link LocalizedUnknownCurrencyException}-Klasse.
 *
 * @author oboehm
 */
public final class LocalizedUnknownCurrencyExceptionTest {

    private final LocalizedUnknownCurrencyException exception = new LocalizedUnknownCurrencyException("Taler");

    @Test
    public void testGetMessage() {
        assertEquals("unknown currency code: taler", exception.getMessage().toLowerCase());
    }

    @Test
    public void testGetLocalizedMessage() {
        String msg = exception.getLocalizedMessage();
        if ("DE".equals(Locale.getDefault().getCountry())) {
            assertEquals("unbekannte W\u00e4hrung: Taler", msg);
        } else {
            assertEquals(exception.getMessage().toLowerCase(), msg);
        }
    }

}