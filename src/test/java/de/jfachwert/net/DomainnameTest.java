/*
 * Copyright (c) 2017-2022 by Oliver Boehm
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
 * (c)reated 08.08.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Text;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link Domainname}-Klasse.
 *
 * @author oboehm
 */
public class DomainnameTest extends AbstractFachwertTest<String, Text> {

    private final Domainname domainName = new Domainname("www.jfachwert.de");

    /**
     * Als Test-Objekt verwenden wir einen gueltigen Domainnamen.
     *
     * @param name Domainname
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Domainname createFachwert(String name) {
        return Domainname.of(name);
    }

    /**
     * Erzeugt einen Code fuer einen gueltigen Domainnamen.
     *
     * @return "jfachwert.de"
     */
    @Override
    protected String getCode() {
        return "jfachwert.de";
    }

    /**
     * Test mit einem ungueltigen Domainnamen.
     */
    @Test
    public void testDomainnameInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Domainname("-abc"));
    }

    /**
     * Bei Domainnamen spielt Gross-/Kleinschreibung keine Rolle. Das sollte
     * dann auch fuer die equals-Methode gelten.
     */
    @Test
    public void testEqualsUppercase() {
        Domainname lowercase = new Domainname("aosd.de");
        Domainname uppercase = new Domainname("AOSD.de");
        assertEquals(lowercase, uppercase);
    }

    /**
     * Testmethode fuer {@link Domainname#getTLD()}.
     */
    @Test
    public void testGetTLD() {
        assertEquals("de", domainName.getTLD().toString());
    }

    /**
     * Testmethode fuer {@link Domainname#getLevelDomain(int)}.
     */
    @Test
    public void testGetSecondLevelDomain() {
        Domainname www = new Domainname("www.jfachwert.de");
        Domainname jfachwert = new Domainname("jfachwert.de");
        assertEquals(www.getLevelDomain(2), jfachwert);
        assertEquals(www.getLevelDomain(3), www);
    }

    /**
     * Weitere Testmethode fuer {@link Domainname#getLevelDomain(int)}.
     */
    @Test
    public void testGetFourthLevelDomain() {
        assertThrows(IllegalArgumentException.class, () -> domainName.getLevelDomain(4));
    }

}
