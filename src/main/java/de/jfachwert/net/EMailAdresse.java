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
 * (c)reated 23.06.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.*;
import de.jfachwert.pruefung.*;

/**
 * Eine E-Mail-Adresse ist die eindeutige Absender- und Empfaengeradresse im
 * E-Mail-Verkehr. Sie besteht aus zwei Teilen, die durch ein @-Zeichen
 * voneinander getrennt sind:
 * <ul>
 *     <li>
 *         Der lokale Teil, im Englischen local-part genannt,
 *         steht vor dem @-Zeichen.
 *     </li>
 *     <li>
 *         Der globale Teil, im Englischen domain-part genannt,
 *         steht nach dem @-Zeichen.
 *     </li>
 * </ul>
 * Bei der E-Mail-Adresse "email@example.com" ist "email" der lokale Teil
 * und "example.com" der globale Teil.
 *
 * @author oboehm
 * @since 0.3 (23.06.2017)
 */
public class EMailAdresse extends AbstractFachwert<String> {

    /**
     * Legt eine Instanz einer EMailAdresse an.
     *
     * @param emailAdresse eine gueltige Adresse, z.B. "max@mustermann.de"
     */
    public EMailAdresse(String emailAdresse) {
        super(validate(emailAdresse));
    }

    /**
     * Fuehrt ein sehr einfache Pruefung der uebegebenen E-Mail-Adresse durch.
     *
     * @param emailAdresse zu pruefende E-Mail-Adresse
     * @return die validierte E-Mail-Adresse (zur Weiterverarbeitung)
     */
    public static String validate(String emailAdresse) {
        if (!emailAdresse.contains("@")) {
            throw new InvalidValueException(emailAdresse, "email_address");
        }
        return emailAdresse;
    }

}
