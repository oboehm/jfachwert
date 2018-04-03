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
 * (c)reated 06.10.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.Range;

/**
 * Der AccessValidator ueberprueft den Zugriff auf Arrays, ob er gueltig ist
 * oder mit einem ungueltigen Index erfolgte. Ansonsten wird eine
 * {@link javax.validation.ValidationException} geworfen.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.5
 */
public class AccessValidator {

    /** Utility-Klasse - wird nicht instanziiert. */
    private AccessValidator() {
    }

    /**
     * Liefert das n-te Element des uebergebenen Arrays zurueck, falls ein
     * korrekter Index uebergaben wird
     *
     * @param <T>   Typ-Parameter
     * @param array Array, auf das zugegriffen wird
     * @param n     Array-Index, beginnend bei 0
     * @return n -te Element des Arrays
     */
    public static <T> T access(T[] array, int n) {
        int max = array.length - 1;
        if ((n < 0) || (n > max)) {
            throw new InvalidValueException(n, "n", Range.between(0, max));
        }
        return array[n];
    }

}
