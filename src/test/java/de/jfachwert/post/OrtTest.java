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
import static org.junit.Assert.assertEquals;
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
     * Test-Methode fuer {@link Ort#getPLZ()}.
     */
    @Test
    public void testGetPLZ() {
        PLZ plz = new PLZ("73728");
        Ort esslingen = new Ort(plz, "Esslingen");
        assertEquals(plz, esslingen.getPLZ().get());
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

    /**
     * Wenn zwei Ort gleich heissen, aber nur zu einem Ort eine PLZ angegeben
     * wurde, kann man nicht 100% sicher sein, ob das auch der gewuenschte
     * Ort ist. Sollte fuer diesen Fall vielleicht besser eine Exception
     * geschmissen werden?
     */
    @Test
    public void testEqualsNotSatisfiable() {
        Ort neustadtHarz = new Ort(new PLZ("99762"), "Neustadt");
        Ort neustadt = new Ort("Neustadt");
        assertThat(neustadt, not(neustadtHarz));
    }

    /**
     * Wenn zwei Ort unterschiedlich sind, brauchen wir die PLZ nicht mehr, um
     * eine Ungleichheit festzustellen.
     */
    @Test
    public void testEqualsSatisfiable() {
        Ort neustadt = new Ort(new PLZ("99762"), "Neustadt");
        Ort altstadt = new Ort("Altstadt");
        assertThat(neustadt, not(altstadt));
    }

    /**
     * Wenn die PLZ zusammen mit dem Ort angegeben wird, sollte das erkannt
     * werden.
     */
    @Test
    public void testCtor() {
        Ort entenhausen = new Ort("12345 Entenhausen");
        assertEquals(new PLZ("12345"), entenhausen.getPLZ().get());
        assertEquals("Entenhausen", entenhausen.getName());
    }

    /**
     * Die Erkennung von "D-01001 Dresden" ist etwas anspruchsvoller, da die
     * PLZ nicht mit einer Ziffer beginnt.
     */
    @Test
    public void testCtorDresden() {
        Ort dresden = new Ort("D-01001 Dresden");
        assertEquals(new PLZ("D-01001"), dresden.getPLZ().get());
        assertEquals("Dresden", dresden.getName());
    }

    /**
     * Dieser Test dient zur Absicherung, ob die {@link Ort}-Klasse auch
     * korrekt mit Leerzeichen umgeht, und sie nicht irrtuemlich den vorderen
     * Teil als PLZ interpretiert.
     */
    @Test
    public void testHamburgAltona() {
        Ort altona = new Ort("Hamburg Altona");
        assertEquals("Hamburg Altona", altona.getName());
    }

}
