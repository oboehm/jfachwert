/*
 * Copyright (c) 2020 by Oliver Boehm
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
 * (c)reated 15.03.2020 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception

import org.apache.commons.lang3.RegExUtils
import java.text.MessageFormat
import java.util.*

/**
 * In diesem Interface sind die Gemeinsamkeiten aller XxxLocalizedException
 * zusammengefasst. Eine gemeinsame Oberklasse war leider nicht möglich, weil
 * die betroffenen Exceptions bereits von anderen Exceptions abgeleitet sind.
 *
 * Diese Kotlin-Interface ist nahezu identisch mit dem alten Java-Interface.
 * Wegen https://youtrack.jetbrains.com/issue/KT-6653 gab es Probleme mit
 * der getMessage(String)-Methode. Deswegen wurde diese Methode fuer Kotlin
 * in #getMessageKey umbenannt.
 *
 * ist es momentan leider
 * nicht moeglich, fuer Kotlin- und Java-Implementierungen das gleiche
 * Interface zu verwenden.
 *
 * @author oboehm
 * @since 4.0 (15.03.2020)
 */
interface LocalizedException {

    /**
     * Dies ist eine Hilfsmethode, um aus einer Message den entsprechenden
     * Schluessel zu generieren, der dann fuer den Zugriff auf das
     * [ResourceBundle] verwendet wird.
     *
     * @param message Meldung der Exception (z.B. "missing value"
     * @return Meldung als Key (z.B. "missing_values")
     */
    fun getMessageKey(message: String): String {
        return RegExUtils.replaceAll(message, " ", "_")
    }

    /**
     * Hier wird die Beschreibung im Original (meist englisch) zurueckgegeben.
     *
     * @return Original-Beschreibung
     */
    val message: String?

    /**
     * Im Gegensatz `getMessage()` wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Loacale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    fun getLocalizedMessage(): String

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
        @JvmField
        val BUNDLE = ResourceBundle.getBundle("de.jfachwert.messages")
    }
}