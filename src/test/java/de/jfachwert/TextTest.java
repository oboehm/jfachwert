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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer {@link Text}-Klasse.
 *
 * @author oboehm
 */
public final class TextTest extends FachwertTest {

    private static final Logger LOG = Logger.getLogger(TextTest.class.getName());
    private static final Set<Charset> availableCharsets = new HashSet<>(Arrays.asList(StandardCharsets.ISO_8859_1, Charset.forName("IBM850")));

    static {
        String probe = "a\u00e4\u00f6\u00fc\u00dfA\u00c4\u00d6\u00dc";
        for (Charset charset : Charset.availableCharsets().values()) {
            try {
                if (probe.equals(new String(probe.getBytes(charset), StandardCharsets.UTF_8))) {
                    availableCharsets.add(charset);
                }
            } catch (UnsupportedOperationException ex) {
                LOG.info(charset + " wird auf diesem System nicht unterstuetzt: " + ex);
            }
        }
    }

    @Override
    protected Text createFachwert() {
        return new Text("Hallo Welt!");
    }

    /**
     * Es sollte nicht moeglich sein, einen Null-Text anzulegen. Normalerweise
     * wuerden wir hier eine IllegalArgumentException erwarten, aber Kotlin 1.4
     * macht hier schone eine Nullpointerexception.
     */
    @Test
    public void testCtorNull() {
        assertThrows(RuntimeException.class, () -> new Text(null));
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
     * Beim Adressvergleich von 300.000 Adressen wurde festgestellt, dass viel Zeit
     * in {@link Text#replaceUmlaute)()} verbraucht wurde. Dies ist zwar kein
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
        assertThat (r, not(containsString("W\u00e4hrung")));
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
        assertThat(abc.compareTo(def), lessThan(0));
        assertThat(def.compareTo(abc), greaterThan(0));
    }

    @Test
    public void testIsPrintable() {
        assertTrue(Text.of("Gr\u00fc\u00dfe").isPrintable());
    }

    @Test
    public void testIsPrintableStatic() {
        assertTrue(Text.isPrintable("Gr\u00fc\u00dfe"));
    }

    @Test
    public void testIsPrintableAscii() {
        for (int code = 32; code <= 126; code++) {
            assertTrue(Text.of(Character.toString((char) code)).isPrintable());
        }
    }

    @Test
    public void testIsPrintableSonderzeichen() {
        assertTrue(Text.of("P\u00e4ragraph § 218.").isPrintable());
    }

    /**
     * Test fuer Issue
     * <a href="https://github.com/oboehm/jfachwert/issues/16">#16</a>.
     */
    @Test
    public void testIsPrintableSpanischeZeichen() {
        Text x = Text.of("Por qu\u00e9 el espa\u00f1ol es el \u00fanico idioma que utiliza signos de interrogaci\u00f3n (\u00bf?) y admiraci\u00f3n (\u00a1!) dobles.");
        assertTrue(x.isPrintable());
    }

    @Test
    public void testIsPrintableCurrencies() {
        for (Currency c : Currency.getAvailableCurrencies()) {
            String s = String.format("%s: %s (%s)", c.getCurrencyCode(), c.getSymbol(), c);
            assertTrue(Text.of(s).isPrintable(), s);
        }
    }

    @Test
    public void testIsNotPrintable() {
        String invalid = new String("Gr\u00fc\u00dfe".getBytes(StandardCharsets.ISO_8859_1));
        assertFalse(Text.of(invalid).isPrintable());
    }

    @Test
    public void testDetectUTF8() {
        assertEquals(StandardCharsets.UTF_8, Text.detectCharset("B\u00f6hm"));
    }

    @Test
    public void testDetectLatin1() {
        assertEquals(StandardCharsets.ISO_8859_1, Text.detectCharset("B\u00c3\u00b6hm"));
    }

    @Test
    public void testIsLatin1() {
        Text t = Text.of("B\u00c3\u00b6hm");
        assertTrue(t.isCharset(StandardCharsets.ISO_8859_1));
    }

    @Test
    public void testConvertTo() {
        assertEquals(Text.of("B\u00c3\u00b6hm"), Text.of("B\u00f6hm").convertTo(StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8));
    }

    @Test
    public void testConvertToLatin1() {
        assertEquals("B\u00c3\u00b6hm", Text.convert("B\u00f6hm", StandardCharsets.ISO_8859_1));
    }

    @DisplayName("Konvertierung")
    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("encodingParameters")
    void testConvert(Charset charset) {
        String text = "H\u00e4llo W\u00f6rld";
        String converted = Text.convert(text, charset, StandardCharsets.UTF_8);
        assertEquals(text, Text.convert(converted, StandardCharsets.UTF_8, charset));
    }

    @DisplayName("Charset-Erkennung")
    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("encodingParameters")
    void testDetect(Charset charset) {
        Text utf8 = Text.of("Gr\u00fc\u00dfe aus \u00c4gypten, \u00d6sterreich, \ud801\udc00");
        Text converted = utf8.convertTo(charset, StandardCharsets.UTF_8);
        Collection<Charset> charsets = converted.detectCharsets();
        assertThat(charsets, hasItem(charset));
        assertThat(charsets, hasItem(converted.detectCharset()));
    }

    static Stream<Arguments> encodingParameters() {
        return availableCharsets.stream().map(Arguments::of);
    }

}
