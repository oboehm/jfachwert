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
import de.jfachwert.math.Bruch;
import de.jfachwert.bank.Geldbetrag;
import de.jfachwert.math.Nummer;
import de.jfachwert.math.PackedDecimal;
import de.jfachwert.net.ChatAccount;
import de.jfachwert.net.Domainname;
import de.jfachwert.net.EMailAdresse;
import de.jfachwert.net.Telefonnummer;
import de.jfachwert.post.*;
import de.jfachwert.pruefung.exception.LocalizedValidationException;
import de.jfachwert.rechnung.*;
import de.jfachwert.steuer.SteuerIdNr;
import de.jfachwert.steuer.Steuernummer;
import de.jfachwert.steuer.UStIdNr;
import de.jfachwert.util.TinyUUID;

import javax.validation.ValidationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * <p>
 * Da in dieser Klasse Exceptions auftreten koennen, die nicht weitergegeben
 * werden, wird dazu der Standard-Logger aus dem JDK verwendet. Damit kann man
 * sich zum Debuggen diese Exceptions im Log-Level "FINE" ausgeben lassen.
 * </p>
 *
 * @author oboehm
 * @since 0.5 (13.01.2018)
 */
public class FachwertFactory {
    
    private static final FachwertFactory INSTANCE = new FachwertFactory();
    private final Map<String, Class<? extends Fachwert>> registeredClasses = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(Fachwert.class.getName());

    // Die Registrierung hier ist unschoen, weil dazu die FachwertFactory alle
    // Fachwert-Klassen kennen muss. Schoener waere es, wenn sich die einzelnen
    // Klassen selber registrieren wuerden. Das Problem dabei ist, dass sie es
    // erst machen kann, wenn sie vom Classloader geladen wurde (Henne-Ei-
    // Problem).
    static {
        INSTANCE.register(Text.class);
        INSTANCE.register(Nummer.class);
        INSTANCE.register(Bankverbindung.class);
        INSTANCE.register(BIC.class);
        INSTANCE.register(BLZ.class);
        INSTANCE.register(IBAN.class);
        INSTANCE.register(Kontonummer.class);
        INSTANCE.register(ChatAccount.class);
        INSTANCE.register(Domainname.class);
        INSTANCE.register(EMailAdresse.class);
        INSTANCE.register(Telefonnummer.class);
        INSTANCE.register(Adressat.class);
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
        INSTANCE.register(PackedDecimal.class);
        INSTANCE.register(Bruch.class);
        INSTANCE.register(TinyUUID.class);
        INSTANCE.register(Geldbetrag.class);
        INSTANCE.register(Waehrung.class);
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
    public synchronized void register(Class<? extends Fachwert> fachwertClass) {
        registeredClasses.put(fachwertClass.getSimpleName(), fachwertClass);
    }

    /**
     * Liefert die registrierten Klassen zurueck.
     * 
     * @return registrierte Klassen
     */
    public Map<String, Class<? extends Fachwert>> getRegisteredClasses() {
        return this.registeredClasses;
    }

    /**
     * Liefert einen Fachwert zum angegebenen (Klassen-)Namen. Als Name wird
     * der Klassennamen erwartet. Wird keine Klasse gefunden, wird die Klasse
     * genommen, die am ehesten passt. So wird bei "IBAN1" als Name eine
     * Instanz der IBAN-Klasse zurueckgeliefert.
     * <p>
     * Anmerkung: Die Aehnlichkeit des uebergebenen Namens mit dem
     * tatsaechlichen Namen wird anhand der Levenshtein-Distanz bestimmt.
     * Ist die Differenz zu groß, wird als Fallback die {@link Text}-Klasse
     * verwendet.
     * </p>
     *
     * @param name Namen der Fachwert-Klasse, z.B. "IBAN"
     * @param args Argument(e) fuer den Konstruktor der Fachwert-Klasse
     * @return ein Fachwert
     */
    public Fachwert getFachwert(String name, Object... args) {
        Class<? extends Fachwert> fachwertClass = getClassFor(name);
        return getFachwert(fachwertClass, args);
    }

    /**
     * Liefert einen Fachwert zur angegebenen Klasse.
     *
     * @param clazz Fachwert-Klasse
     * @param args Argument(e) fuer den Konstruktor der Fachwert-Klasse
     * @return ein Fachwert
     */
    public Fachwert getFachwert(Class<? extends Fachwert> clazz, Object... args) {
        Class[] argTypes = toTypes(args);
        try {
            Constructor<? extends Fachwert> ctor = clazz.getConstructor(argTypes);
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof ValidationException) {
                throw (ValidationException) cause;
            } else if (cause instanceof IllegalArgumentException) {
                throw new LocalizedValidationException(cause.getMessage(), cause);
            } else {
                throw new IllegalArgumentException("cannot create " + clazz + " with " + Arrays.toString(args), ex);
            }
        }
    }

