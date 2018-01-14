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
 * (c)reated 13.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert;

import de.jfachwert.bank.*;
import de.jfachwert.net.*;
import de.jfachwert.post.*;
import de.jfachwert.rechnung.*;
import de.jfachwert.steuer.SteuerIdNr;
import de.jfachwert.steuer.Steuernummer;
import de.jfachwert.steuer.UStIdNr;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ueber die FachwertFactory kann ein beliebiger Fachwert generiert oder geholt
 * werden. Normalerweise sollte man da den entsprechenden Konstruktor des
 * Fachwerts bemuehen, aber es gibt auch Situation, wo man den genauen Typ des
 * Fachwertes (noch) nicht weiss. Fuer diese Situation ist diese Factory
 * gedacht.
 * <p>
 * Zum Generieren eines Fachwerts wird der aktuelle Classpath abgeklappert, um
 * die passende Implementierung zu finden. Um die Implementierung erweitern
 * aoder ustauschen zu koennen, hat diese Factory keine statischen Methoden,
 * sondern ist als normale Klasse implementiert.
 * </p>
 *
 * @author oboehm
 * @since 0.5 (13.01.2018)
 */
public class FachwertFactory {
    
    private static final FachwertFactory INSTANCE = new FachwertFactory();
    private final Map<String, Class<? extends Fachwert>> registeredClasses = new HashMap<>();

    // Die Registrierung hier ist unschoen, weil dazu die FachwertFactory alle
    // Fachwert-Klassen kennen muss. Schoener waere es, wenn sich die einzelnen
    // Klassen selber registrieren wuerden. Das Problem dabei ist, dass sie es
    // erst machen kann, wenn sie vom Classloader geladen wurde (Henne-Ei-
    // Problem).
    static {
        INSTANCE.register(Bankverbindung.class);
        INSTANCE.register(BIC.class);
        INSTANCE.register(BLZ.class);
        INSTANCE.register(IBAN.class);
        INSTANCE.register(Kontonummer.class);
        INSTANCE.register(ChatAccount.class);
        INSTANCE.register(Domainname.class);
        INSTANCE.register(EMailAdresse.class);
        INSTANCE.register(Telefonnummer.class);
        INSTANCE.register(Adresse.class);
        INSTANCE.register(Anschrift.class);
        INSTANCE.register(Ort.class);
        INSTANCE.register(PLZ.class);
        INSTANCE.register(Postfach.class);
        INSTANCE.register(Artikelnummer.class);
        INSTANCE.register(Bestellnummer.class);
        INSTANCE.register(Kundennummer.class);
        INSTANCE.register(Rechnungsmonat.class);
        INSTANCE.register(Rechnungsnummer.class);
        INSTANCE.register(Referenznummer.class);
        INSTANCE.register(SteuerIdNr.class);
        INSTANCE.register(Steuernummer.class);
        INSTANCE.register(UStIdNr.class);
    }

    private FachwertFactory() {
    }

    /**
     * Die FachwertFactory ist als Singleton angelegt, um die Implementierung
     * durch Ableitung erweiern zu koennen. Mit dieser Methode wird die
     * einzige Instanz dieser Klasse zurueckgegeben.
     * 
     * @return die einzige Instanz
     */
    public static FachwertFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Hierueber sollten sich die einzelnen Fachwert-Klassen registrieren.
     * Ansonsten werden sie bei {@link FachwertFactory#getFachwert(String, Object...)}
     * nicht gefunden.
     * 
     * @param fachwertClass Fachwert-Klasse
     */
    synchronized public void register(Class<? extends Fachwert> fachwertClass) {
        registeredClasses.put(fachwertClass.getSimpleName(), fachwertClass);
    } 

    /**
     * Liefert einen Fachwert zum angegebenen (Klassen-)Namen. Als Name wird
     * der Klassennamen erwartet. Wird keine Klasse gefunden, wird die Klasse
     * genommen, die am ehesten passt. So wird bei "IBAN1" als Name eine
     * Instanz der IBAN-Klasse zurueckgeliefert.
     *
     * @param name Namen der Fachwert-Klasse, z.B. "IBAN"
     * @param args Argument(e) fuer den Konstruktor der Fachwert-Klasse
     * @return ein Fachwert
     */
    public Fachwert getFachwert(String name, Object... args) {
        Class<? extends Fachwert> fachwertClass = registeredClasses.get(name);
        if (fachwertClass == null) {
            throw new IllegalArgumentException("no Fachwert class found for '" + name + "'");
        }
        return getFachwert(fachwertClass, args);
    }

    /**
     * Liefert einen Fachwert zu angegebenen Klasse.
     *
     * @param clazz Fachwert-Klasse
     * @param args Argument(e) fuer den Konstruktor der Fachwert-Klasse
     * @return ein Fachwert
     */
    public Fachwert getFachwert(Class<? extends Fachwert> clazz, Object... args) {
        Class[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        try {
            Constructor<? extends Fachwert> ctor = clazz.getConstructor(argTypes);
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalArgumentException("cannot create " + clazz + " with " + args);
        }
    }

}
