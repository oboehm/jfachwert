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
 * (c)reated 13.04.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.AbstractFachwertTest;
import de.jfachwert.Fachwert;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Unit-Tests fuer die Ort-Klasse.
 *
 * @author oboehm
 */
public class OrtTest extends AbstractFachwertTest {

    /**
     * Hier nehmen wir zum Testen die kleinste Stadt Deutschlands mit
     * etwa 300 Einwohnern.
     *
     * @return den Ort Arnis in Schleswig Holstein
     */
    @Override
    protected Fachwert createFachwert() {
        return new Ort("Arnis");
    }

    /**
     * Es gibt in Deutschland ueber ein Dutzende Staedten mit dem Namen
     * "Neustadt", die sich in der PLZ unterscheiden und daher nicht gleich
     * sind.
     */
    @Test
    public void testNotEquals() {
        Ort neustadtHarz = new Ort(new PLZ("99762"), "Neustadt");
        Ort neustadtDonau = new Ort(new PLZ("93333"), "Neustadt");
        assertThat(neustadtDonau, not(neustadtHarz));
    }

}
