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

import de.jfachwert.*;
import de.jfachwert.pruefung.exception.InvalidLengthException;

import java.io.*;
import java.util.*;

/**
 * Bei der Laengen-Validierung wird nur die Laenge des Fachwertes geprueft, ob
 * er zwischen der erlaubten Minimal- und Maximallaenge liegt. Ist die
 * Minimallaenge 0, sind leere Werte erlaubt, ist die Maximallaenge unendlich
 * (bzw. groesster Integer-Wert), gibt es keine Laengenbeschraenkung.
 *
 * Urspruenglich besass diese Klasse rein statisiche Methode fuer die
 * Laengenvaliderung. Ab v0.3.1 kann sie auch anstelle eines
 * Pruefziffernverfahrens eingesetzt werden.
 *
 * @author oboehm
 * @since 0.2 (20.04.2017)
 */
public class LengthValidator<T extends Serializable> extends NoopVerfahren<T> {

    public static final PruefzifferVerfahren<String> NOT_EMPTY_VALIDATOR = new LengthValidator<>(1);

    private final int min;
    private final int max;

    public LengthValidator(int min) {
        this(min, Integer.MAX_VALUE);
    }

    public LengthValidator(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Liefert true zurueck, wenn der uebergebene Wert innerhalb der erlaubten
     * Laenge liegt.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return true oder false
     */
    @Override
    public boolean isValid(T wert) {
        int length = Objects.toString(wert, "").length();
        return (length >= min) && (length <= max);
    }

    /**
     * Ueberprueft, ob der uebergebenen Werte innerhalb der min/max-Werte
     * liegt.
     *
     * @param wert zu ueberpruefender Wert
     * @return den ueberprueften Wert (zur Weiterverarbeitung)
     */
    @Override
    public T validate(T wert) {
        if (!isValid(wert)) {
            throw new InvalidLengthException(Objects.toString(wert), min, max);
        }
        return wert;
    }

    /**
     * Validiert die Laenge des uebergebenen Wertes.
     *
     * @param value zu pruefender Wert
     * @param expected erwartete Laenge
     * @return der gepruefte Wert (zur evtl. Weiterverarbeitung)
     */
    public static String validate(String value, int expected) {
        if (value.length() != expected) {
            throw new InvalidLengthException(value, expected);
        }
        return value;
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
        if (min == max) {
            return validate(value, min);
        }
        if ((value.length() < min) || (value.length() > max)) {
            throw new InvalidLengthException(value, min, max);
        }
        return value;
    }

}
