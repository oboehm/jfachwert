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
import org.apache.commons.lang3.*;

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

    private static final EMailValidator DEFAULT_VALIDATOR = new EMailValidator();

    /**
     * Legt eine Instanz einer EMailAdresse an.
     *
     * @param emailAdresse eine gueltige Adresse, z.B. "max@mustermann.de"
     */
    public EMailAdresse(String emailAdresse) {
        this(emailAdresse, DEFAULT_VALIDATOR);
    }

    /**
     * Legt eine Instanz einer EMailAdresse an. Dieser Konstruktor ist
     * hauptsaechlich fuer abgeleitete Klassen gedacht, die ihre eigene
     * Validierung mit einbringen wollen oder aus Performance-Gruenden
     * abschalten wollen.
     *
     * @param emailAdresse eine gueltige Adresse, z.B. "max@mustermann.de"
     * @param validator    SimpleValidator zur Adressen-Validierung
     */
    public EMailAdresse(String emailAdresse, EMailValidator validator) {
        super(validator.validateAdresse(emailAdresse));
    }

    /**
     * Als Local Part wird der Teil einer E-Mail-Adresse bezeichnet, der die
     * Adresse innerhalb der Domain des E-Mail-Providers eindeutig bezeichnet.
     * Typischerweise entspricht der Lokalteil dem Benutzernamen (haeufig ein
     * Pseudonym) des Besitzers des E-Mail-Kontos.
     *
     * @return z.B. "Max.Mustermann"
     */
    public String getLocalPart() {
        return StringUtils.substringBeforeLast(this.getCode(), "@");
    }

    /**
     * Der Domain Part, der hinter dem @-Zeichen steht und fuer den die
     * Syntaxregeln des Domain Name Systems gelten, besteht mindestens aus drei
     * Teilen: einem Hostnamen (z. B. ein Firmenname), einem Punkt und einer
     * Top-Level-Domain.
     *
     * @return z.B. "fachwert.de"
     */
    public Domainname getDomainPart() {
        return new Domainname(StringUtils.substringAfterLast(this.getCode(), "@"));
    }

}
