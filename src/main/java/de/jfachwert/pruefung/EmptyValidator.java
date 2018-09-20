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
package de.jfachwert.pruefung;

import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.exception.EmptyValueException;
import de.jfachwert.pruefung.exception.NullValueException;

/**
 * Der EmptyValidator verhindert, dass 'null' oder ein leerer String als valider
 * Wert durchgereicht wird.
 *
 * @since 1.0
 */
public class EmptyValidator implements SimpleValidator<String> {

    /**
     * Wenn der uebergebene Wert nicht null oder leer ist, wird er unveraendert
     * zurueckgegeben. Ansonsten wird eine
     * {@link javax.validation.ValidationException} geworfen.
     *
     * @param value Wert, der validiert werden soll
     * @return Wert selber, wenn er nicht null ist
     */
    @Override
    public String validate(String value) {
        if (value == null) {
            throw new NullValueException();
        }
        if (value.isEmpty()) {
            throw new EmptyValueException();
        }
        return value;
    }

}
