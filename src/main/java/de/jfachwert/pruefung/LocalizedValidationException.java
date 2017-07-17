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
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Im Gegensatz zur {@link ValidationException} wurde hier
 * {@link ValidationException#getLocalizedMessage()} ueberschrieben, um
 * eine lokalisierte Fehlermeldung zur Verfuegung stellen zu koennen.
 *
 * @author oboehm
 * @since 0.2 (15.05.2017)
 */
public abstract class LocalizedValidationException extends ValidationException {

    private transient ResourceBundle bundle;

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
     * Diese Methode sollte von {@link #getLocalizedMessage()} aufgerufen
     * werden, damit das {@link ResourceBundle} fuer die lokalisierte
     * Message angezogen wird.
     *
     * @param key Eintrag aus messages.properties
     * @param args die einzelnen Arugmente zum 'key'
     * @return lokalisierter String
     */
    protected String getLocalizedMessage(String key, Object... args) {
        return MessageFormat.format(getLocalizedString(key), args);
    }

    /**
     * Liefert den lokalisierten String aus dem {@link ResourceBundle}. Falls
     * dieser nicht existiert wird der Schluessel fuer die Resource selbst
     * als Rueckgabewert verwendet.
     *
     * @param key Resource-Schluessel
     * @return lokalisierter String
     */
    protected String getLocalizedString(String key) {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("de.jfachwert.messages");
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            return key;
        }
    }

}
