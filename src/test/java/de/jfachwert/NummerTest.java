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
 * (c)reated 26.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link Nummer}-Klasse.
 *
 * @author oboehm
 */
public final class NummerTest extends AbstractFachwertTest {

    @Override
    protected Nummer createFachwert() {
        return new Nummer(42);
    }

    /**
     * Test-Methode fuer {@link Nummer#intValue()}.
     */
    @Test
    public void testGet() {
        Nummer nummer = new Nummer("4711");
        assertEquals(4711, nummer.intValue());
    }

}
