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

import de.jfachwert.SimpleValidator;
import de.jfachwert.Text;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.StringUtils;

import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class EMailAdresse extends Text {

    private static final SimpleValidator<String> VALIDATOR = new Validator();
    private static final WeakHashMap<String, EMailAdresse> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen. */
    public static final EMailAdresse NULL = new EMailAdresse("", new NullValidator<>());

    /**
     * Legt eine Instanz einer EMailAdresse an.
     *
     * @param emailAdresse eine gueltige Adresse, z.B. "max@mustermann.de"
     */
    public EMailAdresse(String emailAdresse) {
        this(emailAdresse, VALIDATOR);
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
    public EMailAdresse(String emailAdresse, SimpleValidator<String> validator) {
        super(validator.verify(emailAdresse));
    }

    /**
     * Liefert einen EmailAdresse.
     *
     * @param name gueltige Email-Adresse
     * @return EMailAdresse
     */
    public static EMailAdresse of(String name) {
        return WEAK_CACHE.computeIfAbsent(name, EMailAdresse::new);
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


    /**
     * Die Klasse EMailValidator validiert vornehmlich E-Mail-Adressen.
     * Urspruenglich war er eine separate Klasse, mit v2.2 wurde er anolog
     * zu den anderen Validatoren zur entsprechenden Klasse als innere Klasse
     * verschoben.
     *
     * @author oboehm
     * @since 0.3 (27.06.2017)
     */
    public static class Validator implements SimpleValidator<String> {

        private final Pattern addressPattern;

        /**
         * Hier wird der E-Mail-SimpleValidator mit einerm Pattern von
         * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
         * aufgesetzt.
         */
        public Validator() {
            this(Pattern
                    .compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"));
        }

        /**
         * Dieser Konstruktor ist fuer abgeleitete Klassen gedacht, die das Pattern
         * fuer die Adress-Validierung ueberschreiben moechten.
         *
         * @param pattern Pattern fuer die Adress-Validerung
         */
        protected Validator(Pattern pattern) {
            this.addressPattern = pattern;
        }

        /**
         * Fuehrt ein Pattern-basierte Pruefung der uebegebenen E-Mail-Adresse
         * durch. Schlaegt die Pruefung fehl, wird eine
         * {@link javax.validation.ValidationException} geworfen.
         *
         * @param emailAdresse zu pruefende E-Mail-Adresse
         * @return die validierte E-Mail-Adresse (zur Weiterverarbeitung)
         */
        @Override
        public String validate(String emailAdresse) {
            Matcher matcher = addressPattern.matcher(emailAdresse);
            if (matcher.matches()) {
                return emailAdresse;
            }
            throw new InvalidValueException(emailAdresse, "email_address");
        }

    }

}
