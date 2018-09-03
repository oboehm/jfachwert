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
 * (c)reated 06.07.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;
import de.jfachwert.util.ToFachwertSerializer;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Eine Bankverbindung besteht aus dem Zahlungsempfaenger oder Kontoinhaber,
 * einer IBAN und einer BIC. Bei Inlandsverbindungen kann die BIC entfallen,
 * weswegen sie hier auch optional ist.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.3.0
 */
@JsonSerialize(using = ToFachwertSerializer.class)
public class Bankverbindung implements Fachwert {

    private final String kontoinhaber;
    private final IBAN iban;
    private final BIC bic;

    /**
     * Zerlegt den uebergebenen String in Name, IBAN und (optional) BIC.
     * Folgende Heuristiken werden fuer die Zerlegung angewendet:
     * <ul>
     *     <li>
     *         Reihenfolge ist Name, IBAN und BIC, evtl. durch Kommata 
     *         getrennt
     *     </li>
     *     <li>IBAN wird durch "IBAN" (grossgeschrieben) eingeleitet</li>
     *     <li>
     *         BIC wird durch "BIC" (grossgeschrieben) eingeleitet,
     *         ist aber optional.
     *     </li>
     * </ul>
     * 
     * @param bankverbindung z.B. "Max Muster, IBAN DE41300606010006605605"
     */
    public Bankverbindung(String bankverbindung) {
        this(split(bankverbindung));
    }
    
    private Bankverbindung(Object[] bankverbindung) {
        this(bankverbindung[0].toString(), (IBAN) bankverbindung[1], (BIC) bankverbindung[2]);
    }
    
    /**
     * Erzeugt eine neue Bankverbindung aus der uebergebenen Map.
     *
     * @param map mit den einzelnen Elementen fuer "kontoinhaber", "iban" und
     *            "bic".
     */
    @JsonCreator
    public Bankverbindung(Map<String, String> map) {
        this(map.get("kontoinhaber"), new IBAN(map.get("iban")), new BIC(map.get("bic")));
    }
    
    /**
     * Erzeugt eine neue Bankverbindung.
     *
     * @param name Name des Zahlungsempfaengers
     * @param iban die IBAN
     */
    public Bankverbindung(String name, IBAN iban) {
        this(name, iban, null);
    }

    /**
     * Erzeugt eine neue Bankverbindung.
     *
     * @param name Name des Zahlungsempfaengers
     * @param iban die IBAN
     * @param bic die BIC
     */
    public Bankverbindung(String name, IBAN iban, BIC bic) {
        this.kontoinhaber = name;
        this.iban = iban;
        this.bic = bic;
    }
    
    private static Object[] split(String bankverbindung) {
        String[] splitted = new String[3];
        splitted[0] = stripSeparator(StringUtils.substringBefore(bankverbindung, "IBAN"));
        splitted[1] = stripSeparator(StringUtils.substringAfter(bankverbindung, "IBAN"));
        if (StringUtils.isBlank(splitted[1])) {
            throw new LocalizedIllegalArgumentException(bankverbindung, "bank_account");
        }
        if (splitted[1].contains("BIC")) {
            splitted[2] = stripSeparator(StringUtils.substringAfter(splitted[1], "BIC"));
            splitted[1] = stripSeparator(StringUtils.substringBefore(splitted[1], "BIC"));
        } else {
            splitted[2] = "";
        }
        Object[] values = new Object[3];
        values[0] = splitted[0];
        values[1] = new IBAN(splitted[1]);
        values[2] = splitted[2].isEmpty() ? null : new BIC(splitted[2]);
        return values;
    }
    
    private static String stripSeparator(String raw) {
        String value = raw.trim();
        if (value.endsWith(",")) {
            value = value.substring(0, value.length()-1);
        }
        return value;
    }

    public String getKontoinhaber() {
        return kontoinhaber;
    }

    public IBAN getIban() {
        return iban;
    }

    /**
     * Da die BIC bei Inlands-Ueberweisungen optional ist, wird sie hier als
     * {@link Optional} zurueckgegeben.
     *
     * @return BIC
     */
    public Optional<BIC> getBic() {
        return (bic == null) ? Optional.empty() : Optional.of(bic);
    }

    @Override
    public int hashCode() {
        return iban.hashCode();
    }

    /**
     * Zwei Bankverbindungen sind gleich, wenn IBAN und BIC uebereinstimmen.
     *
     * @param obj die andere Bankverbindung
     * @return true bei Gleichheit
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bankverbindung)) {
            return false;
        }
        Bankverbindung other = (Bankverbindung) obj;
        return this.iban.equals(other.iban) && Objects.equals(this.bic, other.bic);
    }

    @Override
    @SuppressWarnings("squid:S3655")
    public String toString() {
        StringBuilder buf = new StringBuilder(getKontoinhaber());
        buf.append(", IBAN ");
        buf.append(getIban());
        if (getBic().isPresent()) {
            buf.append(", BIC ");
            buf.append(getBic().get());
        }
        return buf.toString();
    }

    /**
     * Liefert die einzelnen Attribute einer Bankverbindung als Map.
     *
     * @return Attribute als Map
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("kontoinhaber", getKontoinhaber());
        map.put("iban", getIban());
        getBic().ifPresent(b -> map.put("bic", b));
        return map;
    }

}
