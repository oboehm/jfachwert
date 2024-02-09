/*
 * Copyright (c) 2024 by Oli B.
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
 * (c)reated 09.02.24 by oboehm
 */
package de.jfachwert.med;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-Tests fuer ZANR.
 *
 * @author oboehm
 */
public class ZANRTest extends LANRTest {

    /**
     * Zum Testen verwenden wir die ZANR-Klasse
     *
     * @param nr LA-Nummer
     * @return Test-Objekt zum Testen
     */
    @Override
    protected ZANR createFachwert(String nr) {
        return ZANR.of(nr);
    }

    @Test
    @Override
    public void testIstKeinZahnarzt() {
        assertTrue(ZANR.of(345678975).isZahnarzt());
    }

}
