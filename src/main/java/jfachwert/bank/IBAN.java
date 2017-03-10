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
 * (c)reated 10.03.17 by oliver (ob@jfachwert.de)
 */
package jfachwert.bank;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;


/**
 * Repraesentation der IBAN.
 *
 * @author oboehm
 */
public class IBAN implements Serializable {

    /** Konstante fuer unbekannte IBAN (aus Wikipedia). */
    public static final IBAN UNBEKANNT = new IBAN("DE19123412341234123412");

    private final String raw;

    /**
     * Instantiiert eine neue IBAN.
     *
     * @param iban im unformattierten Format
     */
    public IBAN(String iban) {
        this.raw = StringUtils.remove(iban, ' ');
    }

    private static String formatted(String iban) {
        String ibanStripped = StringUtils.remove(iban, ' ');
        String input = ibanStripped + "   ";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < ibanStripped.length(); i+= 4) {
            buf.append(input.substring(i, i+4));
            buf.append(' ');
        }
        return buf.toString().trim().toUpperCase();
    }

    /**
     * Liefert die IBAN formattiert in der DIN-Form. Dies ist die uebliche
     * Papierform, in der die IBAN in 4er-Bloecke formattiert wird, jeweils
     * durch Leerzeichen getrennt.
     *
     * @return formatierte IBAN, z.B. "DE19 1234 1234 1234 1234 12"
     */
    public String getFormatted() {
        String input = this.raw + "   ";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.raw.length(); i+= 4) {
            buf.append(input.substring(i, i+4));
            buf.append(' ');
        }
        return buf.toString().trim().toUpperCase();
    }

    /**
     * Liefert die unformattierte IBAN.
     *
     * @return unformattierte IBA
     */
    public String getUnformatted() {
        return this.raw;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.raw.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IBAN)) {
            return false;
        }
        IBAN other = (IBAN) obj;
        return this.raw.equalsIgnoreCase(other.raw);
    }

    /**
     * Hierueber wird die nackte IBAN (ohne Formattierung) ausgegeben.
     *
     * @return the string
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.raw;
    }

}
