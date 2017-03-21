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
import de.jfachwert.bank.IBAN;

import java.math.BigDecimal;

/**
 * Die Klasse Modulo97Verfahren implementiert das Modulo97-Verfahren nach
 * ISO 7064, das fuer die Validierung einer IBAN verwendet wird.
 *
 * @author oboehm
 * @since 0.1.0
 */
public class Modulo97Verfahren implements PruefzifferVerfahren<String> {

    private static final Modulo97Verfahren INSTANCE = new Modulo97Verfahren();

    private Modulo97Verfahren() {
    }

    /**
     * Liefert die einzige Instanz dieses Verfahrens.
     *
     * @return the instance
     */
    public static PruefzifferVerfahren getInstance() {
        return INSTANCE;
    }

    /**
     * Bei der IBAN ist die Pruefziffer 2-stellig und folgt der Laenderkennung.
     *
     * @param wert z.B. "DE68 2105 0170 0012 3456 78"
     * @return z.B. "68"
     */
    @Override
    public String getPruefziffer(String wert) {
        return wert.substring(2, 4);
    }

    /**
     * Ueberprueft die Pruefziffer.
     *
     * @param wert z.B. "DE68 2105 0170 0012 3456 78"
     * @return true, falls die Pruefziffer uebereinstimmt
     */
    @Override
    public boolean isValid(String wert) {
        String pruefziffer = getPruefziffer(wert);
        return pruefziffer.equals(berechnePruefziffer(wert));
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
     * @param raw z.B. "DE00 2105 0170 0012 3456 78"
     * @return z.B. "68"
     */
    @Override
    public String berechnePruefziffer(String raw) {
        return berechnePruefziffer(new IBAN(raw));
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
     * @param iban z.B. "DE00 2105 0170 0012 3456 78"
     * @return z.B. "68"
     */
    // FIXME: hart codiert auf DE (= "1314")
    public String berechnePruefziffer(IBAN iban) {
        String umgestellt = iban.getBLZ().toString() + iban.getKontonummer() + "131400";
        BigDecimal number = new BigDecimal(umgestellt);
        BigDecimal modulo = number.remainder(BigDecimal.valueOf(97));
        int ergebnis = 98 - modulo.intValue();
        return String.format("%02d", ergebnis);
    }

}
