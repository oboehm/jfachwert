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
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.validation.ValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit-Tests fuer {@link IK}-Klasse.
 */
public final class IKTest extends AbstractFachwertTest<Integer, IK> {

    private final IK ik = IK.of("260326822");

    /**
     * Zum Testen verwenden die IK, die auch in Wikipedia erwaehnt wird.
     *
     * @param nr IK-Nummer
     * @return Test-Objekt zum Testen
     */
    @Override
    protected IK createFachwert(String nr) {
        return IK.of(nr);
    }

    /**
     * Zum Testen verwenden die IK, die auch in Wikipedia erwaehnt wird.
     *
     * @return "260326822"
     */
    @Override
    protected String getCode() {
        return "260326822";
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalIK() {
        IK.of(263456789);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test8stelligesIK() {
        IK.of(12345671);
    }

    @Test(expected = ValidationException.class)
    public void testValidate() {
        new IK.Validator().validate(263456789);
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

    @Test
    public void testIKs() throws IOException {
        try (InputStream istream = IKTest.class.getResourceAsStream("/de/jfachwert/med/test-iks.txt")) {
            assertNotNull(istream);
            for (String ik : IOUtils.readLines(istream, StandardCharsets.US_ASCII)) {
                IK.of(ik);
            }
        }
    }

}
