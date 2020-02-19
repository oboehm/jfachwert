/*
 * Copyright (c) 2017-2020 by Oliver Boehm
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
 * (c)reated 19.08.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.net

import de.jfachwert.SimpleValidator
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.NumberValidator
import java.math.BigDecimal

/**
 * Es gibt verschiedene Chat-Dienste wie ICQ, Skype oder Jabber, die hier
 * aufgelistet sind. Allerdings sind nicht alle aufgelistet sondern nur die,
 * die auch bei Xing eingetragen werden koennen.
 *
 * Die einzelnen Chat-Dienste sind alphabetisch sortiert.
 *
 * @since 0.4 (08.08.2017)
 */
enum class ChatDienst constructor(val chatName: String, val validator: SimpleValidator<String> = NullValidator<String>()) {

    /** AOL Instant Messaenger.  */
    AIM("AIM"),

    /** Google Hangout.  */
    GOOGLE_HANGOUT("Google Hangout"),

    /** Einer der aeltesten Messanger Dienste (1988).  */
    ICQ("ICQ", NumberValidator(BigDecimal.valueOf(10000), NumberValidator.INFINITE)),

    /** Jabber Instant Messanger, baut auf XMPP auf.  */
    JABBER("Jabber", EMailAdresse.Validator()),

    /** Skype.  */
    SKYPE("Skype"),

    /** Yahoo Messanger.  */
    YAHOO("Yahoo"),

    /** Sonstiger Messanger.  */
    SONSTIGER("sonstiger");

    /**
     * Wir geben den Namen statt der Konstanten aus.
     *
     * @return z.B. "Jabber"
     */
    override fun toString(): String {
        return chatName
    }



    companion object {

        /**
         * Liefert zum uebergebenen String den passenden Chat-Dienst heraus.
         *
         * @param dienst z.B. "Jabber"
         * @return #SONSTIGER, wenn kein passender Dienst gefunden wurde
         */
        @JvmStatic
        fun of(dienst: String?): ChatDienst {
            for (cd in values()) {
                if (cd.toString().equals(dienst, ignoreCase = true)) {
                    return cd
                }
            }
            return SONSTIGER
        }

        @Deprecated("Bitte of(dienst) benutzen.", ReplaceWith("of(dienst)"), level = DeprecationLevel.WARNING)
        @JvmStatic
        fun toChatDienst(dienst: String?): ChatDienst {
            return of(dienst)
        }

    }

}