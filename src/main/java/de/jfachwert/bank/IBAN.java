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
        this(iban, MOD97);
    }

    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das {@link PruefzifferVerfahren} ueberschreiben koennen.
     * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
     * indem man das {@link de.jfachwert.pruefung.NoopVerfahren} verwendet.
     *
     * @param iban        die IBAN
     * @param pzVerfahren das verwendete PruefzifferVerfahren
     */
    public IBAN(String iban, PruefzifferVerfahren<String> pzVerfahren) {
        super(validate(iban, pzVerfahren));
    }

    /**
     * Mit dieser Methode kann man eine IBAN validieren, ohne dass man erst
     * den Konstruktor aufrufen muss. Falls die Pruefziffer nicht stimmt,
     * wird eine {@link javax.xml.bind.ValidationException} geworfen, wenn
     * die Laenge nicht uebereinstimmt eine {@link IllegalArgumentException}.
     *
     * @param iban die 22-stellige IBAN
     * @return die IBAN in normalisierter Form (ohne Leerzeichen)
     */
    public static String validate(String iban) {
        return validate(iban, MOD97);
    }

    private static String validate(String iban, PruefzifferVerfahren<String> pzVerfahren) {
        String normalized = StringUtils.remove(iban, ' ').toUpperCase();
        if (normalized.length() < 22) {
            throw new IllegalArgumentException(iban + ": zu kurz (" + normalized.length() + " Zeichen)");
        } else if (normalized.length() > 34) {
            throw new IllegalArgumentException(iban + ": zu lang (" + normalized.length() + " Zeichen)");
        }
        return pzVerfahren.validate(normalized);
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
     * @return z.B. "de_DE" (als Locale)
     * @since 0.1.0
     */
    public Locale getLand() {
        String country = this.getUnformatted().substring(0, 2);
        String language = country.toLowerCase();
        switch (country) {
            case "AT":
            case "CH":
                language = "de";
                break;
        }
        return new Locale(language, country);
    }

    /**
     * Liefert die 2-stellige Pruefziffer, die nach der Laenderkennung steht.
     *
     * @return the pruefziffer
     * @since 0.1.0
     */
    public String getPruefziffer() {
        return MOD97.getPruefziffer(this.getUnformatted());
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
     * Extrahiert aus der IBAN die Kontonummer nach der Standard-IBAN-Regel.
     * Ausnahmen, wie sie z.B. in
     * http://www.kigst.de/media/Deutsche_Bundesbank_Uebersicht_der_IBAN_Regeln_Stand_Juni_2013.pdf
     * beschrieben sind, werden nicht beruecksichtigt.
     *
     * @return 10-stellige Kontonummer
     * @since 0.1.0
     */
    public Kontonummer getKontonummer() {
        String iban = this.getUnformatted();
        return new Kontonummer(iban.substring(12));
    }

}
