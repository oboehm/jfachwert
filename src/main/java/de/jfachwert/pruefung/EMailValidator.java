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

import de.jfachwert.net.EMailAdresse;

import java.util.regex.Pattern;

/**
 * Die Klasse EMailValidator validiert vornehmlich E-Mail-Adressen.
 * Mit v2.2 wurde dieser Validator in die
 * {@link de.jfachwert.net.EMailAdresse}-Klasse verschoben.
 *
 * @author oboehm
 * @since 0.3 (27.06.2017)
 * @deprecated bitte Validator aus {@link de.jfachwert.net.EMailAdresse} verwenden
 */
@Deprecated
public class EMailValidator extends EMailAdresse.Validator {

    /**
     * Hier wird der E-Mail-SimpleValidator mit einerm Pattern von
     * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
     * aufgesetzt.
     */
    public EMailValidator() {
        super();
    }

    /**
     * Dieser Konstruktor ist fuer abgeleitete Klassen gedacht, die das Pattern
     * fuer die Adress-Validierung ueberschreiben moechten.
     *
     * @param pattern Pattern fuer die Adress-Validerung
     */
    protected EMailValidator(Pattern pattern) {
        super(pattern);
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
        return validate(emailAdresse);
    }

}
