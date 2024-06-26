/*
 * Copyright (c) 2017-2024 by Oliver Boehm
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
 * (c)reated 04.09.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import patterntesting.runtime.junit.ObjectTester;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-Tests fuer {@link Telefonnummer}-Klasse.
 *
 * @author oboehm
 */
public final class TelefonnummerTest extends AbstractFachwertTest<String, Text> {

    /** Telefonnumer aus Spider Murphy's "Skandal im Sperrbezirik". */
    private final Telefonnummer rosi = new Telefonnummer("+49 (0)811 32 16 8");

    private final String nummer;
    private Telefonnummer rosisNummer;

    public TelefonnummerTest() {
        this.nummer = "+49 811 3216-8";
    }

    /**
     * Hier setzen wir immer die gleiche Telefonnumern (Rosis Telefonnummer
     * aus "Skandal im Sperrbezirk" von der Spider Murphy Gang) in
     * verschiedenen Formaten zum Testen auf.
     *
     * @return Iterable of Array, wie vom Parameterized-Runner vorgegeben.
     */
    public static Collection<Object[]> data() {
        Collection<Object[]> values = new ArrayList<>();
        values.add(new Object[] { "+49 811 3216-8" });
        values.add(new Object[] { "+49 (0)811 32 16 - 8" });
        values.add(new Object[] { "+49(0)811/3216-8" });
        return values;
    }

    @BeforeEach
    public void setUpTelefonnummer() {
        this.rosisNummer = createFachwert(this.nummer);
    }

    /**
     * Zum Testen nehmen wir eine fiktive Telefonnummer (aus Wikipedia).
     *
     * @param nr fiktive Telefonnummer
     * @return "+49 30 12345-67"
     */
    @Override
    protected Telefonnummer createFachwert(String nr) {
        return Telefonnummer.of(nr);
    }

    /**
     * Liefert eine gueltige Telefonnummer.
     *
     * @return z.B. "+49(0)811/3216-8"
     */
    @Override
    protected String getCode() {
        return this.nummer;
    }

    @Override
    protected String getInvalidCode() {
        return "ABC-";
    }

    /**
     * Auch wenn Telefonnummern unterschiedlich formattiert sind, sollten
     * gleiche Nummern als gleich erkannt werden.
     */
    @Test
    public void testEquals() {
        ObjectTester.assertEquals(rosi, new Telefonnummer("+49 811/32168"));
    }

    /**
     * Die Inlandsnummer sollte mit der fuehrenden 0 der Vorwahl anfangen.
     */
    @ParameterizedTest
    @ValueSource(strings = {"+49 811 3216-8", "49 (0)811 32 16 - 8", "+49(0)811/3216-8"})
    public void testGetInlandsnummer(String nr) {
        Telefonnummer telnr = Telefonnummer.of(nr);
        Telefonnummer inland = telnr.getInlandsnummer();
        assertEquals(new Telefonnummer("0811/32168"), inland);
        assertThat(inland.toString(), startsWith("0811"));
    }

    @Test
    public void testGetRufnummer() {
        Telefonnummer rufnummer = rosisNummer.getRufnummer();
        assertEquals(new Telefonnummer("32168"), rufnummer);
    }

    /**
     * Testmethode fuer {@link Telefonnummer#toDinString()}.
     */
    @Test
    public void testToDinString() {
        assertEquals("+49 811 3216-8", rosisNummer.toDinString());
    }

    /**
     * Testmethode fuer {@link Telefonnummer#toDinString()}.
     */
    @Test
    public void testToDinStringNational() {
        assertEquals("0811 3216-8", rosisNummer.getInlandsnummer().toDinString());
    }

    /**
     * Testmethode fuer {@link Telefonnummer#toE123String()}.
     */
    @Test
    public void testToE123String() {
        assertEquals("+49 811 3216 8", rosisNummer.toE123String());
    }

    /**
     * Testmethode fuer {@link Telefonnummer#toE123String()}.
     */
    @Test
    public void testToE123StringNational() {
        assertEquals("(0811) 3216 8", rosisNummer.getInlandsnummer().toE123String());
    }

    /**
     * Testmethode fuer {@link Telefonnummer#toURI()}.
     */
    @Test
    public void testToURI() {
        assertEquals("tel:+49-811-3216-8", rosisNummer.toURI().toString());
    }

    /**
     * Testmethode fuer {@link Telefonnummer#Telefonnummer(URI)}.
     */
    @Test
    public void testTelefonnumerURI() {
        URI uri = rosisNummer.toURI();
        assertEquals(rosisNummer, new Telefonnummer(uri));
    }

    /**
     * Testmethode fuer {@link Telefonnummer#getLaenderkennzahl()}
     */
    @ParameterizedTest
    @ValueSource(strings = {"0049 811 3216-8", "49 (0)811 32 16 - 8", "+49(0)811/3216-8"})
    public void testGetLandeskennzahl(String nr) {
        Telefonnummer telnr = Telefonnummer.of(nr);
        assertEquals(Optional.of("+49"), telnr.getLaenderkennzahl());
    }

    /**
     * Testmethode fuer {@link Telefonnummer#getVorwahl()}.
     */
    @Test
    public void testGetVorwahl() {
        assertEquals("0811", rosisNummer.getVorwahl());
    }

    @Test
    void toPackedDecimal() {
        assertEquals("012", Telefonnummer.of("012").toPackedDecimal().toString());
        assertEquals("012", Telefonnummer.of("(0)12").toPackedDecimal().toString());
    }

}
