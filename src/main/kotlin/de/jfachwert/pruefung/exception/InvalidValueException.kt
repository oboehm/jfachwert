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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception

import org.apache.commons.lang3.Range
import java.io.Serializable

/**
 * Die InvalidValueException ist eine Exception fuer ungueltige Werte.
 *
 * @author oboehm
 * @since 0.2.0 (26.04.2017)
 */
class InvalidValueException : LocalizedValidationException {
    private val value: Serializable?
    private val context: String
    private val range: Range<out Comparable<*>?>?

    /**
     * Erzeugt eine neue Exception fuer einen fehlenden Wert.
     *
     * @param context Resource des fehlenden Wertes (z.B. "house_number")
     */
    constructor(context: String) : super("missing value for " + context.replace('_', ' ')) {
        value = null
        this.context = context
        range = null
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param value der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     */
    constructor(value: Serializable, context: String) : super("invalid value for " + context.replace('_', ' ') + ": \"" + value + '"') {
        this.value = value
        this.context = context
        range = null
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param value   der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     * @param cause   Ursache
     */
    constructor(value: Serializable, context: String, cause: Throwable?) : super("invalid value for " + context.replace('_', ' ') + ": \"" + value + '"', cause) {
        this.value = value
        this.context = context
        range = null
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert, der nicht
     * zwischen den angegebenen Werten liegt.
     *
     * @param value   der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     * @param range   untere und obere Schranke
     */
    constructor(value: Serializable, context: String, range: Range<out Comparable<*>?>) : super("value for " + context.replace('_', ' ') + " is not in " + range + ": \"" + value + '"') {
        this.value = value
        this.context = context
        this.range = range
    }

    /**
     * Im Gegensatz `getMessage()` wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Locale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    override fun getLocalizedMessage(): String {
        val localizedContext = getLocalizedString(context)
        if (value == null) {
            return getLocalizedMessage("pruefung.missingvalue.exception.message", localizedContext)
        }
        return if (range == null) {
            getLocalizedMessage("pruefung.invalidvalue.exception.message", value.toString(), localizedContext)
        } else {
            getLocalizedMessage("pruefung.invalidrange.exception.message", value.toString(), localizedContext, range)
        }
    }

}