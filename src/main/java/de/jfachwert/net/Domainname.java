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
 * (c)reated 08.08.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.SimpleValidator;
import de.jfachwert.Text;
import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * Ueber den Domain-Namen wird ein Rechner im Internet adressiert. Man kann
 * ihn zwar auch ueber seine IP-Adresse ansprechen, aber kann man sich als
 * Normalsterblicher schwer merken.
 * <p>
 * Ein Domainname besteht aus mindestens aus Teilen: einem Hostnamen (z. B.
 * ein Firmenname), einem Punkt und einer Top-Level-Domain.
 * </p>
 *
 * @author oboehm
 * @since 0.4 (08.08.2017)
 */
public class Domainname extends Text {

    private static final WeakHashMap<String, Domainname> WEAK_CACHE = new WeakHashMap<>();
    private static final SimpleValidator<String> VALIDATOR = new Validator();

    /**
     * Legt eine Instanz an.
     *
     * @param name gueltiger Domain-Name
     */
    public Domainname(String name) {
        this(name, VALIDATOR);
    }

    /**
     * Legt eine Instanz an.
     *
     * @param name      gueltiger Domain-Name
     * @param validator zur Pruefung
     */
    public Domainname(String name, SimpleValidator<String> validator) {
        super(name.trim().toLowerCase(), validator);
    }

    /**
     * Liefert einen Domainnamen.
     *
     * @param name gueltiger Domainname
     * @return Domainname
     */
    public static Domainname of(String name) {
        return WEAK_CACHE.computeIfAbsent(name, Domainname::new);
    }

    /**
     * Liefert die Top-Level-Domain (TLD) zurueck.
     *
     * @return z.B. "de"
     */
    public Domainname getTLD() {
        return new Domainname(StringUtils.substringAfterLast(this.getCode(), "."));
    }

    /**
     * Waehrend die Top-Level-Domain die oberste Ebende wie "de" ist, ist die
     * 2nd-Level-Domain von "www.jfachwert.de" die Domain "jfachwert.de" und
     * die 3rd-Level-Domain ist in diesem Beispiel die komplette Domain.
     *
     * @param level z.B. 2 fuer 2nd-Level-Domain
     * @return z.B. "jfachwert.de"
     */
    public Domainname getLevelDomain(int level) {
        String[] parts = this.getCode().split("\\.");
        int firstPart = parts.length - level;
        if ((firstPart < 0) || (level < 1)) {
            throw new LocalizedIllegalArgumentException(level, "level", Range.between(1, parts.length));
        }
        StringBuilder name = new StringBuilder(parts[firstPart]);
        for (int i = firstPart + 1; i < parts.length; i++) {
            name.append('.');
            name.append(parts[i]);
        }
        return new Domainname(name.toString());
    }


    /**
     * Dieser Validator ist fuer die Ueberpruefung von Domainnamen vorgesehen.
     *
     * @since 2.2
     */
    public static class Validator implements SimpleValidator<String> {

        private static final Pattern VALID_PATTERN =
                Pattern.compile("^(?=.{1,253}\\.?$)(?:(?!-|[^.]+_)[A-Za-z0-9-_]{1,63}(?<!-)(?:\\.|$))+$");

        /**
         * Hie valideren wir den Namen auf Richtigkeit. Das Pattern dazu stammt aus
         * https://regex101.com/r/d5Yd6j/1/tests . Allerdings akzeptieren wir auch
         * die TLD wie "de" als gueltigen Domainnamen.
         *
         * @param name Domain-Name
         * @return validierter Domain-Name zur Weiterverarbeitung
         */
        public String validate(String name) {
            if (VALID_PATTERN.matcher(name).matches()) {
                return name;
            }
            throw new InvalidValueException(name, "name");
        }

    }

}
