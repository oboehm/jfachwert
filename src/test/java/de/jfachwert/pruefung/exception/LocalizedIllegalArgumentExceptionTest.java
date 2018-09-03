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
 * (c)reated 03.09.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link LocalizedIllegalArgumentException}-Klasse.
 *
 * @author oboehm
 */
public class LocalizedIllegalArgumentExceptionTest {

    @Test
    public void testGetLocalizedMessage() {
        LocalizedIllegalArgumentException ex = new LocalizedIllegalArgumentException("12 34", "bank_account");
        String msg = ex.getLocalizedMessage();
        if ("de".equals(Locale.getDefault().getLanguage())) {
            assertThat(msg, containsString("Bankverbindung"));
        } else {
            assertThat(msg, containsString("bank account"));
        }
    }

}
