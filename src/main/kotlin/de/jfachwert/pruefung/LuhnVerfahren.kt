/*
 * Copyright (c) 2018 by Oliver Boehm
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
 * (c)reated 11.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

/**
 * Das Luhn-Verfahren ist auch als Luhn-Alogorithmus oder Luhn-Formel
 * bekannt und ist eine einfache Methode zur Berechnung einer Pruefsumme.
 * Das Verfahren dient u.a. zur Verifizierung von:
 * <ul>
 * <li>Kreditkartennummern,</li>
 * <li>Sozialversicherungsnummern,</li>
 * <li>Nummern von Lokomotiven und Triebwagen.</li>
 * </ul>
 * <p>
 * Die Pruefziffer ergibt sich aus der Pruefsumme modulo 10. Sie wird an
 * die bestehende Zahl angehaengt.
 * </p>
 *
 * @author oboehm
 * @since 1.1 (11.12.2018)
 */
public class LuhnVerfahren extends Mod10Verfahren {

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes.
     *
     * @param wert Wert (ohne Pruefziffer)
     * @return errechnete Pruefziffer
     */
    @Override
    public String berechnePruefziffer(String wert) {
        int sum = getQuersumme(wert);
        return Integer.toString(sum % 10);
    }

    private static int getQuersumme(String wert) {
        char[] digits = wert.toCharArray();
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {
            // get digits in reverse order
            int digit = Character.digit(digits[length - i - 1], 10);
            // every 2nd number multiply with 2
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }
        return sum;
    }

}
