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
 * Das Modulo-10-Verfahren ist auch als Luhn-Alogorithmus oder Luhn-Formel
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
 * <p>
 * Die verschiedenen Modulo10-Verfahren, die es gibt, unterscheiden sich noch
 * in der Gewichtung der einzelnen Ziffern. Naeheres kann man unter
 * https://www.activebarcode.de/codes/checkdigit/modulo10.html nachlesen.
 * </p>
 *
 * @author oboehm
 * @since 1.1 (11.12.2018)
 */
public class Mod10Verfahren implements PruefzifferVerfahren<String> {

    private final int gewichtungUngerade;
    private final int gewichtungGerade;

    /** EAN13 mit Gewichtung 3. */
    public static final Mod10Verfahren EAN13 = new Mod10Verfahren(1, 3);

    /** Code25 wird mit der Gewichtung 3 und 1 berchnet. */
    public static final Mod10Verfahren CODE25 = new Mod10Verfahren(3, 1);

    /** Leitcode oder Identcode hat eine Gewichtung von 4 und 9. */
    public static final Mod10Verfahren LEITCODE = new Mod10Verfahren(4, 9);

    /**
     * Bei dem Standard-Modulo10-Verfahren wird eine Gewichtung von 2
     * verwendet.
     */
    public Mod10Verfahren() {
        this(2);
    }

    /**
     * Die Gewichtung ist fuer die ungeraden Ziffern relevant. Sie werden
     * damit multipliziert, bevor die Quersumme gebildet wird.
     *
     * @param gewichtung typischerweise z.B. 3
     */
    public Mod10Verfahren(int gewichtung) {
        this(gewichtung, 1);
    }

    /**
     * Die Gewichtung gibt an, mit welcher Zahl die ungeraden und geraden
     * Stellen mulitpliziert werden, bevor die Quersumme fuer die Preufung
     * gebildet wird. Bei Barcodes wird hier z.B. die Werte 4 und 9 verwendet.
     *
     * @param ungerade Gewichtung fuer ungerade Ziffern
     * @param gerade   Gewichtung fuer gerade Ziffern
     */
    public Mod10Verfahren(int ungerade, int gerade) {
        this.gewichtungUngerade = ungerade;
        this.gewichtungGerade = gerade;
    }
    
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
     * Berechnet die Pruefziffer des uebergebenen Wertes. Die Berechung stammt
     * aus https://de.wikipedia.org/wiki/Luhn-Algorithmus#Java.
     *
     * @param wert Wert (ohne Pruefziffer)
     * @return errechnete Pruefziffer
     */
    public String berechnePruefziffer(String wert) {
        int sum = getQuersumme(wert);
        return Integer.toString((10 - sum % 10) % 10);
    }

    private int getQuersumme(String wert) {
        char[] digits = wert.toCharArray();
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {
            int digit = Character.digit(digits[i], 10);
            if (i % 2 == 0) {
                digit *= this.gewichtungUngerade;
            } else {
                digit *= this.gewichtungGerade;
            }
            sum += digit;
        }
        return sum;
    }

}
