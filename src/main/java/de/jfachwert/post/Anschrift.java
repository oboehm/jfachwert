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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.InvalidValueException;
import org.apache.commons.lang3.StringUtils;

/**
 * Die Anschrift besteht aus Namen und Adresse. Der Name kann dabei eine
 * Person oder eine Personengruppe (zum Beispiel Unternehmen, Vereine und
 * Aehnliches) sein.
 *
 * @author oboehm
 * @since 0.2 (12.05.2017)
 */
public class Anschrift implements Fachwert {

    private final String name;
    private final Adresse adresse;

    /**
     * Erzeugt aus dem Namen und Adresse eine Anschrift.
     *
     * @param name    Namen einer Person oder Personengruppe
     * @param adresse eine gueltige Adresse
     */
    public Anschrift(String name, Adresse adresse) {
        this.name = name;
        this.adresse = adresse;
        validate(name, adresse);
    }

    /**
     * Validiert den uebergebenen Namen und die Adresse. Der Name sollte dabei
     * nicht leer sein und die Adresse nicht 'null'.
     *
     * @param name zu pruefender Name
     * @param adresse eine gueltige Adresse
     */
    public static void validate(String name, Adresse adresse) {
        if (StringUtils.isBlank(name)) {
            throw new InvalidValueException(name, "name");
        }
        if (adresse == null) {
            throw new InvalidValueException(adresse, "address");
        }
    }

    /**
     * Liefert den Namen. Ein Name kan eine Person oder eine Personengruppe
     * (zum Beispiel Unternehmen, Vereine und Aehnliches) sein.
     *
     * @return z.B. "Oli B."
     */
    public String getName() {
        return name;
    }

    /**
     * Liefert die Anschrift der Adresse.
     *
     * @return eine gueltige Adresse
     */
    public Adresse getAdresse() {
        return adresse;
    }

    /**
     * Zwei Anschriften sind gleich, wenn sie den gleichen Namen und die
     * gleiche Adresse besitzen. Dabei spielt es keine Rolle, ob der Name
     * gross- oder kleingeschrieben ist.
     *
     * @param obj die andere Anschrift
     * @return true bei Gleichheit
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Anschrift)) {
            return false;
        }
        Anschrift other = (Anschrift) obj;
        return this.getName().equalsIgnoreCase(other.getName())
                && this.getAdresse().equals(other.getAdresse());
    }

    /**
     * Da eine hashCode-Methode performant sein soll, wird nur der Name zur
     * Bildung des Hash-Codes herangezogen.
     *
     * @return Hashcode des Namen.
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Der Namen mit Anschrift wird einzeilig zurueckgegeben.
     *
     * @return z.B. "Dagobert Duck, 12345 Entenhausen, Geldspeicher 23"
     */
    @Override
    public String toString() {
        return this.getName() + ", " + this.getAdresse();
    }

}
