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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Test fuer {@link SteuerIdNr}-Klasse. Die Beispiel-Id stammt aus
 * https://www.lohnsteuer-kompakt.de/fag/2016/39/wo_kann_ich_meine_steuer-identifikationsnummer_finden.
 *
 * @author oboehm
 */
public final class SteuerIdNrTest extends SteuernummerTest {

    /**
     * Die Steuernummer aus diesem Beispiel stammt aus Wikipedia.
     *
     * @param nr z.B. "12365489753"
     * @return eine Steuer-IdNr
     */
    protected SteuerIdNr createFachwert(String nr) {
        return SteuerIdNr.of(nr);
    }

    /**
     * Die Steuernummer aus diesem Beispiel stammt aus Wikipedia.
     *
     * @return "12365489753" 11-stellige Nummer
     */
    @Override
    protected String getCode() {
        return "12365489753";
    }

    /**
     * Ungueltige Steuernummern sollten nicht erzeugt werden koennen.
     */
    @Test
    public void testSteuerIdNrInvalid() {
        assertThrows(IllegalArgumentException.class, () -> SteuerIdNr.of("12365489750"));
    }

    /**
     * Alte Steuernnummern und Nummer, die zu lang sind, sollten auch nicht
     * angelegt werden koennen.
     */
    @Test
    public void testSteuerIdNrZuLang() {
        assertThrows(IllegalArgumentException.class, () -> new SteuerIdNr("1121081508150"));
    }

}
