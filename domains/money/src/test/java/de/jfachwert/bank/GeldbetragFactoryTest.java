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
 * (c)reated 01.08.24 by oboehm
 */
package de.jfachwert.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Tests fuer {@link GeldbetragFactory}-Klasse.
 *
 * @author oboehm
 */
public class GeldbetragFactoryTest {

    private GeldbetragFactory factory;

    @BeforeEach
    public void setUpFactory() {
        factory = new GeldbetragFactory();
        factory.setCurrency(Waehrung.DEFAULT);
    }

    @Test
    public void testCreate() {
        Geldbetrag zero = factory.create();
        assertEquals(Geldbetrag.ZERO, zero);
    }

}
