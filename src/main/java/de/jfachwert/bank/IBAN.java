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
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.PruefzifferVerfahren;
import de.jfachwert.pruefung.Mod97Verfahren;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;


/**
 * Die IBAN (International Bank Account Number) ist eine international
 * standardisierte Notation fuer Bankkonten, die durch die ISO-Norm ISO 13616-1
 * beschrieben wird.
 *
 * @author oboehm
 */
public class IBAN extends AbstractFachwert<String> {

    private static final PruefzifferVerfahren<String> MOD97 = Mod97Verfahren.getInstance();

    /** Konstante fuer unbekannte IBAN (aus Wikipedia, aber mit korrigierter Pruefziffer). */
    public static final IBAN UNBEKANNT = new IBAN("DE07123412341234123412");

    /**
     * Instantiiert eine neue IBAN.
     *
     * @param iban im unformattierten Format
     */
    public IBAN(String iban) {
        super(MOD97.validate(StringUtils.remove(iban, ' ').toUpperCase()));
    }

    /**
     * Liefert die IBAN formattiert in der DIN-Form. Dies ist die uebliche
     * Papierform, in der die IBAN in 4er-Bloecke formattiert wird, jeweils
     * durch Leerzeichen getrennt.
     *
     * @return formatierte IBAN, z.B. "DE19 1234 1234 1234 1234 12"
     */
    public String getFormatted() {
        String input = this.getUnformatted() + "   ";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.getUnformatted().length(); i+= 4) {
            buf.append(input.substring(i, i+4));
            buf.append(' ');
        }
        return buf.toString().trim();
    }

    /**
     * Liefert die unformattierte IBAN.
     *
     * @return unformattierte IBA
     */
    public String getUnformatted() {
        return this.getCode();
    }

    /**
     * Liefert das Land, zu dem die IBAN gehoert.
     *
     * @return z.B. "DE" (als Locale)
     * @since 0.1.0
     */
    public Locale getLand() {
        String country = this.getUnformatted().substring(0, 2);
        return new Locale(country);
    }

    /**
     * Extrahiert aus der IBAN die Bankleitzahl.
     *
     * @return Bankleitzahl
     * @since 0.1.0
     */
    public BLZ getBLZ() {
        String iban = this.getUnformatted();
        return new BLZ(iban.substring(4, 12));
    }

    /**
     * Extrahiert aus der IBAN die Kontonummer.
     *
     * @return 10-stellige Kontonummer
     * @since 0.1.0
     */
    public Kontonummer getKontonummer() {
        String iban = this.getUnformatted();
        return new Kontonummer(iban.substring(12));
    }

}
