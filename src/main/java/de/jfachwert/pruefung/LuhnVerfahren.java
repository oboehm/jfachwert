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

import de.jfachwert.PruefzifferVerfahren;
import org.apache.commons.lang3.StringUtils;

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
public class LuhnVerfahren implements PruefzifferVerfahren<String> {

    /**
     * Liefert true zurueck, wenn der uebergebene Wert gueltig ist.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return true oder false
     */
    @Override
    public boolean isValid(String wert) {
        if (StringUtils.length(wert) < 1) {
            return false;
        } else {
            return getPruefziffer(wert).equals(berechnePruefziffer(wert.substring(0, wert.length() - 1)));
        }
    }

    /**
     * Meistens ist die letzte Ziffer die Pruefziffer, die hierueber abgefragt
     * werden kann.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return meist ein Wert zwischen 0 und 9
     */
    @Override
    public String getPruefziffer(String wert) {
        return wert.substring(wert.length() - 1);
    }

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes.
     *
     * @param wert Wert (ohne Pruefziffer)
     * @return errechnete Pruefziffer
     */
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
