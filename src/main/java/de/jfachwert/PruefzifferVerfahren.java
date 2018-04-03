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

import de.jfachwert.pruefung.exception.PruefzifferException;

import java.io.Serializable;

/**
 * Viele Fachwerte wie IBAN, ISBN oder Steuernummer besitzen eine Pruefziffer,
 * die sich mithilfe dieses Interfaces ueberpruefen lassen.
 *
 * Prinzipiell kann die Pruefziffer auch ein beliebiges Zeichen sein, aber
 * meistens ist es tatsaechlich ein oder mehrere Ziffern, weswegen dieses
 * Interface nicht PruefzeichenVerfahren, sondern letztendlich
 * PruefzifferVerfahren heisst.
 *
 * @param <T> Typ, der vom Fachwert verwendet wird
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.1.0
 */
public interface PruefzifferVerfahren<T extends Serializable> extends SimpleValidator<T> {

    /**
     * Meistens ist die letzte Ziffer die Pruefziffer, die hierueber abgefragt
     * werden kann.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return meist ein Wert zwischen 0 und 9
     */
    T getPruefziffer(T wert);

    /**
     * Berechnet die Pruefziffer des uebergebenen Wertes.
     *
     * @param wert Wert
     * @return errechnete Pruefziffer
     */
    T berechnePruefziffer(T wert);

    /**
     * Liefert true zurueck, wenn der uebergebene Wert gueltig ist.
     *
     * @param wert Fachwert oder gekapselter Wert
     * @return true oder false
     */
    default boolean isValid(T wert) {
        T pruefziffer = getPruefziffer(wert);
        return pruefziffer.equals(berechnePruefziffer(wert));
    }

    /**
     * Validiert den uebergebenen Wert. Falls dieser nicht stimmt, sollte eine
     * javax.validation.ValidationException geworfen werden.
     *
     * @param wert zu ueberpruefender Wert
     * @return den ueberprueften Wert (zur Weiterverarbeitung)
     */
    default T validate(T wert) {
        if (!isValid(wert)) {
            throw new PruefzifferException(wert, berechnePruefziffer(wert), getPruefziffer(wert));
        }
        return wert;
    }

}
