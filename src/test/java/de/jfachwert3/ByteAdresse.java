/*
 * Copyright (c) 2021 by Oli B.
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
 * (c)reated 06.01.2021 by Oli B. (ob@aosd.de)
 */

package de.jfachwert3;

import de.jfachwert.Fachwert;
import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.Range;

/**
 * Mit der Klasse ByteAdresse wird ein Feld innherhalb eines Teildatensatzes
 * adressiert. Es geht von Adresse 1 bis 256.
 * <p>
 * Dieses Beispiel stammt aus gdv.xport (https://github.com/oboehm/gdv.xport).
 * </p>
 */
public class ByteAdresse extends Number implements Fachwert {

    private static final Validator VALIDATOR = new Validator();
    final byte adresse;

    private ByteAdresse(int adresse) {
        this.adresse = (byte) (VALIDATOR.verify(adresse) - 129);
    }

    public static ByteAdresse of(int n) {
        return new ByteAdresse(n);
    }

    @Override
    public int intValue() {
        return 129 + ((int) adresse);
    }

    @Override
    public long longValue() {
        return intValue();
    }

    @Override
    public float floatValue() {
        return intValue();
    }

    @Override
    public double doubleValue() {
        return intValue();
    }

    @Override
    public String toString() {
        return Integer.toString(intValue());
    }



    public static class Validator implements SimpleValidator<Integer> {

        /**
         * Eine gueltige Byte-Adresse liegt zwischen 1 und 256.
         *
         * @param n Adresse, die validiert wird
         * @return Zahl selber, wenn sie gueltig ist
         */
        @Override
        public Integer validate(Integer n) {
            if ((n < 1) || (n > 256)) {
                throw new InvalidValueException(n, "Adresse", Range.between(1, 256));
            }
            return n;
        }

    }

}
