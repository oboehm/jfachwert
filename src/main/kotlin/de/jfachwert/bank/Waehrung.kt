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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import de.jfachwert.Fachwert;
import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.pruefung.exception.LocalizedUnknownCurrencyException;
import org.apache.commons.collections4.map.ReferenceMap;

import javax.money.CurrencyContext;
import javax.money.CurrencyUnit;
import javax.money.UnknownCurrencyException;
import javax.validation.ValidationException;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class Waehrung.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 1.0
 */
@JsonSerialize(using = ToStringSerializer.class)
public class Waehrung implements Fachwert, Comparable<CurrencyUnit>, CurrencyUnit {

    private static final Logger LOG = Logger.getLogger(Waehrung.class.getName());
    private static final Map<String, Waehrung> CACHE = new ReferenceMap<>();
    private static final SimpleValidator<String> VALIDATOR = new Validator();

    /** Default-Waehrung, die durch die Landeseinstellung (Locale) vorgegeben wird. */
    public static final Currency DEFAULT_CURRENCY = getDefaultCurrency();

    /** Default-Waehrung, die durch die Landeseinstellung (Locale) vorgegeben wird. */
    public static final Waehrung DEFAULT = new Waehrung(DEFAULT_CURRENCY);

    static {
        CACHE.put(DEFAULT_CURRENCY.getCurrencyCode(), DEFAULT);
    }

    /** Die Euro-Waehrung als Konstante. */
    public static final Waehrung EUR = Waehrung.of("EUR");

    /** Null-Konstante fuer Initialiserung. */
    public static final Waehrung NULL = new Waehrung("XXX");

    private final Currency code;

    /**
     * Darueber kann eine Waehrung angelegt werden.
     *
     * @param code z.B. "EUR"
     */
    public Waehrung(String code) {
        this(toCurrency(code));
    }

    /**
     * Darueber kann eine Waehrung angelegt werden.
     *
     * @param code Waehrung
     */
    public Waehrung(Currency code) {
        this(code, new NullValidator<>());
    }

    protected Waehrung(Currency code, SimpleValidator<Currency> validator) {
        this.code = validator.verify(code);
    }

    /**
     * Liefert die Waehrung als Currency zurueck.
     *
     * @return Waehrung als Currency
     */
    public Currency getCode() {
        return code;
    }

    /**
     * Gibt die entsprechende Currency als Waehrung zurueck. Da die Anzahl der
     * Waehrungen ueberschaubar ist, werden sie in einem dauerhaften Cache
     * vorgehalten.
     *
     * @param currency Currency
     * @return Waehrung
     */
    public static Waehrung of(Currency currency) {
        String key = currency.getCurrencyCode();
        return CACHE.computeIfAbsent(key, t -> new Waehrung(currency));
    }

    /**
     * Gibt die entsprechende Currency als Waehrung zurueck.
     *
     * @param currencyUnit CurrencyUnit
     * @return Waehrung
     */
    public static Waehrung of(CurrencyUnit currencyUnit) {
        if (currencyUnit instanceof Waehrung) {
            return (Waehrung) currencyUnit;
        } else {
            return of(currencyUnit.getCurrencyCode());
        }
    }

    /**
     * Gibt die entsprechende Currency als Waehrung zurueck.
     *
     * @param currency Waehrung, z.B. "EUR"
     * @return Waehrung
     */
    public static Waehrung of(String currency) {
        return of(toCurrency(currency));
    }

    /**
     * Ermittelt aus dem uebergebenen String die entsprechende
     * {@link Currency}.
     *
     * @param name z.B. "EUR" oder auch ein einzelnes Symbol
     * @return die entsprechende Waehrung
     */
    public static Currency toCurrency(String name) {
        try {
            return Currency.getInstance(name);
        } catch (IllegalArgumentException iae) {
            if (name.length() <= 3) {
                for (Currency c : Currency.getAvailableCurrencies()) {
                    if (matchesCurrency(name, c)) {
                        return c;
                    }
                }
                return toFallbackCurrency(name, iae);
            } else {
                try {
                    return toCurrency(name.substring(0, 3));
                } catch (LocalizedUnknownCurrencyException ex) {
                    throw new LocalizedUnknownCurrencyException(name, ex);
                }
            }
        }
    }

