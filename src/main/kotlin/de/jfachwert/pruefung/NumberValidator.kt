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
 * (c)reated 30.08.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung

import de.jfachwert.SimpleValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.LocalizedArithmeticException
import org.apache.commons.lang3.Range
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*

/**
 * Der NumberValidator ueberprueft eine uebergebene [Number], ob sie
 * im erlaubten Wertebereich liegt.
 *
 * @author oboehm
 * @since 0.4 (30.08.2017)
 */
class NumberValidator @JvmOverloads
/**
 * Instanziert einen Validator, der prueft, ob ein Wert zwischen den
 * vorgegebenen Grenzen liegt.
 *
 * @param min untere Grenze
 * @param max obere Grenze
 */
constructor(min: BigDecimal = INFINITE.negate(), max: BigDecimal = INFINITE) : SimpleValidator<String> {

    private val range: Range<BigDecimal>

    /**
     * Instanziert einen Validator, der prueft, ob ein Wert zwischen den
     * vorgegebenen Grenzen liegt.
     *
     * @param min untere Grenze
     * @param max obere Grenze
     */
    constructor(min: Long, max: Long) : this(BigDecimal.valueOf(min), BigDecimal.valueOf(max)) {}

    /**
     * Als Default werden alle numerischen Werte zugelassen.
     */
    init {
        range = Range.between(min, max)
    }

    /**
     * Wenn der uebergebene Wert gueltig ist, soll er unveraendert
     * zurueckgegeben werden, damit er anschliessend von der
     * aufrufenden Methode weiterverarbeitet werden kann.
     * Ist der Wert nicht gueltig, soll eine [InvalidValueException]
     * geworfen werden.
     *
     * @param value Wert, der validiert werden soll
     * @return Wert selber, wenn er gueltig ist
     */
    override fun validate(value: String): String {
        val normalized = normalize(value)
        try {
            val n = BigDecimal(normalized)
            if (range.contains(n)) {
                return normalized
            }
        } catch (ex: NumberFormatException) {
            throw InvalidValueException(value, NUMBER, ex)
        }
        throw InvalidValueException(value, NUMBER, range)
    }

    /**
     * Normalisiert einen String, sodass er zur Generierung einer Zahl
     * herangezogen werden kann.
     *
     * @param value z.B. "1,234.5"
     * @return normalisiert, z.B. "1234.5"
     */
    fun normalize(value: String): String {
        if (!value.matches("[\\d,.]+([eE]\\d+)?".toRegex())) {
            throw InvalidValueException(value, NUMBER)
        }
        val locale = guessLocale(value)
        val df = DecimalFormat.getInstance(locale) as DecimalFormat
        df.isParseBigDecimal = true
        return try {
            df.parse(value).toString()
        } catch (ex: ParseException) {
            throw InvalidValueException(value, NUMBER, ex)
        }
    }

    /**
     * Verifiziert die uebergebene Nummer, ob sie eine gueltige Nummer und
     * nicht unendlich oder 'NaN' ist. Falls die Nummer unendlich oder 'NaN'
     * ist, wird eine [ArithmeticException] geworfen.
     *
     * @param number zu pruefende Nummer
     * @return die Nummer selbst zur Weiterverarbeitung
     */
    fun verifyNumber(number: Number): Number {
        if (number is Double || number is Float) {
            val dValue: Double = number.toDouble()
            if (java.lang.Double.isNaN(dValue) || java.lang.Double.isInfinite(dValue)) {
                throw LocalizedArithmeticException(dValue, NUMBER)
            }
        }
        return number
    }



    companion object {

        private const val NUMBER = "number"

        /** Wenn man keine obere Grenze angeben will, nimmt man diesen Wert.  */
        val INFINITE = BigDecimal.valueOf(Long.MAX_VALUE)

        private fun guessLocale(value: String): Locale {
            return if (value.matches("\\d+(\\.\\d{3})*(,\\d+)?".toRegex())) Locale.GERMAN else Locale.ENGLISH
        }

    }

}