    /**
     * Validiert die uebergebenen Argumente mit Hilfe der angegebenen Klasse,
     * die als (Klassen-)Namen angegeben wird. Viele Fachwert-Klassen haben
     * eine (statische) validate-Methode, die dafuer verwendet wird. Fehlt
     * diese validate-Methode, wird der Konstruktor fuer die Validierung
     * herangezogen. Schlaegt die Validierung fehl, wird eine 
     * Schlaegt die Validierung fehl, wird eine 
     * {@link javax.validation.ValidationException} geworfen.
     * <p>
     * Wenn es den uebergebenen (Klassen-)Namen nicht gibt, wird mithilfe der
     * Levenshtein-Distanz die aehnlichste Klasse genommen. Ist die Differenz
     * zu groß, wird als Fallback die {@link Text}-Klasse verwendet.
     * </p>
     *
     * @param name Namen der Fachwert-Klasse, z.B. "IBAN"
     * @param args Argument(e), die validiert werden
     */
    public void validate(String name, Object... args) {
        Class<? extends Fachwert> fachwertClass = getClassFor(name);
        validate(fachwertClass, args);
    }

    /**
     * Validiert die uebergebenen Argumente mit Hilfe der angegebenen Klasse.
     * Viele Fachwert-Klassen haben eine (statische) validate-Methode, die
     * dafuer verwendet wird. Fehlt diese validate-Methode, wird der 
     * Konstruktor fuer die Validierung herangezogen. Schlaegt die Validierung
     * fehl, wird eine {@link javax.validation.ValidationException} geworfen.
     * <p>
     * Dies ist eine der wenigen Stelle, wo eine
     * Log-Ausgabe erscheinen kann. Hintergrund ist die Exception, die hier
     * gefangen, aber nicht weitergegeben wird. Im Log-Level "FINE" kann man
     * sich diese Exception zur Fehlersuche ausgeben.
     * </p>
     *
     * @param clazz Fachwert-Klasse
     * @param args Argument(e), die validiert werden
     */
    public void validate(Class<? extends Fachwert> clazz, Object... args) {
        Class[] argTypes = toTypes(args);
        try {
            Method method = clazz.getMethod("validate", argTypes);
            method.invoke(null, args);
        } catch (InvocationTargetException ex) {
            LOG.log(Level.FINE, "Call of validate method of " + clazz + "failed:", ex);
            if (ex.getTargetException() instanceof ValidationException) {
                throw (ValidationException) ex.getTargetException();
            }
            getFachwert(clazz, args);
        } catch (ReflectiveOperationException ex) {
            LOG.log(Level.FINE, "Cannot call validate method of " + clazz, ex);
            getFachwert(clazz, args);
        }
    }

    private Class<? extends Fachwert> getClassFor(String name) {
        Class<? extends Fachwert> fachwertClass = registeredClasses.get(name);
        if (fachwertClass == null) {
            fachwertClass = registeredClasses.get(getSimilarName(name));
        }
        return fachwertClass;
    }
    
    private static Class[] toTypes(Object[] args) {
        Class[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        return argTypes;
    }

    private String getSimilarName(String name) {
        String similarName = "?";
        int minDistance = Integer.MAX_VALUE;
        for (String registeredName : registeredClasses.keySet()) {
            int dist = distance(name, registeredName);
            if (dist < minDistance) {
                similarName = registeredName;
                minDistance = dist;
            }
        }
        if (minDistance > 2) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Nearest name for '" + name + "' is '" + similarName + "' and too far away (" + minDistance +
                        ") - will use Text class as fallback.");
            }
            similarName = Text.class.getSimpleName();
        }
        return similarName;
    }

    /**
     * Berechnet die Levenshtein-Distanz. Der Algorithmus dazu stammt aus
     * http://rosettacode.org/wiki/Levenshtein_distance#Java.
     * 
     * @param a erser Text
     * @param b zweiter Text
     * @return Levenshtein-Distanz
     */
    private static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

}
