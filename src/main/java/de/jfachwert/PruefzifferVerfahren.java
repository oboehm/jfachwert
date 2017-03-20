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
 * See the License
 *
 * for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 19.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert;

import java.io.Serializable;

/**
 * Viele Fachwerte wie IBAN, ISBN oder Steuernummer besitzen eine Pruefziffer,
 * dies sich mithilfe dieses Interfaces ueberpruefen lassen.
 *
 * @param <T> Typ, der vom Fachwert verwendet wird
 * @author <a href="ob@aosd.de">oliver</a>
 * @version $
 * @since 0.1.0
 */
public interface PruefzifferVerfahren<T> extends Serializable {

    /**
     * Meistens ist die letzte Ziffer die Pruefziffer, die hierueber abgefragt
     * werden kann.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return meist ein Wert zwischen 0 und 9
     */
    T getPruefziffer(T wert);

    /**
     * Is valid boolean.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return the boolean
     */
    boolean isValid(T wert);

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes (ohne Pruefziffer).
     *
     * @param raw Wert ohne Pruefziffer
     * @return errechnete Pruefziffer
     */
    T berechnePruefziffer(T raw);

}