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
 * (c)reated 27.06.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.exception.InvalidValueException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Die Klasse EMailValidator validiert vornehmlich E-Mail-Adressen.
 *
 * @author oboehm
 * @since 0.3 (27.06.2017)
 */
public class EMailValidator implements SimpleValidator<String> {

    private final Pattern addressPattern;

    /**
     * Hier wird der E-Mail-SimpleValidator mit einerm Pattern von
     * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
     * aufgesetzt.
     */
    public EMailValidator() {
        this(Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"));
    }

    /**
     * Dieser Konstruktor ist fuer abgeleitete Klassen gedacht, die das Pattern
     * fuer die Adress-Validierung ueberschreiben moechten.
     *
     * @param pattern Pattern fuer die Adress-Validerung
     */
    protected EMailValidator(Pattern pattern) {
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
    public String validateAdresse(String emailAdresse) {
        Matcher matcher = addressPattern.matcher(emailAdresse);
        if (matcher.matches()) {
            return emailAdresse;
        }
        throw new InvalidValueException(emailAdresse, "email_address");
    }

    /**
     * Ruft zur Pruefung die {@link #validateAdresse(String)}-Methode auf.
     *
     * @param account zu pruefende Adresse
     * @return Wert selber, wenn er gueltig ist
     */
    @Override
    public String validate(String account) {
        return validateAdresse(account);
    }

}
