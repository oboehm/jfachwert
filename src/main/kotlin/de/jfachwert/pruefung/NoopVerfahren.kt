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
 * (c)reated 26.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.pruefung;

import de.jfachwert.PruefzifferVerfahren;

import java.io.*;

/**
 * "Noop" steht fuer "No Operation" und bedeutet, dass mit diesem Pruefziffer-
 * Verfahren keine Validierung stattfindet. Dies kann immer dann verwendet
 * werden, wenn man die Validierung abschalten will.
 *
 * @author oboehm
 * @since 0.1.0
 */
public class NoopVerfahren<T extends Serializable> implements PruefzifferVerfahren<T> {

    /**
     * Meistens ist die letzte Ziffer die Pruefziffer, die hierueber abgefragt
     * werden kann.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return meist ein Wert zwischen 0 und 9
     */
    @Override
    public T getPruefziffer(T wert) {
        return wert;
    }

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes.
     *
     * @param wert Wert
     * @return errechnete Pruefziffer
     */
    @Override
    public T berechnePruefziffer(T wert) {
        return wert;
    }

}
