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
import de.jfachwert.pruefung.Modulo11Verfahren;

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
 *
 * @author oboehm
 * @since 0.0.2
 */
public class Steuernummer extends AbstractFachwert<String> {

    public static final PruefzifferVerfahren<String> PRUEFZIFFER_VERFAHREN = Modulo11Verfahren.getInstance();

    /**
     * Hierueber wird eine neue Steuernummer angelegt.
     *
     * @param nr eine 10- bis 13-stellige Steuernummer.
     */
    public Steuernummer(String nr) {
        super(nr);
    }

    /**
     * Die letzte Ziffer ist die Pruefziffer, die hierueber abgefragt werden
     * kann.
     *
     * @return Wert zwischen 0 und 9
     */
    public int getPruefziffer() {
        return Integer.valueOf(PRUEFZIFFER_VERFAHREN.getPruefziffer(this.getCode()));
    }

    /**
     * Ueberprueft anhand der Pruefziffer, ob die Steuernummer gueltig ist.
     * Diese Methode ist aber nur fuer die 11-stellige Steuer-Identifikationsnummer
     * (TIN) implementiert. Fuer andere Steuernummer kommt eine
     * {@link IllegalArgumentException}.
     *
     * @return true, falls Pruefziffer gueltig ist
     */
    public boolean isValid() {
        return PRUEFZIFFER_VERFAHREN.isValid(this.getCode());
    }

}
