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
 * (c)reated 16.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Unit-Tests fuer {@link BSNR}-Klasse.
 */
public class BSNRTest extends AbstractFachwertTest<Integer> {

    /**
     * Erzeugt eine BSNR zum Testen.
     *
     * @param nr BS-Nummer
     * @return Test-Objekt zum Testen
     */
    @Override
    protected BSNR createFachwert(String nr) {
        return BSNR.of(nr);
    }

    /**
     * Das Beispiel dazu stammt aus
     * http://media.dav-medien.de/sample/100006566_p__v1.pdf.
     *
     * @return "345678975"
     */
    @Override
    protected String getCode() {
        return "345678975";
    }

    @Test
    public void testBSNRmit0() {
        BSNR nr = BSNR.of(12345678);
        assertEquals("012345678", nr.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test10stelligeBSNR() {
        BSNR.of(1234567890);
    }

}
