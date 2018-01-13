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

import java.lang.reflect.Constructor;
import java.util.Arrays;
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
 * @since x.x (13.01.2018)
 */
public class FachwertFactory {

    /**
     * Liefert einen Fachwert zum angegebenen (Klassen-)Namen. Als Name wird
     * der Klassennamen erwartet. Wird keine Klasse gefunden, wird die Klasse
     * genommen, die am ehesten passt. So wird bei "IBAN1" als Name eine
     * Instanz der IBAN-Klasse zurueckgeliefert.
     *
     * @param classname Namen der Fachwert-Klasse, z.B. "IBAN"
     * @param args Argument(e) fuer den Konstruktor der Fachwert-Klasse
     * @return
     */
    public Fachwert getFachwert(String classname, Object... args) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Liefert einen Fachwert zu angegebenen Klasse.
     *
     * @param clazz Fachwert-Klasse
     * @param args Argument(e) fuer den Konstruktor der Fachwert-Klasse
     * @return
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
