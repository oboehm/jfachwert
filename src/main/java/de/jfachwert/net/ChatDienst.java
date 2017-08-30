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
 * (c)reated 19.08.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.*;

import java.math.*;

/**
 * Es gibt verschiedene Chat-Dienste wie ICQ, Skype oder Jabber, die hier
 * aufgelistet sind. Allerdings sind nicht alle aufgelistet sondern nur die,
 * die auch bei Xing eingetragen werden koennen.
 *
 * Die einzelnen Chat-Dienste sind alphabetisch sortiert.
 *
 * @since 0.4 (08.08.2017)
 */
public enum ChatDienst {

    /** AOL Instant Messaenger. */
    AIM("AIM"),

    /** Google Hangout. */
    GOOGLE_HANGOUT("Google Hangout"),

    /** Einer der aeltesten Messanger Dienste (1988). */
    ICQ("ICQ", new NumberValidator(BigDecimal.valueOf(10000), NumberValidator.INFINITE)),

    /** Jabber Instant Messanger, baut auf XMPP auf. */
    JABBER("Jabber", new EMailValidator()),

    /** Skype. */
    SKYPE("Skype"),

    /** Yahoo Messanger. */
    YAHOO("Yahoo"),

    /** Sonstiger Messanger. */
    SONSTIGER("sonstiger");

    private final String name;
    private final SimpleValidator validator;

    ChatDienst(String name) {
        this(name, new NullValidator());
    }

    ChatDienst(String name, SimpleValidator validator) {
        this.name = name;
        this.validator = validator;
    }

    /**
     * Liefert den passenden Validator, mit dem der Account-Name validiert
     * werden kann.
     *
     * @return passender Validator
     */
    public SimpleValidator getValidator() {
        return validator;
    }

    /**
     * Wir geben den Namen statt der Konstanten aus.
     *
     * @return z.B. "Jabber"
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Liefert zum uebergebenen String den passenden Chat-Dienst heraus.
     *
     * @param dienst z.B. "Jabber"
     * @return #SONSTIGER, wenn kein passender Dienst gefunden wurde
     */
    public static ChatDienst toChatDienst(String dienst) {
        for (ChatDienst cd : values()) {
            if (cd.toString().equalsIgnoreCase(dienst)) {
                return cd;
            }
        }
        return SONSTIGER;
    }
}
