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

import de.jfachwert.net.Telefonnummer;

import java.util.regex.Pattern;

/**
 * Die Klasse TelefonnummerValidator validiert die Schreibweise von
 * Telefonnummern. Mit v2.2 wurde dieser Validator (analog zu den
 * anderen Validatoren) in die {@link de.jfachwert.net.Telefonnummer}-
 * Klasse verschoben.
 *
 * @author oboehm
 * @since 0.5 (05.09.2017)
 * @deprecated bitte Validator in {@link de.jfachwert.net.Telefonnummer} verwenden
 */
@Deprecated
public class TelefonnummerValidator extends Telefonnummer.Validator {

    /**
     * Hier wird der E-Mail-SimpleValidator mit einerm Pattern von
     * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
     * aufgesetzt.
     */
    public TelefonnummerValidator() {
        super();
    }

    /**
     * Dieser Konstruktor ist fuer abgeleitete Klassen gedacht, die das Pattern
     * fuer die Adress-Validierung ueberschreiben moechten.
     *
     * @param pattern Pattern fuer die Adress-Validerung
     */
    protected TelefonnummerValidator(Pattern pattern) {
        super(pattern);
    }

}
