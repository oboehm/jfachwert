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

import de.jfachwert.*;
import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.*;

import java.util.regex.*;

/**
 * Die Klasse TelefonnummerValidator validiert die Schreibweise von
 * Telefonnummern.
 *
 * @author oboehm
 * @since 0.5 (05.09.2017)
 */
public class TelefonnummerValidator implements SimpleValidator<String> {

    private final Pattern pattern;
    private final LengthValidator<String> lengthValidator = new LengthValidator<>(3, 15);

    /**
     * Hier wird der E-Mail-SimpleValidator mit einerm Pattern von
     * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
     * aufgesetzt.
     */
    public TelefonnummerValidator() {
        this(Pattern.compile("[0-9-+/ ()]+"));
    }

    /**
     * Dieser Konstruktor ist fuer abgeleitete Klassen gedacht, die das Pattern
     * fuer die Adress-Validierung ueberschreiben moechten.
     *
     * @param pattern Pattern fuer die Adress-Validerung
     */
    protected TelefonnummerValidator(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Ueberprueft die Telefonnummer, ob sie nur erlaubte Nummern (und
     * Sonderzeichen) enthaelt.
     *
     * @param nummer zu pruefende Telefonnummer
     * @return Wert selber, wenn er gueltig ist
     */
    @Override
    public String validate(String nummer) {
        Matcher matcher = pattern.matcher(nummer);
        if (matcher.matches()) {
            String normalized = StringUtils.removeAll(nummer, "[ \t+-/]|(\\(0\\))");
            lengthValidator.validate(normalized);
            return nummer;
        }
        throw new InvalidValueException(nummer, "phone_number");
    }

}
