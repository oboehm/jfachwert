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

import org.junit.Before;
import org.junit.Test;
import patterntesting.runtime.junit.ImmutableTester;
import patterntesting.runtime.junit.SerializableTester;

import java.io.NotSerializableException;
import java.io.Serializable;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsInstanceOf.*;

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

    private Fachwert fachwert;

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden.
     *
     * @return Test-Objekt zum Testen
     */
    protected abstract Fachwert getFachwert();

    /**
     * Wir setzen den Fachwert nicht waehrend der Initialisierungsphase auf,
     * damit die abgeleiteten Test-Klassen die Chance haben, erst sauber ihre
     * Attribute zu initialiseren, ehe die getFachwert-Methode aufgerufen wird.
     */
    @Before
    public void setUpFachwert() {
        this.fachwert = this.getFachwert();
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
        assertThat("looks like default implementation", s, not(containsString("@")));
    }

}
