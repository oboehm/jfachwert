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

import javax.validation.ValidationException;

/**
 * Die MissingValueException ist eine Exception fuer fehlende Werte.
 *
 * @author oboehm
 * @since 0.2.0 (03.05.2017)
 */
public class MissingValueException extends ValidationException {

    private final Object value;
    private final String context;

    /**
     * Erzeugt eine neue Exception
     *
     * @param value Objekt mit fehlendem Wert
     * @param context was fuer ein fehlerhafter Wert
     */
    public MissingValueException(Object value, String context) {
        super("invalid value for " + context + ": '" + value + "'");
        this.value = value;
        this.context = context;
    }

}
