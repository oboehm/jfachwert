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
 * (c)reated 17.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert;

import org.junit.Test;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link Text}-Klasse.
 *
 * @author oboehm
 */
public final class TextTest extends FachwertTest {

    @Override
    protected Text createFachwert() {
        return new Text("Hallo Welt!");
    }

    /**
     * Es sollte nicht moeglich sein, einen Null-Text anzulegen.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCtorNull() {
        new Text(null);
    }

    @Test
    public void testgetDistanzKommuntativ() {
        Text hello = new Text("hello");
        Text hallo = new Text("hallo");
        assertEquals(hello.getDistanz(hallo), hallo.getDistanz(hello));
    }

    @Test
    public void testgetDistanzZero() {
        Text hello = Text.of("hello");
        assertEquals(0, hello.getDistanz("hello"));
    }

    @Test
    public void testGetDistanz() {
        Text hello = new Text("hello");
        Text hallo = new Text("hallo");
        Text world = new Text("world");
        assertThat(hello.getDistanz(hallo), lessThan(hello.getDistanz(world)));
    }

    /**
     * Die statische to-Methode sollte keine Dupliakte zurueckliefern.
     */
    @Test
    public void testTo() {
        assertSame(Text.of("hello"), Text.of("hello"));
    }

    @Test
    public void testReplaceUmlaute() {
        Text gruesse = Text.of("Gr\u00fc\u00dfe").replaceUmlaute();
        assertEquals(Text.of("Gruesse"), gruesse);
    }

    @Test
    public void testReplaceLazlo() {
        assertEquals(Text.of("Lazlo"), Text.of("L\u00e1zl\u00f3").replaceUmlaute());
    }

    @Test
    public void testReplaceRene() {
        assertEquals(Text.of("Rene"), Text.of("R\u00e9n\u00e9").replaceUmlaute());
    }

    @Test
    public void testReplaceCitroen() {
        assertEquals(Text.of("Citroen"), Text.of("Citro\u00ebn").replaceUmlaute());
    }

}
