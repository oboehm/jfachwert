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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.util.ToFachwertSerializer;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Die Anschrift besteht aus Namen und Adresse oder Postfach. Der Name kann
 * dabei eine Person oder eine Personengruppe (zum Beispiel Unternehmen,
 * Vereine und Aehnliches) sein.
 *
 * @author oboehm
 * @since 0.2 (12.05.2017)
 */
@JsonSerialize(using = ToFachwertSerializer.class)
public class Anschrift implements Fachwert {

    private static final Logger LOG = Logger.getLogger(Anschrift.class.getName());
    public static final String ADDRESS = "address";

    private final Adressat adressat;
    private final Adresse adresse;
    private final Postfach postfach;

    /**
     * Zerlegt die uebergebene Anschrift in Adressat und Adresse oder Postfach,
     * um daraus eine Anschrift zu erzeugen. Folgende Heuristiken werden fuer 
     * die Zerlegung herangezogen:
     * <ul>
     *     <li>Adressat steht an erster Stelle</li>
     *     <li>Einzelteile werden durch Komma oder Zeilenvorschub getrennt</li>
     * </ul>
     *
     * @param anschrift z.B. "Donald Duck, 12345 Entenhausen, Gansstr. 23"
     */
    public Anschrift(String anschrift) {
        this(split(anschrift));
    }
    
    private Anschrift(Object[] anschrift) {
        this(new Adressat(anschrift[0].toString()), (Adresse) anschrift[1], (Postfach) anschrift[2]);
    }
    
    /**
     * Erzeugt eine neue Anschrift aus der uebergebenen Map.
     *
     * @param map mit den einzelnen Elementen fuer "adressat" und "adresse".
     */
    @JsonCreator
    public Anschrift(Map<String, Object> map) {
        this(new Adressat(map.get("adressat").toString()), new Adresse((Map<String, String>) (map.get("adresse"))));
    }

    /**
     * Erzeugt aus dem Namen und Adresse eine Anschrift.
     *
     * @param name    Namen einer Person oder Personengruppe
     * @param adresse eine gueltige Adresse
     */
    public Anschrift(String name, Adresse adresse) {
        this(new Adressat(name), adresse);
        validate(name, adresse);
    }

    /**
     * Erzeugt aus dem Adressaten und Adresse eine Anschrift.
     *
     * @param name    Namen einer Person oder Personengruppe
     * @param adresse eine gueltige Adresse
     */
    public Anschrift(Adressat name, Adresse adresse) {
        this(name, adresse, null);
    }

    /**
     * Erzeugt aus dem Namen und einem Postfach eine Anschrift.
     *
     * @param name     Namen einer Person oder Personengruppe
     * @param postfach ein gueltiges Postfach
     */
    public Anschrift(String name, Postfach postfach) {
        this(new Adressat(name), postfach);
        validate(name, postfach);
    }

    /**
     * Erzeugt aus dem Adressaten und einem Postfach eine Anschrift.
     *
     * @param name     Namen einer Person oder Personengruppe
     * @param postfach ein gueltiges Postfach
     */
    public Anschrift(Adressat name, Postfach postfach) {
        this(name, null, postfach);
    }
    
    private Anschrift(Adressat name, Adresse adresse, Postfach postfach) {
        this.adressat = name;
        this.adresse = adresse;
        this.postfach = postfach;
        if (adresse == null) {
            if (postfach == null) {
                throw new InvalidValueException("post_office_box");
            }
        } else {
            if (postfach != null) {
                throw new InvalidValueException(adresse, ADDRESS);
            }
        }
    }

    /**
     * Zerlegt die uebergebene Anschrift in Adressat und Adresse oder Postfach
     * fuer die Validierung.i Folgende Heuristiken werden fuer die Zerlegung
     * herangezogen:
     * <ul>
     *     <li>Adressat steht an erster Stelle</li>
     *     <li>Einzelteile werden durch Komma oder Zeilenvorschub getrennt</li>
     * </ul>
     * 
     * @param anschrift z.B. "Donald Duck, 12345 Entenhausen, Gansstr. 23"
     */
    public static void validate(String anschrift) {
        split(anschrift);
    }
    
