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
 * (c)reated 01.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link GeldbetragFactory}-Klasse.
 *
 * @author oboehm
 */
public final class GeldbetragFactoryTest {
    
    private final GeldbetragFactory factory = new GeldbetragFactory();

    /**
     * Tested das Anlegen eines Geldbetrags ueber die GeldbetragFactory.
     */
    @Test
    public void testCreate() {
        Geldbetrag zero = factory.create();
        assertEquals(Geldbetrag.ZERO, zero);
    }

    /**
     * Tested das Setzen eines Geldbetrags.
     */
    @Test
    public void testSetNumber() {
        factory.setNumber(BigDecimal.ONE);
        factory.setCurrency("CHF");
        assertEquals(Geldbetrag.valueOf("1 CHF"), factory.create());
    }
    
}
