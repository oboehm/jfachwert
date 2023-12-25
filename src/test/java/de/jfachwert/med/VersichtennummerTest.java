/*
 * Copyright (c) 2023 by Oli B.
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
 * (c)reated 23.12.23 by oboehm
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Klasse VersichtennummerTest.
 *
 * @author oboehm
 * @since 5.1 (23.12.23)
 */
public final class VersichtennummerTest extends AbstractFachwertTest<String, Versichertennummer> {

    @Override
    protected Versichertennummer createFachwert(String code) {
        return Versichertennummer.of(code);
    }

    /**
     * Liefert eine Test-Versichtennummer als String.
     *
     * @return "DE41300606010006605605"
     */
    @Override
    protected String getCode() {
        return "A123456780";
    }

    @Test
    void testWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> new Versichertennummer("X1234"));
    }

    @Test
    void testTooMuchLetters() {
        assertThrows(IllegalArgumentException.class, () -> Versichertennummer.of("XX12345678"));
    }

}
