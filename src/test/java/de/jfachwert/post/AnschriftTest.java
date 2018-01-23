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
package de.jfachwert.post;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import javax.validation.ValidationException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Unit-Teests fuer de.jfachwert.post.Anschrift.
 *
 * @author oboehm
 * @since 0.2 (12.05.2017)
 */
public final class AnschriftTest extends AbstractFachwertTest {

    /**
     * Zum Testen genererien wir eine einfache Anschrift.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Anschrift createFachwert() {
        Adresse adresse = new AdresseTest().createFachwert();
        return new Anschrift("Daisy", adresse);
    }

    /**
     * Eine Anschrift mit leerem Namen sollte nicht angelegt werden koennen.
     */
    @Test(expected = ValidationException.class)
    public void testCreateAnschriftWithInvalidAdresse() {
        Adresse entenhausen = createFachwert().getAdresse();
        new Anschrift("", entenhausen);
    }

    /**
     * Hier testen wir das Anlegen einer Anschrift mit einem Postfach.
     */
    @Test
    public void testAnschriftWithPostfach() {
        Postfach postfach = new PostfachTest().createFachwert();
        Anschrift anschrift = new Anschrift("Mr. Postman", postfach);
        assertEquals(postfach, anschrift.getPostfach());
        assertThat(anschrift.toString(), containsString(postfach.toString()));
    }

    /**
     * Test-Methode fuer {@link Anschrift#getAdressat()}.
     */
    @Test
    public void testGetAdressat() {
        Adresse adresse = new AdresseTest().createFachwert();
        Anschrift anschrift = new Anschrift("Dagobert, Duck", adresse);
        assertEquals(new Adressat("Dagobert, Duck"), anschrift.getAdressat());
    }

    /**
     * Test-Methode fuer {@link Anschrift#validate(String)}.
     */
    @Test
    public void testValidate() {
        Anschrift.validate("Donald Duck, 12345 Entenhausen, Gansstr. 23");
    }

    /**
     * Hier testen wir, ob bei der Zerlegung des Eingabe-Strings das Postfach
     * richtig erkannt wird.
     */
    @Test
    public void testAnschriftWithPostfachNummer() {
        String anschrift = "Musterfirma GmbH\nPostfach 12 34 56\n12350 Musterdorf";
        Anschrift musterfirma = new Anschrift(anschrift);
        assertEquals(new Postfach("Postfach 12 34 56\n12350 Musterdorf"), musterfirma.getPostfach());
    }

}
