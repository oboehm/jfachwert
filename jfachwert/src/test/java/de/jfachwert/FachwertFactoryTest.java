/*
 * Copyright (c) 2018-2024 by Oliver Boehm
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
 * (c)reated 13.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert;

import de.jfachwert.bank.BIC;
import de.jfachwert.bank.IBAN;
import de.jfachwert.math.Primzahl;
import de.jfachwert.steuer.UStIdNr;
import org.junit.jupiter.api.Test;
import clazzfish.monitor.ClasspathMonitor;

import de.jfachwert.pruefung.exception.ValidationException;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collection;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests fuer {@link FachwertFactory}-Klasse. Dieses Klasse befindet sich
 * hier in diesem "Sammel"-Modul, weil hier die Registrierung der Klassen aus
 * den verschiedenen Modulen besser getestet werden kann.
 *
 * @author oboehm
 */
public class FachwertFactoryTest {
    
    private static final FachwertFactory FACTORY = FachwertFactory.getInstance();

    /**
     * Test-Methode fuer {@link FachwertFactory#getFachwert(String, Serializable...)}.
     */
    @Test
    public void testGetFachwertClass() {
        KFachwert iban = FACTORY.getFachwert(IBAN.class, "DE41300606010006605605");
        assertEquals(new IBAN("DE41300606010006605605"), iban);
    }

    /**
     * Test-Methode fuer {@link FachwertFactory#getFachwert(String, Serializable...)}
     */
    @Test
    public void testGetFachwertString() {
        KFachwert bic = FACTORY.getFachwert("BIC", "BELADEBEXXX");
        assertEquals(new BIC("BELADEBEXXX"), bic);
    }

    /**
     * Hier testen wir, ob auch "aehnliche" Namen gefunden werden, falls es den
     * angegebenen Namen nicht gibt. Die Test-BIC ist die Zentrale der
     * Deutschen Bundesbank.
     */
    @Test
    public void testGetSimilarFachwert() {
        KFachwert bic = FACTORY.getFachwert("BIC1", "MARKDEFF");
        assertEquals(new BIC("MARKDEFF"), bic);
    }

    /**
     * Test-Methode fuer {@link FachwertFactory#validate(Class, java.io.Serializable...)}.
     * Die Test-BIC stammt von der Raiffeisenbank Kitzbuehel.
     */
    @Test
    public void testValidateClass() {
        FACTORY.validate(BIC.class, "RZTIAT22263");
    }

    /**
     * Mit Umstellung auf Kotlin ist die validate-Methode der einzelnen
     * Klassen in das Companion-Objekt gewandert. UStIdNr war dabei die
     * erste Klasse, die von Java auf Kotlin umgestellt wurde.
     */
    @Test
    public void testValidateCompanion() {
        assertThrows(ValidationException.class, () -> FACTORY.validate(UStIdNr.class, "DE136695970"));
    }

    /**
     * Test-Methode fuer {@link FachwertFactory#validate(String, java.io.Serializable...)}.
     */
    @Test
    public void testValidateString() {
        FACTORY.validate("Bankverbindung", "Max Muster", new IBAN("DE41300606010006605605"),
                new BIC("GENODEF1JEV"));
    }

    /**
     * Bei den meisten Fachwerten spielen Blanks am Ende keine Rolle und sollte
     * bei der Validierung ignoriert werden.
     */
    @Test
    public void testValiateWithTrailingBlank() {
        FACTORY.validate("Kontonummer", "1111111111  ");
    }
    
    /**
     * Test-Methode fuer {@link FachwertFactory#validate(Class, java.io.Serializable...)}.
     */
    @Test
    public void testValidateWithFailure() {
        assertThrows(ValidationException.class, () -> FACTORY.validate(BIC.class, "AAA"));
    }

    /**
     * Wenn ein unbekannter Name angegeben wird, der kaum Aehnlichkeit mit
     * vorhandenen Klasse aufweist, soll auf eine Fallback-Klasse ohne
     * Validierung (Text) zurueckgegriffen werden.
     */
    @Test
    public void testValidateUnknownName() {
        FACTORY.validate("irgendwas", "42");
    }

    /**
     * Wenn man alle Fachwerte validiert, sollte idealerweise ausser einer
     * {@link ValidationException} keine andere Exception auftreten koennen.
     */
    @Test
    public void testValidateAll() {
        Collection<Class<? extends KFachwert>> registeredClasses = FACTORY.getRegisteredClasses().values();
        for (Class<? extends KFachwert> fachwertClass : registeredClasses) {
            try {
                FACTORY.validate(fachwertClass, "TEST");
            } catch (ValidationException mayhappen) {
                assertThat(fachwertClass + " throws inusfficient exeption", mayhappen.getMessage(),
                        containsString("TEST"));
            }
        }
    }

    /**
     * Hiermit testen wir, ob sich jede Fachwertklasse auch in der
     * {@link FachwertFactory} registriert ist.
     *
     * @throws ClassNotFoundException the class not found exception
     */
    @Test
    public void testRegisteredClasses() throws ClassNotFoundException {
        ClasspathMonitor cpmon = ClasspathMonitor.getInstance();
        String[] classes = cpmon.getClasspathClasses();
        for (String classname : classes) {
            if (classname.startsWith("de.jfachwert.")
                    && (!classname.startsWith("de.jfachwert.bank.Geldbetrag"))
                    && (!classname.equals("de.jfachwert.bank.Waehrung"))) {
                check(Class.forName(classname));
            }
        }
    }
    
    private static void check(Class<?> clazz) {
        int mod = clazz.getModifiers();
        if (Modifier.isAbstract(mod) || Modifier.isInterface(mod) || clazz.equals(Primzahl.class)) {
            return;
        }
        if (KFachwert.class.isAssignableFrom(clazz)) {
            assertThat(FACTORY.getRegisteredClasses().values(), hasItem((Class<? extends KFachwert>) clazz));
        }
    }

}
