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
 * (c)reated 16.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import patterntesting.runtime.junit.ImmutableTester;
import patterntesting.runtime.junit.ObjectTester;
import patterntesting.runtime.junit.SerializableTester;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Modifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * In der Klasse FachwertTest sind die Tests zusammengefasst, die fuer alle
 * Fachwert-Klassen gelten. Dies sind:
 * <ul>
 *     <li>Fachwerte sind unveraenderlich (immutable),</li>
 *     <li>Fachwerte sind serialisierbar,</li>
 *     <li>haben eine ueberschriebene toString-Methode</li>
 *     <li>und weitere, die mit Tests ueberprueft werden.</li>
 * </ul>
 * <p>
 * Anmerkung: vor 1.2 waren diese Tests in AbstractFachwertTest versammelt,
 * was aber zur Verwirrung gefuehrt hat. Jetzt ist AbstractFachwertTest fuer
 * die Fachwert-Klassen vorgesehen, die von AbstractFachwert abgeleitet sind.
 * </p>
 *
 * @author oboehm
 */
public class FachwertTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private KFachwert fachwert;

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden. Und zwar muss jedesmal
     * der gleiche Fachwert erzeugt werden, weil sonst der equals-Test nicht
     * funktioniert.
     * <p>
     * Da die Default-Konfiguration von Maven diese Klasse nicht ausblendet,
     * ist diese Klasse nicht mehr 'abstract', obwohl sie eigentlich als
     * abstrakte Oberklasse fuer alle Fachwert-Tests gedacht war. Daher
     * wird jetzt zum Testen ein Fachwert generiert.
     * </p>
     *
     * @return Test-Objekt zum Testen
     */
    protected KFachwert createFachwert() {
        return FachwertFactory.getInstance().getFachwert("Name", "Oli B.");
    }

    /**
     * Wir setzen den Fachwert nicht waehrend der Initialisierungsphase auf,
     * damit die abgeleiteten Test-Klassen die Chance haben, erst sauber ihre
     * Attribute zu initialiseren, ehe die getFachwert-Methode aufgerufen wird.
     */
    @BeforeEach
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
        assertThat(fachwert, IsInstanceOf.instanceOf(Serializable.class));
        SerializableTester.assertSerialization(fachwert);
    }

    /**
     * Hier ueberpruefen wir, ob die toString-Implementierung ueberschrieben
     * ist.
     */
    @Test
    public void testToString() {
        String s = fachwert.toString();
        assertThat("looks like default implementation", s, Matchers.not(
                CoreMatchers.containsString(fachwert.getClass().getName() + "@")));
    }

    /**
     * Alle Fachwerte sollten ableitbar sein, damit sie auch fuer eigene Zwecke
     * ueberschrieben werden koennen. Dazu duerfen die Klassen nicht final sein.
     */
    @Test
    public void testNotFinal() {
        Class<? extends KFachwert> clazz = fachwert.getClass();
        if (!clazz.isEnum()) {
            assertFalse(Modifier.isFinal(clazz.getModifiers()), clazz + " should be not final");
        }
    }

    /**
     * Falls die equals- und hashCode-Methode von {@link KFachwert}
     * ueberschrieben werden, wird die Korrektheit hier zur Sicherheit
     * ueberprueft.
     */
    @Test
    public void testEquals() {
        KFachwert one = this.createFachwert();
        KFachwert anotherOne = this.createFachwert();
        ObjectTester.assertEquals(one, anotherOne);
    }

    /**
     * Hier testen wir, ob die Serialisierung nach und von JSON funktioniert.
     */
    @Test
    public void testJsonSerialization() {
        String json = marshal(fachwert);
        KFachwert deserialized = unmarshal(json, fachwert.getClass());
        assertEquals(fachwert, deserialized, json);
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
            OBJECT_MAPPER.writeValue(writer, obj);
            writer.close();
            return writer.toString();
        } catch (IOException ex) {
            throw new IllegalArgumentException("could not marshal " + obj + " to JSON string", ex);
        }
    }

    /**
     * Wandelt den uebergebenen JSON-String in ein gewuenschtes Klassen-Objekt.
     *
     * @param <T>   the generic type
     * @param json  the json
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
