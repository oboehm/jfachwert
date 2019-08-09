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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 24.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Text;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Test fuer {@link UStIdNr}-Klasse.
 *
 * @author oboehm
 */
public final class UStIdNrTest extends AbstractFachwertTest<String, Text> {

    /**
     * Zum Testen wird hier eine deutsche Umsatzsteuer-IdNr mit gueltiger
     * Pruefziffer verwendet.
     *
     * @param nr gueltige Umsatzsteuer-IdNr
     * @return Test-Objekt zum Testen
     */
    @Override
    protected UStIdNr createFachwert(String nr) {
        return UStIdNr.of(nr);
    }

    /**
     * Zum Testen wird hier eine deutsche Umsatzsteuer-IdNr mit gueltiger
     * Pruefziffer zurueckgegeben.
     *
     * @return "DE 136 695 976"
     */
    @Override
    protected String getCode() {
        return "DE 136 695 976";
    }

    /**
     * Falls die UStIdNr eine falsche Pruefziffer enthaelt, soll sie nicht
     * erzeugt werden koennen.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUStIdNrInvalid() {
        UStIdNr.of("DE136695970");
    }

    /**
     * Testmethode fuer {@link UStIdNr#getLand()}.
     */
    @Test
    public void testGetLand() {
        assertEquals(new Locale("DE"), new UStIdNr("DE136695976").getLand());
    }

}
