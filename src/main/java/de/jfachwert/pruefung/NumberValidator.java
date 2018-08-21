/*
 * Copyright (c) 2017 by Oliver Boehm
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
package de.jfachwert.pruefung;

import de.jfachwert.*;
import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.pruefung.exception.LocalizedArithmeticException;
import org.apache.commons.lang3.*;

import javax.validation.*;
import java.math.*;

/**
 * Der NumberValidator ueberprueft eine uebergebene {@link Number}, ob sie
 * im erlaubten Wertebereich liegt.
 *
 * @author oboehm
 * @since 0.4 (30.08.2017)
 */
public class NumberValidator implements SimpleValidator<String> {

    private final Range<BigDecimal> range;

     /** Wenn man keine obere Grenze angeben will, nimmt man diesen Wert. */
    public static final BigDecimal INFINITE = BigDecimal.valueOf(Long.MAX_VALUE);

    /**
     * Als Default werden alle numerischen Werte zugelassen.
     */
    public NumberValidator() {
        this(INFINITE.negate(), INFINITE);
    }

    /**
     * Instanziert einen Validator, der prueft, ob ein Wert zwischen den
     * vorgegebenen Grenzen liegt.
     *
     * @param min untere Grenze
     * @param max obere Grenze
     */
    public NumberValidator(long min, long max) {
        this(BigDecimal.valueOf(min), BigDecimal.valueOf(max));
    }

    /**
     * Instanziert einen Validator, der prueft, ob ein Wert zwischen den
     * vorgegebenen Grenzen liegt.
     *
     * @param min untere Grenze
     * @param max obere Grenze
     */
    public NumberValidator(BigDecimal min, BigDecimal max) {
        this.range = Range.between(min, max);
    }

    /**
     * Wenn der uebergebene Wert gueltig ist, soll er unveraendert zurueckgegeben werden, damit er anschliessend von der
     * aufrufenden Methode weiterverarbeitet werden kann. Ist der Wert nicht gueltig, soll eine {@link
     * ValidationException} geworfen werden.
     *
     * @param value Wert, der validiert werden soll
     * @return Wert selber, wenn er gueltig ist
     */
    @Override
    public String validate(String value) {
        try {
            BigDecimal n = new BigDecimal(value);
            if (range.contains(n)) {
                return value;
            }
        } catch (NumberFormatException ex) {
            throw new InvalidValueException(value, "number", ex);
        }
        throw new InvalidValueException(value, "number", range);
    }

    /**
     * Verifiziert die uebergebene Nummer, ob sie eine gueltige Nummer und
     * nicht unendlich oder 'NaN' ist. Falls die Nummer unendlich oder 'NaN'
     * ist, wird eine {@link ArithmeticException} geworfen.
     *
     * @param number zu pruefende Nummer
     * @return die Nummer selbst zur Weiterverarbeitung
     */
    public Number verifyNumber(Number number) {
        if ((number instanceof Double) || (number instanceof Float)) {
            double dValue = number.doubleValue();
            if (Double.isNaN(dValue) || Double.isInfinite(dValue)) {
                throw new LocalizedArithmeticException(dValue, "number");
            }
        }
        return number;
    }

}
