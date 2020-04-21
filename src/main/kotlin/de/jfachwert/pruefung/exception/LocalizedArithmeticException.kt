/*
 * Copyright (c) 2018 by Oliver Boehm
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
 * (c)reated 20.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception;

import java.io.Serializable;

/**
 * Die LocalizedArithmeticException ist eine Unterklasse der
 * {@link ArithmeticException} mit lokalisierter Fehlermeldung.
 *
 * @author oboehm
 * @since 1.0 (20.08.2018)
 */
public class LocalizedArithmeticException extends ArithmeticException implements LocalizedException {
    
    private final InvalidValueException valueException;

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param value der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     */
    public LocalizedArithmeticException(Serializable value, String context) {
        super("invalid value for " + context.replace('_', ' ') + ": \"" + value + '"');
        this.valueException = new InvalidValueException(value, context);
    }

    /**
     * Erzeugt eine lokalisiserte Fehlermeldung fuer diese Exception.
     *
     * @return lokalisierte Fehlermeldung
     */
    @Override
    public String getLocalizedMessage() {
        return valueException.getLocalizedMessage();
    }

}
