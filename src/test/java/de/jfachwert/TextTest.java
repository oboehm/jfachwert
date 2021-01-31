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

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link Text}-Klasse.
 *
 * @author oboehm
 */
public final class TextTest extends FachwertTest {

    private static final Logger LOG = Logger.getLogger(TextTest.class.getName());

    @Override
    protected Text createFachwert() {
        return new Text("Hallo Welt!");
    }

    /**
     * Es sollte nicht moeglich sein, einen Null-Text anzulegen. Normalerweise
     * wuerden wir hier eine IllegalArgumentException erwarten, aber Kotlin 1.4
     * macht hier schone eine Nullpointerexception.
     */
    @Test(expected = RuntimeException.class)
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
        MatcherAssert.assertThat(hello.getDistanz(hallo), lessThan(hello.getDistanz(world)));
    }

    /**
     * Die statische of-Methode sollte keine Dupliakte zurueckliefern.
     */
    @Test
    public void testOf() {
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

    @Test
    public void testReplaceUebung() {
        assertEquals(Text.of("Uebung"), Text.of("\u00dcbung").replaceUmlaute());
    }

    /**
     * Beim Adressvergleich von 300.000 wurde festgestellt, dass viel Zeit in
     * {@link Text#replaceUmlaute)()} verbraucht wurde. Dies ist zwar kein
     * echter Performance-Test, er gibt aber zumindestens Anhaltspunkte, ob
     * die Performance sich verbessert hat.
     * 
     * So dauert dieser Test auf einem Entwickler-Notebook von 2015 zwischen
     * 0,8 und 1,3 ms fuer die urspruengliche Implementierung. Nach der
     * Optimierung der Methode braucht sie jetzt zwischen 0,2 und 0,3 ms
     * (auf dem gleichen Rechner).
     *
     * @throws IOException the io exception
     */
    @Test
    public void testReplaceUmlautePerformance() throws IOException {
        Properties props = new Properties();
        try (InputStream istream = getClass().getResourceAsStream("/de/jfachwert/messages_de.properties")) {
            props.load(istream);
        }
        String s = props.toString();
        LOG.info("replaceUmlaute started");
        long t0 = System.nanoTime();
        String r = Text.replaceUmlaute(s);
        long t1 = System.nanoTime();
        LOG.info("replaceUmlaute started ended after " + (t1 - t0) / 1000000.0 + " ms");
        MatcherAssert.assertThat (r, not(containsString("W\u00e4hrung")));
    }

    @Test
    public void testEqualsIgnoreCase() {
        assertTrue(Text.of("hello").equalsIgnoreCase(Text.of("Hello")));
    }

    @Test
    public void testEqualsIgnoreUmlaute() {
        assertTrue(Text.of("Gruesse").equalsIgnoreUmlaute(Text.of("Gr\u00fc\u00dfe")));
    }

    @Test
    public void testEqualsIgnoreCaseAndUmlaute() {
        assertTrue(Text.of("GRUESSE").equalsIgnoreCaseAndUmlaute(Text.of("Gr\u00fc\u00dfe")));
    }

    @Test
    public void testToLowerCase() {
        assertEquals(Text.of("world"), Text.of("World").toLowerCase());
    }

    @Test
    public void testToUpperCase() {
        assertEquals(Text.of("WORLD"), Text.of("World").toUpperCase());
    }

    @Test
    public void testCompareTo() {
        Text abc = Text.of("abc");
        Text def = Text.of("def");
        MatcherAssert.assertThat(abc.compareTo(def), lessThan(0));
        MatcherAssert.assertThat(def.compareTo(abc), greaterThan(0));
    }
    
}
