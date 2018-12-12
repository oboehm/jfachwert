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
 * (c)reated 10.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link IK}-Klasse.
 */
public final class IKTest extends AbstractFachwertTest {

    private final IK ik = createFachwert();

    /**
     * Zum Testen verwenden die IK, die auch in Wikipedia erwaehnt wird.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected IK createFachwert() {
        return IK.of("260326822");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalIK() {
        IK.of(260326823);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test8stelligesIK() {
        IK.of(12345671);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test10stelligesIK() {
        IK.of(1234567897);
    }

    @Test
    public void testGetKlassifikation() {
        assertEquals(26, ik.getKlassifikation());
    }

    @Test
    public void getGetRegionalbereich() {
        assertEquals(3, ik.getRegionalbereich());
    }

    @Test
    public void testGetSeriennummer() {
        assertEquals(2682, ik.getSeriennummer());
    }

    @Test
    public void testGetPruefziffer() {
        assertEquals(2, ik.getPruefziffer());
    }

}
