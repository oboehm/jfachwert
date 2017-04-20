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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

/**
 * Die Klasse LengthValidator ist eine rein statische Klassen fuer die
 * Laengenvalidierung.
 *
 * @author oboehm
 * @since 0.2 (20.04.2017)
 */
public final class LengthValidator {

    private LengthValidator() {
    }

    /**
     * Validiert die Laenge des uebergebenen Wertes.
     *
     * @param value zu pruefender Wert
     * @param expected erwartete Laenge
     * @return der gepruefte Wert (zur evtl. Weiterverarbeitung)
     */
    public static String validate(String value, int expected) {
        return validate(value, expected, expected);
    }

    /**
     * Validiert die Laenge des uebergebenen Wertes.
     *
     * @param value zu pruefender Wert
     * @param min   geforderte Minimal-Laenge
     * @param max   Maximal-Laenge
     * @return der gepruefte Wert (zur evtl. Weiterverarbeitung)
     */
    public static String validate(String value, int min, int max) {
        if ((value.length() < min) || (value.length() > max)) {
            throw new IllegalLengthException(value, min, max);
        }
        return value;
    }

}
