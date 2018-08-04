/*
 * Copyright (c) 2018 by Oliver Boehm
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
 * (c)reated 04.08.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.pruefung.exception.InvalidValueException;

import javax.money.CurrencyContext;
import javax.money.CurrencyUnit;
import java.util.Currency;

/**
 * Class Waehrung.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.8
 */
public class Waehrung extends AbstractFachwert<Currency> implements CurrencyUnit {

    /**
     * Darueber kann eine Waehrung angelegt werden.
     *
     * @param code z.B. "EUR"
     */
    public Waehrung(String code) {
        this(Currency.getInstance(validate(code)));
    }

    /**
     * Darueber kann eine Waehrung angelegt werden.
     *
     * @param code Waehrung
     */
    public Waehrung(Currency code) {
        super(code);
    }

    /**
     * Validiert den uebergebenen Waehrungscode.
     *
     * @param code Waehrungscode als String
     * @return Waehrungscode zur Weiterverarbeitung
     */
    public static String validate(String code) {
        try {
            Currency.getInstance(code);
        } catch (IllegalArgumentException ex) {
            throw new InvalidValueException(code, "currency");
        }
        return code;
    }

    /**
     * Liefert den Waehrungscode.
     *
     * @return z.B. "EUR"
     */
    @Override
    public String getCurrencyCode() {
        return getCode().getCurrencyCode();
    }

    /**
     * Liefert den numerischen Waehrungscode.
     *
     * @return z.B. 978 fuer EUro
     */
    @Override
    public int getNumericCode() {
        return getCode().getNumericCode();
    }

    /**
     * Liefert die Anzahl der Nachkommastellen einer Waehrung.
     *
     * @return meist 2, manchmal 0
     */
    @Override
    public int getDefaultFractionDigits() {
        return getCode().getDefaultFractionDigits();
    }

    @Override
    public CurrencyContext getContext() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Zum Vergleich wird der Waehrungscode herangezogen und alphabetisch
     * verglichen.
     *
     * @param other die andere Waerhung
     * @return eine negative Zahl wenn die ander Waehrung alphabetisch
     * danach kommt.
     */
    @Override
    public int compareTo(CurrencyUnit other) {
        return getCurrencyCode().compareTo(other.getCurrencyCode());
    }

    /**
     * Als toString-Implementierung wird der Waehrungscode ausgegeben.
     *
     * @return z.B. "EUR"
     */
    @Override
    public String toString() {
        return getCurrencyCode();
    }

}
