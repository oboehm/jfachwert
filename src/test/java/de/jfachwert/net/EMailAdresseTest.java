package de.jfachwert.net;/*
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
 * (c)reated 23.06.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.*;
import org.junit.*;

import javax.validation.*;

import static org.junit.Assert.*;

/**
 * Unit-Teests fuer de.jfachwert.net.EMailAdresse.
 *
 * @author oboehm
 */
public final class EMailAdresseTest extends AbstractFachwertTest {

    private final EMailAdresse testAdresse = createFachwert();

    /**
     * Zum Testen generieren wir eine gueltige Email-Adresse.
     *
     * @return eine Test-Email-Adresse
     */
    @Override
    protected EMailAdresse createFachwert() {
        return new EMailAdresse("test@jfachwert.de");
    }

    /**
     * Eine falsche E-Mail-Adresse sollte zurueckgewiesen werden.
     */
    @Test(expected = ValidationException.class)
    public void testInvalidEmailAdresse() {
        new EMailAdresse("gibts.net");
    }

    /**
     * Testmethode fuer {@link EMailAdresse#getLocalPart()}.
     */
    @Test
    public void testGetLocalPart() {
        assertEquals("test", testAdresse.getLocalPart());
    }

    /**
     * Testmethode fuer {@link EMailAdresse#getDomainPart()}.
     */
    @Test
    public void testGetDomainPart() {
        assertEquals(new Domainname("jfachwert.de"), testAdresse.getDomainPart());
    }

}