    private static boolean matchesCurrency(String name, Currency c) {
        return name.equalsIgnoreCase(c.getCurrencyCode()) || name.equalsIgnoreCase(c.getSymbol());
    }

    private static Currency toFallbackCurrency(String name, IllegalArgumentException iae) {
        if (name.equals("\u20ac")) {
            return Currency.getInstance("EUR");
        } else {
            throw new LocalizedUnknownCurrencyException(name);
        }
    }

    /**
     * Validiert den uebergebenen Waehrungscode.
     *
     * @param code Waehrungscode als String
     * @return Waehrungscode zur Weiterverarbeitung
     */
    public static String validate(String code) {
        return VALIDATOR.validate(code);
    }

    /**
     * Liefert die Currency zurueck.
     *
     * @return die Currency aus java.util.
     */
    public Currency getCurrency() {
        return getCode();
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
     * Liefert das Waehrungssymbol.
     *
     * @return z.B. "$"
     */
    public String getSymbol() {
        return getCode().getSymbol();
    }

    /**
     * Lieft das Waehrungssymbol der uebergebenen Waehrungseinheit.
     *
     * @param cu Waehrungseinheit
     * @return z.B. das Euro-Zeichen
     */
    public static String getSymbol(CurrencyUnit cu) {
        try {
            return Waehrung.of(cu).getSymbol();
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.WARNING, "Cannot get symbol for '" + cu + "':", ex);
            return cu.getCurrencyCode();
        }
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
     * Zwei Waehrungen sind nur dann gleich, wenn sie vom gleichen Typ sind .
     *
     * @param obj zu vergleichender Waehrung
     * @return true bei Gleichheit
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Waehrung)) {
            return false;
        }
        Waehrung other = (Waehrung) obj;
        return this.getCode().equals(other.getCode());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getCode().hashCode();
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

    /**
     * Ermittelt die Waehrung. Urspruenglich wurde die Default-Currency ueber
     * <pre>
     *     Currency.getInstance(Locale.getDefault())
     * </pre>
     * ermittelt. Dies fuehrte aber auf der Sun zu Problemen, da dort
     * die Currency fuer die Default-Locale folgende Exception hervorrief:
     * <pre>
     * java.lang.IllegalArgumentException
     *     at java.util.Currency.getInstance(Currency.java:384)
     *     at de.jfachwert.bank.Geldbetrag.&lt;clinit&gt;
     *     ...
     * </pre>
     *
     * @return normalerweise die deutsche Currency
     */
    private static Currency getDefaultCurrency() {
        Locale[] locales = {Locale.getDefault(), Locale.GERMANY, Locale.GERMAN};
        for (Locale loc : locales) {
            try {
                return Currency.getInstance(loc);
            } catch (IllegalArgumentException iae) {
                LOG.log(Level.INFO,
                        "No currency for locale '" + loc + "' available on this machine - will try next one.", iae);
            }
        }
        return Currency.getAvailableCurrencies().iterator().next();
    }


    /**
     * Dieser Validator ist fuer die Ueberpruefung von Waehrungen vorgesehen.
     *
     * @since 3.0
     */
    public static class Validator implements SimpleValidator<String> {

        /**
         * Wenn der uebergebene Waehrungsstring gueltig ist, wird er
         * unveraendert zurueckgegeben, damit er anschliessend von der
         * aufrufenden Methode weiterverarbeitet werden kann. Ist der Wert
         * nicht gueltig, wird eine {@link ValidationException} geworfen.
         *
         * @param code Waehrungs-String, der validiert wird
         * @return Wert selber, wenn er gueltig ist
         */
        @Override
        public String validate(String code) {
            try {
                toCurrency(code);
            } catch (IllegalArgumentException | UnknownCurrencyException ex) {
                throw new InvalidValueException(code, "currency");
            }
            return code;
        }

    }

}