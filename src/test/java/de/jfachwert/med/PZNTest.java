/*
 * Copyright (c) 2020 by Oliver Boehm
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
 * (c)reated 25.05.2020 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Unit-Tests fuer {@link PZN}-Klasse.
 */
public final class PZNTest extends AbstractFachwertTest<Integer, PZN> {

    /**
     * Erzeugt eine PZN zum Testen.
     *
     * @param nr PZN-Nummer
     * @return Test-Objekt zum Testen
     */
    @Override
    protected PZN createFachwert(String nr) {
        return PZN.of(nr);
    }

    /**
     * Das Beispiel dazu stammt aus
     * https://www.ifaffm.de/mandanten/1/documents/04_ifa_coding_system/IFA-Info_Pr%C3%BCfziffer_PZN_PPN_UDI_DE.pdf
     *
     * @return "2758089"
     */
    @Override
    protected String getCode() {
        return "2758089";
    }

    @Test
    public void testPZNmit0() {
        PZN nr = PZN.of(1234567);
        assertEquals("PZN-01234567", nr.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test10stelligeBSNR() {
        PZN.of(1234567890);
    }

    /**
     * PZNs koennen fuehrende Nullen enthalten.
     */
    @Test
    public void testOf() {
        PZN nr = PZN.of("02758089");
        assertThat(nr.toString(), containsString("02758089"));
    }

}
