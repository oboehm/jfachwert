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
 * (c)reated 12.10.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.NumberValidator;
import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.StringUtils;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Der GeldbetragFormatter ist fuer die Formattierung und Parsen von
 * Geldbetraegen zustaendig.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 1.0.1 (12.10.18)
 */
public class GeldbetragFormatter implements MonetaryAmountFormat {

    /**
     * The {@link AmountFormatContext} to be applied when a {@link MonetaryAmount} is formatted.
     *
     * @return the {@link AmountFormatContext} used, never {@code null}.
     */
    @Override
    public AmountFormatContext getContext() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Formats the given {@link MonetaryAmount} to a {@code Appendable}.
     * <p>
     * Example implementations of {@code Appendable} are {@code StringBuilder}, {@code StringBuffer}
     * or {@code Writer}. Note that {@code StringBuilder} and {@code StringBuffer} never throw an
     * {@code IOException}.
     *
     * @param appendable the appendable to add to, not null
     * @param amount     the amount to print, not null
     * @throws UnsupportedOperationException if the formatter is unable to print
     * @throws IOException                   if an IO error occurs, thrown by the {@code appendable}
     * @throws MonetaryParseException        if there is a problem while parsing
     */
    @Override
    public void print(Appendable appendable, MonetaryAmount amount) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Wandelt den Text in einen {@link Geldbetrag} um.
     *
     * @param text Text, z.B. "2,50 EUR"
     * @return Geldbetrag (niemals {@code null})
     * @throws MonetaryParseException falls der Text kein Geldbetrag darstellt
     */
    @Override
    public Geldbetrag parse(CharSequence text) throws MonetaryParseException {
        return parse(Objects.toString(text));
    }

    private Geldbetrag parse(String text) throws MonetaryParseException {
        String trimmed = new NullValidator<String>().validate(text).trim();
        String[] parts = StringUtils.splitByCharacterType(StringUtils.upperCase(trimmed));
        if (parts.length == 0) {
            throw new InvalidValueException(text, "money amount");
        }
        Currency cry = Waehrung.DEFAULT_CURRENCY;
        String currencyString = findCurrencyString(parts);
        if (StringUtils.isNotEmpty(currencyString)) {
            cry = Waehrung.toCurrency(currencyString);
            trimmed = StringUtils.remove(trimmed, currencyString).trim();
        }
        BigDecimal n = new BigDecimal(new NumberValidator().validate(trimmed));
        return Geldbetrag.of(n, cry);
    }

    private static String findCurrencyString(String[] parts) {
        if (!StringUtils.isNumericSpace(parts[0])) {
            return parts[0];
        }
        if (!StringUtils.isNumericSpace(parts[parts.length - 1])) {
            return parts[parts.length - 1];
        }
        return "";
    }

    /**
     * Wandelt im Wesentlichen den uebergebenen Geldbetrag in seine String-
     * Darstellung um.
     *
     * @param amount Geldbetrag
     * @return Geldbetrag als String
     */
    @Override
    public String queryFrom(MonetaryAmount amount) {
        return Objects.toString(amount);
    }

}
