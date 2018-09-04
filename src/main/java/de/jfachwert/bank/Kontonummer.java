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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 16.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.pruefung.exception.InvalidLengthException;
import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ValidationException;

/**
 * Eigentlich ist die Kontonummer Bestandteil der IBAN. Trotzdem wird sie
 * noch hauefig verwendet und ist uns daher einen eigenen Typ wert.
 *
 * @author oboehm
 * @since 0.1.0
 */
public class Kontonummer extends AbstractFachwert<Long> {

    /**
     * Hierueber wird eine neue Kontonummer angelegt.
     *
     * @param nr eine maximal 10-stellige Zahl
     */
    public Kontonummer(String nr) {
        this(Long.valueOf(verify(nr)));
    }

    /**
     * Hier gehen wir davon aus, dass eine Kontonummer immer eine Zahl ist und
     * fuehrende Nullen keine Rollen spielen.
     *
     * @param nr the nr
     */
    public Kontonummer(long nr) {
        super(verify(nr));
    }

    /**
     * Eine gueltige Kontonummer beginnt bei 1 und hat maximal 10 Stellen.
     *
     * @param kontonr die Kontonummer
     * @return die validierte Kontonummer zur Weiterverabeitung
     */
    public static String validate(String kontonr) {
        String normalized = StringUtils.trimToEmpty(kontonr);
        try {
            validate(Long.valueOf(normalized));
        } catch (NumberFormatException nfe) {
            throw new InvalidValueException(kontonr, "account_number", nfe);
        }
        return normalized;
    }
    
    private static String verify(String kontonr) {
        try {
            return validate(kontonr);
        } catch (ValidationException ex) {
            throw new LocalizedIllegalArgumentException(ex);
        }
    }

    /**
     * Eine gueltige Kontonummer beginnt bei 1 und hat maximal 10 Stellen.
     *
     * @param kontonr die Kontonummer
     * @return die validierte Kontonummer zur Weiterverabeitung
     */
    public static long validate(long kontonr) {
        if (kontonr < 1) {
            throw new InvalidValueException(kontonr, "account_number");
        }
        if (kontonr > 9_999_999_999L) {
            throw new InvalidLengthException(Long.toString(kontonr), 1, 10);
        }
        return kontonr;
    }

    private static long verify(long kontonr) {
        try {
            return validate(kontonr);
        } catch (ValidationException ex) {
            throw new LocalizedIllegalArgumentException(ex);
        }
    }

    /**
     * Um ein einheitliches Format der Kontonummer zu bekommen, geben wir
     * sie immer 10-stellig aus und fuellen sie notfalls mit fuehrenden
     * Nullen auf.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return String.format("%010d", this.getCode());
    }

}
