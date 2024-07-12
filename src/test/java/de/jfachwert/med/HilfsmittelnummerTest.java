/*
 * Copyright (c) 2024 by Oliver Boehm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 12.07.24 by oliver (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Tests fuer {@link Hilfsmittelnummer}-Klasse.
 */
public class HilfsmittelnummerTest extends AbstractFachwertTest<Long, Hilfsmittelnummer>  {

    /**
     * Erzeugt eine Hilfsmittelnummer zum Testen.
     *
     * @param nr Hilfsmittelnummer
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Hilfsmittelnummer createFachwert(String nr) {
        return Hilfsmittelnummer.of(nr);
    }

    /**
     * Das Beispiel dazu stammt aus <a href=
     * "https://sorgrollstuhltechnik.de/faq/was-ist-eine-hilfsmittelnummer/"
     * >Was ist eine Hilfsmittelnummer?</a>.
     *
     * @return "18.50.03.2006"
     */
    @Override
    protected String getCode() {
        return "18.50.03.2006";
    }

    @Test
    @Override
    public void testToString() {
        String s = "05.07.02.3011";
        Hilfsmittelnummer bandage = Hilfsmittelnummer.of(s);
        assertEquals(s, bandage.toString());
        assertEquals("0507023011", bandage.toShortString());
    }

}
