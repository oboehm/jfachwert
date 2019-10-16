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
package de.jfachwert.bank.internal;

import de.jfachwert.bank.Geldbetrag;
import de.jfachwert.bank.Waehrung;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.NumberValidator;
import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.pruefung.exception.LocalizedMonetaryParseException;
import org.apache.commons.lang3.StringUtils;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/**
 * Der GeldbetragFormatter ist fuer die Formattierung und Parsen von
 * Geldbetraegen zustaendig.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 1.0.1 (12.10.18)
 */
public class GeldbetragFormatter implements MonetaryAmountFormat {

    private final AmountFormatContext context;
    private static Map<Locale, Locale> MAPPED_LOCALES = new HashMap<>();

    static {
        MAPPED_LOCALES.put(new Locale("de_DE"), Locale.GERMANY);
    }

    public GeldbetragFormatter() {
        this(Locale.getDefault());
    }

    private GeldbetragFormatter(Locale locale) {
        this(AmountFormatContextBuilder.of("jfachwert").setLocale(locale).build());
    }

    private GeldbetragFormatter(AmountFormatContext context) {
        this.context = context;
    }

    public static GeldbetragFormatter of(Locale locale) {
        Locale mapped = MAPPED_LOCALES.getOrDefault(locale, locale);
        return new GeldbetragFormatter(AmountFormatContextBuilder.of("jfachwert").setLocale(mapped).build());
    }

    public static GeldbetragFormatter of(AmountFormatContext context) {
        return new GeldbetragFormatter(context);
    }

    /**
     * Der {@link AmountFormatContext}, der normalerweise fuer die
     * Formattierung verwendet wird. Wird aber von dieser Klasse
     * (noch) nicht intern verwendet.
     *
     * @return Context
     */
    @Override
    public AmountFormatContext getContext() {
        return context;
    }

    /**
     * Gibt lediglich den Geldbetrag aus.
     * <p>
     * Beispiele fuer {@code Appendable} sind {@code StringBuilder}, {@code StringBuffer}
     * oder {@code Writer}, wobei nur letzteres eine {@code IOException} verursachen kann.
     *
     * @param appendable z.B. einen StringBuilder
     * @param amount     Geldbetrag
     * @throws IOException kann bei einem {@code Writer} auftreten
     */
    @Override
    public void print(Appendable appendable, MonetaryAmount amount) throws IOException {
        CurrencyUnit currency = amount.getCurrency();
        int fractionDigits = currency.getDefaultFractionDigits();
        synchronized (this.context) {
            NumberFormat formatter = getFormatter(this.context.getLocale());
            formatter.setMinimumFractionDigits(fractionDigits);
            formatter.setMaximumFractionDigits(fractionDigits);
            String s = formatter.format(amount.getNumber()) + " " + currency;
            appendable.append(s);
        }
    }

    private NumberFormat getFormatter(Locale locale) {
        NumberFormat formatter =  NumberFormat.getInstance(locale);
        String s = formatter.format(0);
        if (!"0".equals(s)) {
            formatter = NumberFormat.getInstance();
        }
        return formatter;
    }

    /**
     * Wandelt den Text in einen {@link Geldbetrag} um.
     *
     * @param text Text, z.B. "2,50 EUR"
     * @return Geldbetrag (niemals {@code null})
     * @throws MonetaryParseException falls der Text kein Geldbetrag darstellt
     */
    @Override
    public MonetaryAmount parse(CharSequence text) throws MonetaryParseException {
        return parse(Objects.toString(text));
    }

    private MonetaryAmount parse(String text) throws MonetaryParseException {
        String trimmed = new NullValidator<String>().validate(text).trim();
        String[] parts = StringUtils.splitByCharacterType(StringUtils.upperCase(trimmed));
        if (parts.length == 0) {
            throw new InvalidValueException(text, "money amount");
        }
        Currency cry = Waehrung.DEFAULT_CURRENCY;
        String currencyString = findCurrencyString(parts);
        try {
            trimmed = StringUtils.remove(trimmed, currencyString).trim();
            BigDecimal n = new BigDecimal(new NumberValidator().validate(trimmed));
            if (StringUtils.isNotEmpty(currencyString)) {
                cry = Waehrung.toCurrency(currencyString);
            }
            return getMonetaryAmount(cry, n);
        } catch (IllegalArgumentException | ValidationException ex) {
            throw new LocalizedMonetaryParseException(text, ex);
        }
    }

    private MonetaryAmount getMonetaryAmount(Currency cry, BigDecimal n) {
        MonetaryAmountFactory amountFactory = this.context.get(MonetaryAmountFactory.class);
        if (amountFactory == null) {
            return Geldbetrag.of(n, cry);
        } else {
            return amountFactory.setNumber(n).setCurrency(cry.toString()).create();
        }
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + context.getLocale() + ")";
    }

}