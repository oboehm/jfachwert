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
 * Unit-Tests fuer de.jfachwert.pruefung.EMailValidator.
 *
 * @author oboehm
 */
@RunWith(Parameterized.class)
public class EMailValidatorTest {

    private final String emailAdresse;
    private final boolean valid;
    private final EMailValidator validator = new EMailValidator();

    /**
     * Hierueber werden die Test-Werte per Konstruktor "injected".
     *
     * @param adresse gueltige oder ungueltige E-Mail-Adresse
     * @param valid kennzeichnet die E-Mail-Adresse als gueltig oder ungueltgi
     */
    public EMailValidatorTest(String adresse, boolean valid) {
        this.emailAdresse = adresse;
        this.valid = valid;
    }

    /**
     * Hier setzen wir die verschiedenen Test-Adressen auf. Die Werte stammen aus
     * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
     * .
     *
     * @return Iterable of Array, wie vom Parameterized-Runner vorgegeben.
     */
    @Parameterized.Parameters(name = "{index}: email {0}: {1}")
    public static Collection<Object[]> data() {
        Collection<Object[]> values = new ArrayList<>();
        addValidAdressen(values, "mkyong@yahoo.com", "mkyong-100@yahoo.com", "mkyong.100@yahoo.com",
                "mkyong111@mkyong.com", "mkyong-100@mkyong.net", "mkyong.100@mkyong.com.au", "mkyong@1.com",
                "mkyong@gmail.com.com", "mkyong+100@gmail.com", "mkyong-100@yahoo-test.com");
        addInvalidAdressen(values, "mkyong", "mkyong@.com.my", "mkyong123@gmail.a", "mkyong123@.com",
                "mkyong123@.com.com", ".mkyong@mkyong.com", "mkyong()*@gmail.com", "mkyong@%*.com",
                "mkyong..2002@gmail.com", "mkyong.@gmail.com", "mkyong@mkyong@gmail.com", "mkyong@gmail.com.1a");
        return values;
    }

    private static void addValidAdressen(Collection<Object[]> values, String... adressen) {
        addAdressen(values, true, adressen);
    }

    private static void addInvalidAdressen(Collection<Object[]> values, String... adressen) {
        addAdressen(values, false, adressen);
    }

    private static void addAdressen(Collection<Object[]> values, boolean valid, String... adressen) {
        for(String adr : adressen) {
            Object[] array = { adr, valid };
            values.add(array);
        }
    }

    /**
     * Hier testen wir den SimpleValidator fuer die verschiedensten E-Mail-Adressen,
     * die ueber die Parameterized-Klasse hereinkommen.
     */
    @Test
    public void validateAdresse() {
        try {
            String validated = validator.validateAdresse(emailAdresse);
            assertEquals(emailAdresse, validated);
            assertThat(emailAdresse, valid, is(true));
        } catch (ValidationException expected) {
            assertThat(expected.getMessage(), valid, is(false));
        }
    }

}
