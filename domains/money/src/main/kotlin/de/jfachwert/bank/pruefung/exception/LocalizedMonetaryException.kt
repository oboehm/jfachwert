/*
 * Copyright (c) 2018-2022 by Oliver Boehm
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
package de.jfachwert.bank.pruefung.exception

import de.jfachwert.pruefung.exception.LocalizedException
import javax.money.MonetaryException

/**
 * Im Gegensatz zur [MonetaryException] wurde hier
 * [MonetaryException.getLocalizedMessage] ueberschrieben, um
 * eine lokalisierte Fehlermeldung zur Verfuegung stellen zu koennen.
 *
 * @author oboehm
 * @since 1.0 (19.07.2018)
 */
open class LocalizedMonetaryException : MonetaryException, LocalizedException {
    private val amounts: MutableList<Any> = ArrayList()
    private var msg: String

    /**
     * Diese Exception wird vervendet, wenn zwei Geldbetraege mit
     * unterschiedlichen Waehrungen verglichen oder addiert werden.
     *
     * @param message Meldung (z.B. "different currencies")
     * @param args die verschiedenen Geldbetraege oder Argumente
     */
    constructor(message: String, vararg args: Any?) : super(message + ": " + args) {
        amounts.addAll(listOf(args))
        msg = message
    }

    /**
     * Diese Exception wird verwendet, wenn ein verwendeter Operator mit
     * einer Exception fehlschlaegt.
     *
     * @param message Meldung (z.B. "operator failed")
     * @param arg z.B. der Operator
     * @param cause die Ursache fuer die Exception
     */
    constructor(message: String, arg: Any, cause: Throwable?) : super(message, cause) {
        amounts.add(arg)
        msg = message
    }

    /**
     * Im Gegensatz `getMessage()` wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Locale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    override fun getLocalizedMessage(): String {
        return getLocalizedString(getMessageKey(msg)) + ": " + amounts
    }

}