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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 11.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * In der Klasse AbstractFachwertTest sind die Tests zusammengefasst, die fuer
 * alle Fachwert-Klassen gelten, die von AbstractFachwert abgeleietet sind.
 * <p>
 * Anmerkung: vor 1.2 waren hier alle gemeinsamen Tests fuer alle Fachwert-
 * Implementierungen versammelt, was aber zur Verwirrung gefuehrt hat.
 * Jetzt ist AbstractFachwertTest fuer die Fachwert-Klassen vorgesehen,
 * die von AbstractFachwert abgeleitet sind.
 * </p>
 *
 * @param <T> the type parameter
 * @author <a href="ob@aosd.de">oliver</a>
 */
public abstract class AbstractFachwertTest<T extends Serializable, S extends AbstractFachwert<T, S>> extends FachwertTest {

    private static final Logger LOG = Logger.getLogger(AbstractFachwertTest.class.getName());

    /**
     * Zum Testen erstellen wir hierueber ein Test-Objekt.
     *
     * @param code den Code zum Erstellen des Test-Objekts
     * @return Test -Objekt zum Testen
     */
    protected abstract AbstractFachwert<T, S> createFachwert(String code);

    /**
     * Erzeugt eine Code, der zum Erstellen eines Test-Objekts verwendet wird.
     * Er sollte von abgeleiteten Klassen ueberschrieben werden, wenn damit
     * kein gueltiges Test-Objekt erstellt werden kann.
     *
     * @return "3.14"
     */
    protected String getCode() {
        return Double.toString(3.14);
    }

    /**
     * Erzeugt eine ungueltigen Code, der zum Erstellen eines Test-Objekts
     * verwendet wird. Er sollte von abgeleiteten Klassen ueberschrieben
     * werden, wenn diese Implementierung einen gueltigen Code zurueckliefert.
     *
     * @return ""
     */
    protected String getInvalidCode() {
        return "";
    }

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden. Und zwar muss jedesmal
     * der gleiche Fachwert erzeugt werden, weil sonst der equals-Test nicht
     * funktioniert.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected KFachwert createFachwert() {
        return createFachwert(getCode());
    }

    /**
     * Zum Testen erzeugen wir hier zwei gleiche, aber nicht diesselben
     * Strings. Daraus sollten zwei gleiche Fachwerte mit demselben internen
     * Code erzeugt werden.
     */
    @Test
    public void testNoDuplicate() {
        String s1 = getCode();
        String s2 = new StringBuilder(getCode()).toString();
        assertNotSame(s1, s2);
        AbstractFachwert f1 = createFachwert(s1);
        AbstractFachwert f2 = createFachwert(s2);
        assertSame(f1.getCode(), f2.getCode());
        assertSame(f1, f2);
    }

    /**
     * Testfall fuer Issue #29. Hiermit wird ueberprueft, dass nach Aufruf
     * des GCs auch der verwendete WeakCache auch tatsaechlich aufgeraeumt
     * wurde.
     */
    @Test
    public void testOfCaching() {
        String s = getCode();
        AbstractFachwert f1 = createFachwert(s);
        if (forceGC()) {
            AbstractFachwert f2 = createFachwert(s);
            assertNotSame(f1, f2);
            assertEquals(f1, f2);
        } else {
            LOG.info("GC wurde nicht durchgefuehrt.");
        }
    }

    @Test
    public void isValid() {
        assertTrue(createFachwert().isValid());
    }

    @Test
    public void suppressInvalid() {
        assertThrows(IllegalArgumentException.class, () -> createFachwert(getInvalidCode()));
    }

}
