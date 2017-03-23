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
 * (c)reated 21.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.pruefung;

import de.jfachwert.PruefzifferVerfahren;

import java.math.BigDecimal;

/**
 * Die Klasse Mod97Verfahren implementiert das Modulo97-Verfahren nach
 * ISO 7064, das fuer die Validierung einer IBAN verwendet wird.
 *
 * @author oboehm
 * @since 0.1.0
 */
public class Mod97Verfahren implements PruefzifferVerfahren<String> {

    private static final Mod97Verfahren INSTANCE = new Mod97Verfahren();

    private Mod97Verfahren() {
    }

    /**
     * Liefert die einzige Instanz dieses Verfahrens.
     *
     * @return the instance
     */
    public static PruefzifferVerfahren<String> getInstance() {
        return INSTANCE;
    }

    /**
     * Bei der IBAN ist die Pruefziffer 2-stellig und folgt der Laenderkennung.
     *
     * @param wert z.B. "DE68 2105 0170 0012 3456 78"
     * @return z.B. "68"
     */
    public String getPruefziffer(String wert) {
        return wert.substring(2, 4);
    }

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes (ohne Pruefziffer).
     * Ohne Pruefziffer heisst dabei, dass anstelle der Pruefziffer die
     * uebergebene IBAN eine "00" enthalten kann.
     * <p>
     * Die Pruefziffer selbst wird dabei nach dem Verfahren umgesetzt, das in
     * Wikipedia beschrieben ist:
     * </p>
     * <ol>
     *     <li>
     *         Setze die beiden Pruefziffern auf 00 (die IBAN beginnt dann z. B.
     *         mit DE00 für Deutschland).
     *     </li>
     *     <li>
     *         Stelle die vier ersten Stellen an das Ende der IBAN.
     *     </li>
     *     <li>
     *         Ersetze alle Buchstaben durch Zahlen, wobei A = 10, B = 11, …, Z = 35.
     *     </li>
     *     <li>
     *         Berechne den ganzzahligen Rest, der bei Division durch 97 bleibt.
     *     </li>
     *     <li>
     *         Subtrahiere den Rest von 98, das Ergebnis sind die beiden
     *         Pruefziffern. Falls das Ergebnis einstellig ist, wird es mit
     *         einer fuehrenden Null ergaenzt.
     *     </li>
     * </ol>
     *
     * @param wert z.B. "DE00 2105 0170 0012 3456 78"
     * @return z.B. "68"
     */
    public String berechnePruefziffer(String wert) {
        char[] land = wert.substring(0, 2).toUpperCase().toCharArray();
        String umgestellt = wert.substring(4) + toZahl(land[0]) + toZahl(land[1]) + "00";
        BigDecimal number = new BigDecimal(umgestellt);
        BigDecimal modulo = number.remainder(BigDecimal.valueOf(97));
        int ergebnis = 98 - modulo.intValue();
        return String.format("%02d", ergebnis);
    }

    private static int toZahl(char c) {
        return 10 + c - 'A';
    }

}
