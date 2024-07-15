/*
 * Copyright (c) 2017-2022 by Oliver Boehm
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
import java.util.*

/**
 * Die InvalidValueException ist eine Exception fuer ungueltige Werte.
 *
 * @author oboehm
 * @since 0.2.0 (26.04.2017)
 */
open class LocalizedIllegalArgumentException(message: String?, private val valueException: Throwable) : IllegalArgumentException(message, valueException), LocalizedException {

    /**
     * Erzeugt eine [LocalizedIllegalArgumentException].
     *
     * @param message Fehlermeldung
     */
    constructor(message: String) : this(null, message) {}

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert, der nicht
     * zwischen den angegebenen Werten liegt.
     *
     * @param value   der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     * @param range   untere und obere Schranke
     */
    constructor(value: Serializable?, context: String, range: Range<out Comparable<*>?>?) : this(value, context, InvalidValueException(value!!, context, range!!)) {}

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param value der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     * @param cause Ursache
     */
    @JvmOverloads
    constructor(value: Serializable?, context: String, cause: Throwable = InvalidValueException(value!!, context)) : this("illegal value for " + context.replace('_', ' ') + ": \"" + value + '"', cause) {
    }

    /**
     * Dieser Constructor kann bei Arrays mit falscher Groesse eingesetzt
     * werden.
     *
     * @param array fehlerhaftes Array
     * @param expected erwartete Array-Groesse
     */
    constructor(array: ByteArray, expected: Int) : this("array=" + Arrays.toString(array) + " has not length " + expected + " (but " + array.size + ")",
            InvalidLengthException(array, expected)) {
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param cause eigentliche Ursache
     */
    constructor(cause: Throwable) : this(cause.message, cause) {}

    /**
     * Erzeugt eine lokalisiserte Fehlermeldung fuer diese Exception.
     *
     * @return lokalisierte Fehlermeldung
     */
    override fun getLocalizedMessage(): String {
        return valueException.localizedMessage
    }

}