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

/**
 * Das Modulo-11-Verfahren wird fuer die 11-stellige Steuer-Identifikationsnummer
 * und die Umsatzsteuer-Identificationsnummer verwendet. Sie ist in
 * "DIN ISO 7964, Mod 11, 10" beschreiben (s.a.
 * http://www.jura.uni-sb.de/BGBl/TEIL1/1993/19930736.1.HTML).
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.1.0
 */
public class Mod11Verfahren implements PruefzifferVerfahren<String> {

    private final int anzahlStellen;

    public Mod11Verfahren(int anzahlStellen) {
        this.anzahlStellen = anzahlStellen;
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
     * Diese Methode ist aber nur fuer die 11-stellige Steuer-Identifikationsnummer
     * (TIN) implementiert. Fuer andere Steuernummer kommt eine
     * {@link IllegalArgumentException}.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return the boolean
     */
    @Override
    public boolean isValid(String wert) {
        int n = anzahlStellen + 1;
        if (wert.length() != n) {
            throw new IllegalArgumentException("Steuernummer '" + wert + "' ist nicht " + n + " Zeichen lang");
        }
        String pruefziffer = getPruefziffer(wert);
        return pruefziffer.equals(berechnePruefziffer(wert.substring(0, wert.length() - 1)));
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
        for (int stelle = 1; stelle <= this.anzahlStellen; stelle++) {
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
