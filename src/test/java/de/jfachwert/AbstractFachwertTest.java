/*
 * Copyright (c) 2017 by Oliver Boehm
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
 * (c)reated 11.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import patterntesting.runtime.junit.*;

import java.io.*;
import java.lang.reflect.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

/**
 * In der Klasse AbstractFachwertTest sind die Tests zusammengefasst, die fuer
 * alle Fachwert-Klassen gelten. Dies sind:
 * <ul>
 *     <li>Fachwerte sind unveraenderlich (immutable),</li>
 *     <li>Fachwerte sind serialisierbar,</li>
 *     <li>haben eine ueberschriebene toString-Methode</li>
 *     <li>und weitere, die mit Tests ueberprueft werden.</li>
 * </ul>
 *
 * @author <a href="ob@aosd.de">oliver</a>
 */
public abstract class AbstractFachwertTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private Fachwert fachwert;

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden. Und zwar muss jedesmal
     * der gleiche Fachwert erzeugt werden, weil sonst der equals-Test nicht
     * funktioniert.
     *
     * @return Test-Objekt zum Testen
     */
    protected abstract Fachwert createFachwert();

    /**
     * Wir setzen den Fachwert nicht waehrend der Initialisierungsphase auf,
     * damit die abgeleiteten Test-Klassen die Chance haben, erst sauber ihre
     * Attribute zu initialiseren, ehe die getFachwert-Methode aufgerufen wird.
     */
    @Before
    public void setUpFachwert() {
        this.fachwert = this.createFachwert();
    }

    /**
     * Hiermit stellen wir sicher, dass Fachwerte unveraenderlich sind.
     */
    @Test
    public void testImmutable() {
        ImmutableTester.assertImmutable(fachwert.getClass());
    }

    /**
     * Hiermit pruefen wir die Serialisierbarkeit.
     *
     * @throws NotSerializableException the not serializable exception
     */
    @Test
    public void testSerializable() throws NotSerializableException {
        assertThat(fachwert, instanceOf(Serializable.class));
        SerializableTester.assertSerialization(fachwert);
    }

    /**
     * Hier ueberpruefen wir, ob die toString-Implementierung ueberschrieben
     * ist.
     */
    @Test
    public void testToString() {
        String s = fachwert.toString();
        assertThat("looks like default implementation", s, not(containsString(fachwert.getClass().getName() + "@")));
    }

    /**
     * Alle Fachwerte sollten ableitbar sein, damit sie auch fuer eigene Zwecke
     * ueberschrieben werden koennen. Dazu duerfen die Klassen nicht final sein.
     */
    @Test
    public void testNotFinal() {
        Class<? extends Fachwert> clazz = fachwert.getClass();
        assertFalse(clazz + " should be not final", Modifier.isFinal(clazz.getModifiers()));
    }

    /**
     * Falls die equals- und hashCode-Methode von {@link AbstractFachwert}
     * ueberschrieben werden, wird die Korrektheit hier zur Sicherheit
     * ueberprueft.
     */
    @Test
    public void testEquals() {
        Fachwert one = this.createFachwert();
        Fachwert anotherOne = this.createFachwert();
        ObjectTester.assertEquals(one, anotherOne);
    }

    /**
     * Hier testen wir, ob die Serialisierung nach und von JSON funktioniert.
     */
    @Test
    public void testJsonSerialization() {
        String json = marshal(fachwert);
        Fachwert deserialized = unmarshal(json, fachwert.getClass());
        assertEquals(json, fachwert, deserialized);
    }

    /**
     * Wandelt ein Klassen-Objekt in einen JSON-String.
     *
     * @param <T> the generic type
     * @param obj the obj
     * @return the string
     */
    protected static <T> String marshal(final T obj) {
        try {
            StringWriter writer = new StringWriter();
            OBJECT_MAPPER.writeValue( writer, obj );
            writer.close();
            return writer.toString();
        } catch (IOException ex) {
            throw new IllegalArgumentException("could not marshal " + obj + " to JSON string", ex);
        }
    }

    /**
     * Wandelt den uebergebenen JSON-String in ein gewuenschtes Klassen-Objekt.
     *
     * @param <T> the generic type
     * @param json the json
     * @param clazz the clazz
     * @return the t
     */
    protected static <T> T unmarshal(final String json, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException ex) {
            throw new IllegalArgumentException("could not unmarshall '" + json + "' to " + clazz, ex);
        }
    }

}
