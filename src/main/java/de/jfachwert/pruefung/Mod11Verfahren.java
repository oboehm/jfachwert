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
 * (c)reated 19.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import de.jfachwert.PruefzifferVerfahren;

import javax.validation.ValidationException;

/**
 * Das Modulo-11-Verfahren ueberprueft momentan nur die 11-stellige
 * Steuer-Identifikationsnummer. Andere Steuernummern werden (noch) nicht
 * unterstuetzt.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.1.0
 */
public class Mod11Verfahren implements PruefzifferVerfahren<String> {

    private static final Mod11Verfahren INSTANCE = new Mod11Verfahren();

    private Mod11Verfahren() {
    }

    /**
     * Liefert die einzigen Instanz.
     *
     * @return die einzige Instanz
     */
    public static PruefzifferVerfahren<String> getInstance() {
        return INSTANCE;
    }

    /**
     * Die letzte Ziffer ist die Pruefziffer, die hierueber abgefragt werden
     * kann.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return Wert zwischen 0 und 9
     */
    public String getPruefziffer(String wert) {
        return wert.substring(wert.length() - 1);
    }

    /**
     * Is valid boolean.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return the boolean
     */
    public boolean isValid(String wert) {
        if (wert.length() != 11) {
            throw new IllegalArgumentException("Steuernummer '" + wert + "' ist nicht 11 Zeichen lang");
        }
        String pruefziffer = getPruefziffer(wert);
        return pruefziffer.equals(berechnePruefziffer(wert.substring(0, wert.length() - 1)));
    }

    /**
     * Validiert den uebergebenen Wert. Falls dieser nicht stimmt, wird eine
     * {@link ValidationException} geworfen werden.
     *
     * @param wert zu ueberpruefender Wert
     */
    public void validate(String wert) {
        if (!isValid(wert)) {
            throw new PruefzifferException(wert, this);
        }
    }

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes.
     * Die Berechung stammt aus Wikipedia und wurde nach Java uebersetzt
     * (s. https://de.wikipedia.org/wiki/Steuer-Identifikationsnummer).
     * <p>
     * Der Ausgangswert fuer die Berechnung kann mit oder ohne Pruefziffer
     * uebergeben werden. Es werden nur die ersten 10 Ziffern zur Ermittlung
     * der Pruefziffer herangezogen.
     * </p>
     *
     * @param wert Wert (mit oder ohne Pruefziffer)
     * @return errechnete Pruefziffer
     */
    public String berechnePruefziffer(String wert) {
        char[] ziffernfolge = wert.toCharArray();
        int produkt = 10;
        for (int stelle = 1; stelle <= 10; stelle++) {
            int summe = (Character.getNumericValue(ziffernfolge[stelle-1]) + produkt) % 10;
            if (summe == 0) {
                summe = 10;
            }
            produkt = (summe * 2) % 11;
        }
        int pruefziffer = 11 - produkt;
        if (pruefziffer == 10) {
            pruefziffer = 0;
        }
        return Integer.toString(pruefziffer);
    }

}
