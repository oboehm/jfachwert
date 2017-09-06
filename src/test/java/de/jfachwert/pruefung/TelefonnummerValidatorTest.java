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
 * (c)reated 27.06.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import javax.validation.*;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link TelefonnummerValidator}-Klasse.
 *
 * @author oboehm
 */
@RunWith(Parameterized.class)
public class TelefonnummerValidatorTest {

    private final String telefonnummer;
    private final boolean valid;
    private final TelefonnummerValidator validator = new TelefonnummerValidator();

    /**
     * Hierueber werden die Test-Werte per Konstruktor "injected".
     *
     * @param nummer gueltige oder ungueltige Telefonnummer
     * @param valid kennzeichnet die Telefonnummer als gueltig oder ungueltgi
     */
    public TelefonnummerValidatorTest(String nummer, boolean valid) {
        this.telefonnummer = nummer;
        this.valid = valid;
    }

    /**
     * Hier setzen wir die verschiedenen Telefonnumer auf. Die Testwerte dazu
     * entstammen u.a. aus dem Wikipedia-Artikel zu Rufnummern.
     *
     * @return Iterable of Array, wie vom Parameterized-Runner vorgegeben.
     */
    @Parameterized.Parameters(name = "{index}: {0}: {1}")
    public static Collection<Object[]> data() {
        Collection<Object[]> values = new ArrayList<>();
        addValidNummern(values, "+49 30 12345-67", "+49 30 1234567", "+49 (30) 12345 - 67", "+49-30-1234567",
                "+49 (0)30 12345-67", "030 12345-67", "(030) 12345 67", "0900 5 123456", "0 30 / 12 34 56",
                "+43 1 58058-0", "01 58058-0", "026 324 11 13", "+41 26 324 11 13", "+49 30 12345678910",
                "+49 (0)30 / 12 34 5 - 67 89 10");
        addInvalidNummern(values, "Buchstaben", "012abc", "+49 30 123456789101");
        return values;
    }

    private static void addValidNummern(Collection<Object[]> values, String... nummern) {
        addNummern(values, true, nummern);
    }

    private static void addInvalidNummern(Collection<Object[]> values, String... nummern) {
        addNummern(values, false, nummern);
    }

    private static void addNummern(Collection<Object[]> values, boolean valid, String... nummern) {
        for(String n : nummern) {
            Object[] array = { n, valid };
            values.add(array);
        }
    }

    /**
     * Hier testen wir den SimpleValidator fuer die verschiedensten E-Mail-Adressen,
     * die ueber die Parameterized-Klasse hereinkommen.
     */
    @Test
    public void validateTelefonnummer() {
        try {
            String validated = validator.validate(telefonnummer);
            assertEquals(telefonnummer, validated);
            assertThat(telefonnummer, valid, is(true));
        } catch (ValidationException expected) {
            assertThat(expected.getMessage(), valid, is(false));
        }
    }

}
