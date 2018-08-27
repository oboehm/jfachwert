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
 * (c)reated 19.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * In diesem Interface sind die Gemeinsamkeiten aller XxxLocalizedException
 * zusammengefasst. Eine gemeinsame Oberklasse war leider nicht möglich, weil
 * die betroffenen Exceptions bereits von anderen Exceptions abgeleitet sind.
 *
 * @author oboehm
 * @since 1.0 (19.07.2018)
 */
public interface LocalizedException {
    
    ResourceBundle BUNDLE = ResourceBundle.getBundle("de.jfachwert.messages");

    /**
     * Dies ist eine Hilfsmethode, um aus einer Message den entsprechenden
     * Schluessel zu generieren, der dann fuer den Zugriff auf das
     * {@link ResourceBundle} verwendet wird.
     *
     * @param message Meldung der Exception (z.B. "missing value"
     * @return Meldung als Key (z.B. "missing_values")
     */
    default String getMessageKey(String message) {
        return StringUtils.replaceAll(message, " ", "_");
    }

    /**
     * Hier wird die Beschreibung im Original (meist englisch) zurueckgegeben.
     *
     * @return Original-Beschreibung
     */
    String getMessage();

    /**
     * Im Gegensatz {@code getMessage()} wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Loacale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    String getLocalizedMessage();

    /**
     * Liefert den lokalisierten String aus dem {@link ResourceBundle}. Falls
     * dieser nicht existiert wird der Schluessel fuer die Resource selbst
     * als Rueckgabewert verwendet.
     *
     * @param key Resource-Schluessel
     * @return lokalisierter String
     */
    default String getLocalizedString(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (MissingResourceException ex) {
            return key;
        }
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
    default String getLocalizedMessage(String key, Object... args) {
        return MessageFormat.format(getLocalizedString(key), args);
    }
    
}
