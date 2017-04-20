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
 * (c)reated 11.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.pruefung.IllegalLengthException;

/**
 * BIC steht fuer Bank (oder auch Businiess) Identifier Code und kennzeichnet
 * weltweit Kreditinstitute, Broker oder aehnliche Unternehmen. Im Allegemeinen
 * wird die BIC im Zahlungsverkehr zusammen mit der IBAN verwendet.
 * <p>
 *     Der BIC hat eine Laenge von 11 oder 14 alphanumerischen Zeichen mit
 *     folgendem Aufbau: BBBBCCLLbbb
 * </p>
 * <ul>
 *     <li>
 *         BBBB: 4-stelliger Bankcode, vom Geldinstitut frei waehlbar
 *         (nur Buchstaben)
 *     </li>
 *     <li>
 *         CC: 2-stelliger Laendercode nach ISO 3166-1 (nur Buchstaben)
 *     </li>
 *     <li>
 *         LL: 2-stellige Codierung des Ortes (Buchstaben/Ziffern)
 *     </li>
 *     <li>
 *         bbb: 3-stellige Kennzeichnung (Branche-Code) der Filiale oder
 *         Abteilung. Kann um "XXX" auf 6-stellig ergaenzt werden
 *         (Buchstaben/Ziffern)
 *     </li>
 * </ul>
 *
 * @author <a href="ob@aosd.de">oliver</a>
 */
public class BIC extends AbstractFachwert<String> {

    /**
     * Hierueber wird eine neue BIC angelegt.
     *
     * @param code eine 11- oder 14-stellige BIC
     */
    public BIC(String code) {
        super(validate(code));
    }

    /**
     * Hierueber kann man eine BIC ohne den Umweg ueber den Konstruktor
     * validieren.
     *
     * @param bic die BIC (11- oder 14-stellig)
     * @return die validierte BIC (zur Weiterverarbeitung)
     */
    public static String validate(String bic) {
        if ((bic.length() != 11) && (bic.length() != 14)) {
            throw new IllegalLengthException(bic, 11, 14);
        }
        return bic;
    }

}
