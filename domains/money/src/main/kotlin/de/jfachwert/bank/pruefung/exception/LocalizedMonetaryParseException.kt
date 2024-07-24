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
 * (c)reated 12.10.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank.pruefung.exception

import de.jfachwert.pruefung.exception.LocalizedException
import javax.money.format.MonetaryParseException

/**
 * Die LocalizedMonetaryParseException ist fuer Fehler beim Parsen von
 * Geldbetraegen gedacht.
 *
 * @author ob@aosd.de
 * @since 1.0.1 (12.10.18)
 */
open class LocalizedMonetaryParseException(parsedData: CharSequence, @get:Synchronized override val cause: Throwable) :
        MonetaryParseException(parsedData.toString(), parsedData, 0), LocalizedException {

    /**
     * Im Gegensatz `getMessage()` wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Locale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    override fun getLocalizedMessage(): String {
        return getLocalizedString(
                getLocalizedString("is_no_monetary_amount") + ": '" + input + "' (" + super.message + ")")
    }

}