    private static Object[] split(String anschrift) {
        String[] lines = StringUtils.trimToEmpty(anschrift).split("[,\\n$]");
        if (lines.length < 2) {
            throw new InvalidValueException(anschrift, "address");
        }
        Object[] parts = new Object[3];
        parts[0] = new Adressat(lines[0]);
        String adresseOrPostfach = anschrift.substring(lines[0].length()+1).trim();
        try {
            parts[1] = null;
            parts[2] = new Postfach(adresseOrPostfach);
        } catch (ValidationException ex) {
            LOG.log(Level.FINE, "'" + adresseOrPostfach + "' is not a post office box:", ex);
            parts[1] = new Adresse(adresseOrPostfach);
            parts[2] = null;
        }
        return parts;
    }

    /**
     * Validiert den uebergebenen Namen und die Adresse. Der Name sollte dabei
     * nicht leer sein und die Adresse nicht 'null'.
     *
     * @param name zu pruefender Name
     * @param adresse eine gueltige Adresse
     */
    public static void validate(String name, Adresse adresse) {
        validateName(name);
        if (adresse == null) {
            throw new InvalidValueException("address");
        }
    }

    /**
     * Validiert den uebergebenen Namen und das Postfach. Der Name sollte dabei
     * nicht leer sein und das Postfach nicht 'null'.
     *
     * @param name zu pruefender Name
     * @param postfach ein gueltiges Postfach
     */
    public static void validate(String name, Postfach postfach) {
        validateName(name);
        if (postfach == null) {
            throw new InvalidValueException("post_office_box");
        }
    }

    private static void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new InvalidValueException(name, "name");
        }
    }

    /**
     * Liefert den Adressaten. Ein Adressat kann eine Person oder eine 
     * Personengruppe (zum Beispiel Unternehmen, Vereine und Aehnliches) sein.
     *
     * @return z.B. "Mustermann, Max" als Adressat
     */
    public Adressat getAdressat() {
        return adressat;
    }

    /**
     * Liefert den Namen. Ein Name kann eine Person oder eine Personengruppe
     * (zum Beispiel Unternehmen, Vereine und Aehnliches) sein.
     *
     * @return z.B. "Mustermann"
     */
    public String getName() {
        return adressat.getName();
    }

    /**
     * Liefert die Adresse der Anschrift. Voraussetzung fuer den Aufruf dieser
     * Methode ist, dass die Anschrift tatsaechlich eine Adresse enthaelt, und
     * kein Postfach.
     *
     * @return eine gueltige Adresse
     */
    public Adresse getAdresse() {
        if (adresse == null) {
            throw new IllegalStateException("no address available");
        }
        return adresse;
    }

    /**
     * Liefert das Postfach der Anschrift. Voraussetzung fuer den Aufruf dieser
     * Methode ist, dass die Anschrift tatsaechlich ein Postfach enthaelt, und
     * keine Adresse.
     *
     * @return ein gueltiges Postfach
     */
    public Postfach getPostfach() {
        if (postfach == null) {
            throw new IllegalStateException("no post office box available");
        }
        return postfach;
    }

    /**
     * Hierueber kann abgefragt werden, ob die Anschrift eine Adresse oder ein
     * Postfach beinhaltet.
     *
     * @return true bei Postfach
     */
    public boolean hasPostfach() {
        return this.postfach != null;
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
        return this.adressat.hashCode();
    }

    /**
     * Der Namen mit Anschrift wird einzeilig zurueckgegeben.
     *
     * @return z.B. "Dagobert Duck, 12345 Entenhausen, Geldspeicher 23"
     */
    @Override
    public String toString() {
        return getName() + ", " + (hasPostfach() ? getPostfach() : getAdresse());
    }

    /**
     * Liefert die einzelnen Attribute eines Postfaches als Map.
     *
     * @return Attribute als Map
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("adressat", getAdressat());
        map.put("adresse", getAdresse());
        return map;
    }

}
