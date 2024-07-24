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
import de.jfachwert.Text;
import org.junit.jupiter.api.Test;
import patterntesting.runtime.junit.ObjectTester;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link Name}-Klasse.
 *
 * @author oboehm
 */
public final class NameTest extends AbstractFachwertTest<String, Text> {

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

    @Test
    public void testGetVornamenListe() {
        List<String> vornamen = Name.of("Was, Karl Otto").getVornamenListe();
        assertEquals(2, vornamen.size());
        assertEquals("Karl", vornamen.get(0));
        assertEquals("Otto", vornamen.get(1));
    }

    @Test
    public void testGetLeereVornamenListe() {
        List<String> vornamen = Name.of("Nobody").getVornamenListe();
        assertThat(vornamen, empty());
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

    @Test
    public void testNotEquals() {
        Name tick = Name.of("Duck, Tick");
        Name trick = Name.of("Duck, Trick");
        assertNotEquals(tick, trick);
    }

    @Test
    public void testEqualsWithWhitespaces() {
        assertTrue(Name.of("Duck,  Tick ").equalsSemantic(Name.of(" Duck ,Tick")));
    }

    @Test
    public void testEqualsExact() {
        Name a = new Name("hugo");
        Name b = new Name("Hugo");
        Name c = new Name("Hugo");
        assertFalse(a.equalsExact(b));
        assertTrue(b.equalsExact(c));
        ObjectTester.assertEquals(a, b);
    }

    @Test
    public void testEqualsSemanticKarlHeinz() {
        Name a = Name.of("Ott, Karl Heinz");
        Name b = Name.of("Ott, Karl-Heinz");
        Name c = Name.of("Ott, Karlheinz");
        assertTrue(a.equalsSemantic(b));
        assertTrue(a.equalsSemantic(c));
        assertTrue(b.equalsSemantic(c));
        assertTrue(b.equalsSemantic(a));
        assertTrue(c.equalsSemantic(a));
        assertTrue(c.equalsSemantic(b));
    }

    @Test
    public void testEquals2Vornamen() {
        Name karl = Name.of("Otto, Karl");
        Name karlheinz = Name.of("Otto, Karl Heinz");
        ObjectTester.assertEquals(karl, karlheinz);
    }

    @Test
    public void testMMustermann() {
        Name mustermann = Name.of("M. Mustermann");
        assertEquals("Mustermann", mustermann.getNachname());
        assertEquals("M.", mustermann.getVorname());
    }

    @Test
    public void testDonaldDuck() {
        Name donald = Name.of("Donald Duck");
        assertEquals("Duck", donald.getNachname());
        assertEquals("Donald", donald.getVorname());
    }

    @Test
    public void testGetNamensListe() {
        Name ewing = Name.of("J.R.Ewing");
        assertThat(ewing.getNamensListe(), contains("J.", "R.", "Ewing"));
    }

}
