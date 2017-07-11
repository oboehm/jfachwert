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
 * (c)reated 11.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung;

import java.io.*;
import java.util.*;

/**
 * Bei der LaengenPruefung wird nur die Laenge des Fachwertes geprueft, ob
 * er zwischen der erlaubten Minimal- und Maximallaenge liegt. Ist die
 * Minimallaenge 0, sind leere Werte erlaubt, ist die Maximallaenge unendlich
 * (bzw. groesster Integer-Wert), gibt es keine Laengenbeschraenkung.
 *
 * @author oboehm
 * @since 0.3.1 (11.07.2017)
 */
public class LaengenPruefung<T extends Serializable> extends NoopVerfahren<T> {

    private final int min;
    private final int max;

    public LaengenPruefung(int min) {
        this(min, Integer.MAX_VALUE);
    }

    public LaengenPruefung(int min, int max) {
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
            throw new IllegalLengthException(Objects.toString(wert), min, max);
        }
        return wert;
    }

}
