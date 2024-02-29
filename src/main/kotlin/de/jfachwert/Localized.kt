/*
 * Copyright (c) 2024 by Oli B.
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
 * (c)reated 29.02.24 by oboehm
 */
package de.jfachwert

import java.text.MessageFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * In diesem Interface sind Hilfsmethoden fuer die Unterstuetzung von
 * Localization zusammengefasst. Urspruenglich war diese Funktionalitaet
 * in LocalizedException, wurde aber hier herausgezogen.
 *
 * @author oboehm
 * @since 5.4 (29.02.2024)
 */
interface Localized {

    /**
     * Liefert den lokalisierten String aus dem [ResourceBundle]. Falls
     * dieser nicht existiert wird der Schluessel fuer die Resource selbst
     * als Rueckgabewert verwendet.
     *
     * @param key Resource-Schluessel
     * @return lokalisierter String
     */
    fun getLocalizedString(key: String): String {
        return try {
            BUNDLE.getString(key)
        } catch (ex: MissingResourceException) {
            logger.log(Level.FINE, "resource for $key not found", ex)
            key
        }
    }

    /**
     * Diese Methode sollte von [.getLocalizedMessage] aufgerufen
     * werden, damit das [ResourceBundle] fuer die lokalisierte
     * Message angezogen wird.
     *
     * @param key Eintrag aus messages.properties
     * @param args die einzelnen Arugmente zum 'key'
     * @return lokalisierter String
     */
    fun getLocalizedMessage(key: String, vararg args: Any?): String {
        return MessageFormat.format(getLocalizedString(key), *args)
    }

    companion object {
        private val logger: Logger = Logger.getLogger(Localized::class.java.name)
        private val BUNDLE = ResourceBundle.getBundle("de.jfachwert.messages")
    }

}