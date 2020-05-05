/*
 * Copyright (c) 2019, 2020 by Oliver Boehm
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
 * (c)reated 10.08.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.pruefung.exception

import org.apache.commons.lang3.StringUtils
import javax.money.UnknownCurrencyException

/**
 * Klasse LocalizedUnknownCurrencyException.
 *
 * @author oboehm
 * @since 10.08.2019
 */
class LocalizedUnknownCurrencyException : UnknownCurrencyException, ILocalizedException {

    /**
     * Kreiert eine Exception fuer eine unbekannte Waehrung.
     *
     * @param currency unbekannte Waehrung
     */
    constructor(currency: String) : super(currency) {}

    /**
     * Kreiert eine Exception fuer eine unbekannte Waehrung.
     *
     * @param currency unbekannte Waehrung
     * @param cause    Ursache fuer die Exception
     */
    constructor(currency: String, cause: Throwable) : super(currency) {
        super.initCause(cause)
    }

    /**
     * Im Gegensatz `getMessage()` wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Locale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    override fun getLocalizedMessage(): String {
        val code = StringUtils.substringAfter(super.message, ":")
        return getLocalizedString("unknown_currency_code") + ":" + code
    }

}