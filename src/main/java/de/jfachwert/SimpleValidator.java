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
 * (c)reated 20.08.17 by oliver (ob@oasd.de)
 */
package de.jfachwert;

import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;
import de.jfachwert.pruefung.exception.NullValueException;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Um die verschiedenen Validatoren als allgemeines Attribut verwendenen
 * zu koennen, sind die Gemeinsamkeiten in diesem Interface zusammengefasst.
 * <p>
 * Damit das Interface nicht mit dem Validator-Interface aus javax.validation
 * verwechselt wird, wurde es in SimpleValidator umbenannt.
 * </p>
 * <p>
 * Mit v4 wurde dieses Interface anfangs ebenfalls nach Kotlin ueberfuehrt.
 * Allerdings hat Kotlin bei Interfaces mit Default-Implementierung noch
 * Kompatibiltaetsprobleme (https://youtrack.jetbrains.com/issue/KT-4779).
 * Daher wurde die urspruengliche Implementierung wieder restauriert und die
 * Kotlin-Variante nach KSimpleValidator verschoben
 * </p>
 *
 * @since 0.4 (20.08.17)
 */
public interface SimpleValidator<T extends Serializable> extends Serializable {

    static final Logger logger = Logger.getLogger(SimpleValidator.class.getName());

    /**
     * Wenn der uebergebene Wert gueltig ist, soll er unveraendert
     * zurueckgegeben werden, damit er anschliessend von der aufrufenden
     * Methode weiterverarbeitet werden kann. Ist der Wert nicht gueltig,
     * soll eine {@link javax.validation.ValidationException} geworfen
     * werden.
     *
     * @param value Wert, der validiert werden soll
     * @return Wert selber, wenn er gueltig ist
     */
    T validate(T value);

    default Object validateObject(Object value) {
        try {
            return validate((T) value);
        } catch (ClassCastException ex) {
            logger.log(Level.FINE, "cannot cast " + value, ex);
        }
        if (value == null) {
            throw new NullValueException();
        }
        return value;
    }

    /**
     * Im Unterschied zur {@link #validate(Serializable)}-Methode wird hier
     * eine {@link IllegalArgumentException} geworfen, wenn der Wert kein
     * gueltiges Argument ist.
     *
     * @param value Wert, der verifiziert werden soll
     * @return Wert selber, wenn er gueltig ist
     */
    default T verify(T value) {
        try {
            return validate(value);
        } catch (ValidationException ex) {
            throw new LocalizedIllegalArgumentException(ex);
        }
    }

}
