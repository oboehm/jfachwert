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
 * (c)reated 13.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.PruefzifferVerfahren;
import de.jfachwert.math.PackedDecimal;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.Mod11Verfahren;

/**
 * Die Steuernummer oder Steuer-Identnummer ist eine eindeutige Nummer, die vom
 * Finanzamt vergeben wird. Die Nummer ist eindeutig einem Steuerpflichtigen
 * zugeordnet.
 *
 * Die Laenge der Steuernummer variierte beim Standardschema der Laender
 * zwischen 10 und 11 Ziffern und hatte für das Bundesschema einheitlich 13
 * Ziffern.
 *
 * Seit 2008 ist die Steuernummer durch die Steuer-Identifikationsnummer
 * abgeloest, die aus 10 Ziffer + Pruefziffer besteht. Diese Unterscheidung
 * wird in dieser Klasse aber (noch) nicht vorgenommen.
 * <p>
 * Zur Reduzierung des internen Speicherverbrauchs wird die BLZ als
 * {@link PackedDecimal} abgelegt.
 * </p>
 *
 * @author oboehm
 * @since 0.0.2
 */
public class Steuernummer extends AbstractFachwert<PackedDecimal> {

    private static final PruefzifferVerfahren<String> MOD11 = new Mod11Verfahren(10);

    /**
     * Hierueber wird eine neue Steuernummer angelegt.
     *
     * @param nr eine 10- bis 13-stellige Steuernummer.
     */
    public Steuernummer(String nr) {
        this(nr, MOD11);
    }

    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das {@link PruefzifferVerfahren} ueberschreiben koennen.
     * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
     * indem man das {@link de.jfachwert.pruefung.NoopVerfahren} verwendet.
     *
     * @param nr          die Steuernummer
     * @param pzVerfahren das verwendete PruefzifferVerfahren
     */
    public Steuernummer(String nr, PruefzifferVerfahren<String> pzVerfahren) {
        super(PackedDecimal.valueOf(validate(nr, pzVerfahren)));
    }

    /**
     * Die Steuernummer muss zwischen 10 und 13 Stellen lang sein und die
     * Pruefziffer muss stimmen (falls sie bekannt ist).
     *
     * @param nr die Steuernummer
     * @return die validierte Steuernummer zur Weiterverarbeitung
     */
    public static String validate(String nr) {
        return validate(nr, MOD11);
    }

    private static String validate(String nr, PruefzifferVerfahren<String> pzVerfahren) {
        LengthValidator.validate(nr, 10, 13);
        if (nr.length() == 11) {
            return pzVerfahren.validate(nr);
        }
        return nr;
    }

    /**
     * Die letzte Ziffer ist die Pruefziffer, die hierueber abgefragt werden
     * kann.
     *
     * @return Wert zwischen 0 und 9
     */
    public int getPruefziffer() {
        return Integer.valueOf(MOD11.getPruefziffer(this.getCode().toString()));
    }

}
