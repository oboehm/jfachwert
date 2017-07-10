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
 * Die InvalidValueException ist eine Exception fuer ungueltige Werte.
 *
 * @author oboehm
 * @since 0.2.0 (26.04.2017)
 */
public class InvalidValueException extends LocalizedValidationException {

    private final Object value;
    private final String context;

    /**
     * Erzeugt eine neue Exception fuer einen fehlenden Wert.
     *
     * @param context Resource des fehlenden Wertes (z.B. "house_number")
     */
    public InvalidValueException(String context) {
        super("missing value for " + context.replace('_', ' '));
        this.value = null;
        this.context = context;
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param value der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     */
    public InvalidValueException(Object value, String context) {
        super("invalid value for " + context.replace('_', ' ') + ": \"" + value + '"');
        this.value = value;
        this.context = context;
    }

    /**
     * Im Gegensatz {@code getMessage()} wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Loacale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    @Override
    public String getLocalizedMessage() {
        if (value == null) {
            return getLocalizedMessage("pruefung.missingvalue.exception.message", getLocalizedString(context));
        } else {
            return getLocalizedMessage("pruefung.invalidvalue.exception.message", value, getLocalizedString(context));
        }
    }

}
