/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 19.02.2019 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;
import patterntesting.runtime.junit.ObjectTester;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit-Tests fuer {@link Name}-Klasse.
 *
 * @author oboehm
 */
public final class NameTest extends AbstractFachwertTest<String> {

    @Override
    protected Name createFachwert(String name) {
        return Name.of(name);
    }

    @Override
    protected String getCode() {
        return "Duck, Donald";
    }

    @Test
    public void testGetNachname() {
        assertEquals("Duck", Name.of("Duck, Donald").getNachname());
    }

    @Test
    public void testGetVorname() {
        assertEquals("Donald", Name.of("Duck, Donald").getVorname());
    }

    @Test
    public void testHasVorname() {
        assertFalse(Name.of("Duck").hasVorname());
    }

    /**
     * Umlaute sollten beim Vergleich keine Rolle spielen. Ebenso Gross- und
     * Kleinschreibung.
     */
    @Test
    public void testEquals() {
        Name mitUmlaute = Name.of("G\u00f6the, G\u00fcnther");
        Name ohneUmlaute = Name.of("goethe, guenther");
        ObjectTester.assertEquals(ohneUmlaute, mitUmlaute);
    }

}
