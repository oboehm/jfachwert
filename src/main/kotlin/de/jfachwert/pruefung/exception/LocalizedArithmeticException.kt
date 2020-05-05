/*
 * Copyright (c) 2018-2020 by Oliver Boehm
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
 * (c)reated 20.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception

import java.io.Serializable

/**
 * Die LocalizedArithmeticException ist eine Unterklasse der
 * [ArithmeticException] mit lokalisierter Fehlermeldung.
 *
 * @author oboehm
 * @since 1.0 (20.08.2018)
 */
class LocalizedArithmeticException(value: Serializable, context: String) : ArithmeticException("invalid value for " + context.replace('_', ' ') + ": \"" + value + '"'), LocalizedException {
    private val valueException: InvalidValueException

    /**
     * Erzeugt eine lokalisiserte Fehlermeldung fuer diese Exception.
     *
     * @return lokalisierte Fehlermeldung
     */
    override fun getLocalizedMessage(): String {
        return valueException.localizedMessage
    }

    init {
        valueException = InvalidValueException(value, context)
    }

}