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

import de.jfachwert.AbstractFachwert;
import org.apache.commons.lang3.StringUtils;

/**
 * Eine Postleitzahl (PLZ) kennzeichnet den Zustellort auf Briefen, Paketten
 * oder Paeckchen. In Deutschland ist es eine 5-stellige Zahl, wobei auch
 * "0" als fuehrende Ziffer erlaubt ist.
 *
 * @author oboehm
 * @since 0.2.0 (10.04.2017)
 */
public class PLZ extends AbstractFachwert<String> {

    /**
     * Hierueber wird eine Postleitzahl angelegt.
     *
     * @param plz z.B. "70839" oder "D-70839"
     */
    public PLZ(String plz) {
        super(normalize(plz));
    }

    private static String normalize(String plz) {
        return StringUtils.replaceChars(plz, " -", "").toUpperCase();
    }

    /**
     * Hierueber kann man abfragen, ob der Postleitzahl eine Landeskennung
     * vorangestellt ist.
     *
     * @return true, falls PLZ eine Kennung besitzt
     */
    public boolean hasLandeskennung() {
        char kennung = this.getCode().charAt(0);
        return Character.isLetter(kennung);
    }

    /**
     * Liefert die Landeskennung als String. Wenn keine Landeskennung
     * angegeben wurde, wird eine Exception geworfen.
     *
     * @return z.B. "D" fuer Deutschland
     */
    public String getLandeskennung() {
        String plz = this.getCode();
        if (plz.startsWith("D")) {
            return "D";
        } else if (plz.startsWith("A")) {
            return "A";
        } else if (plz.startsWith("CH")) {
            return "CH";
        }
        throw new UnsupportedOperationException("unbekannte Landeskennung: " + plz);
    }

    /**
     * Liefert die PLZ in kompakter Schreibweise (ohne Trennzeichen zwischen
     * Landeskennung und eigentlicher PLZ) zurueck.
     *
     * @return z.B. "D70839"
     */
    public String toShortString() {
        return this.getCode();
    }

    /**
     * Liefert die PLZ in mit Trennzeichen zwischen Landeskennung (falls
     * vorhanden) und eigentlicher PLZ zurueck.
     *
     * @return z.B. "D-70839"
     */
    public String toLongString() {
        String plz = this.getCode();
        if (this.hasLandeskennung()) {
            int i = StringUtils.indexOfAny(plz, "0123456789");
            plz = plz.substring(0, i) + "-" + plz.substring(i);
        }
        return plz;
    }

    /**
     * Aus Lesbarkeitsgruenden wird zwischen Landeskennung und eigentlicher PLZ
     * ein Trennzeichen mit ausgegeben.
     *
     * @return z.B. "D-70839"
     */
    @Override
    public String toString() {
        return this.toLongString();
    }

}
