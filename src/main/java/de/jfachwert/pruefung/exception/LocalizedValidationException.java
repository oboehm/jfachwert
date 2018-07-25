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
package de.jfachwert.pruefung.exception;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ValidationException;

/**
 * Im Gegensatz zur {@link ValidationException} wurde hier
 * {@link ValidationException#getLocalizedMessage()} ueberschrieben, um
 * eine lokalisierte Fehlermeldung zur Verfuegung stellen zu koennen.
 *
 * @author oboehm
 * @since 0.2 (15.05.2017)
 */
public class LocalizedValidationException extends ValidationException implements LocalizedException {

    /**
     * Erzeugt eine {@link LocalizedValidationException}.
     *
     * @param message Fehlermeldung
     */
    public LocalizedValidationException(String message) {
        super(message);
    }

    /**
     * Erzeugt eine {@link LocalizedValidationException}.
     *
     * @param message Fehlermeldung
     * @param cause   Ursache
     */
    public LocalizedValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Im Gegensatz {@code getMessage()} wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Loacale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    public String getLocalizedMessage() {
        String[] parts = StringUtils.split(getMessage(),":", 2);
        String localized = getLocalizedString(getMessageKey(parts[0]));
        if (parts.length > 1) {
            localized += ":" + parts[1];
        }
        return localized;
    }

}
