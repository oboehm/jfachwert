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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 18.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.NumberValidator;
import de.jfachwert.pruefung.exception.*;
import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.spi.DefaultNumberValue;

import javax.money.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Objects;

/**
 * Diese Klasse unterstuetzt sie JSR 354 und das{@link MonetaryAmount} 
 * Interface, das Bestandteil von Java 9 ist. Da in alten Anwendungen
 * oftmals ein {@link BigDecimal} verwendet wurde, wird auch diese
 * Schnittstelle weitgehende unterstützt. Einzige Unterschied ist
 * die {@link MonetaryAmount#stripTrailingZeros()}-Methode, die einen anderen
 * Rueckgabewert hat. Deswegen ist dies Klasse auch nicht von
 * {@link BigDecimal} abgeleitet...
 * <p>
 * Im Gegensatz zur {@link org.javamoney.moneta.Money}- und 
 * {@link org.javamoney.moneta.FastMoney}-Klasse kann diese Klasse
 * ueberschrieben werden, falls anderes Rundungsverhalten oder
 * eine angepasste Implementierung benoetigt wird.
 * </p>
 *
 * @author oboehm
 * @since 1.0 (18.07.2018)
 */
@JsonSerialize(using = ToStringSerializer.class)
public class Geldbetrag implements MonetaryAmount, Comparable<MonetaryAmount>, Fachwert {
    
    private static final GeldbetragFactory FACTORY = new GeldbetragFactory();
    private static final NumberValidator NUMBER_VALIDATOR = new NumberValidator();

    /** Da 0-Betraege relativ haeufig vorkommen, spendieren wir dafuer eine eigene Konstante. */
    public static final Geldbetrag ZERO = new Geldbetrag(BigDecimal.ZERO);

    /** Der minimale Betrag, den wir unterstuetzen. */
    public static final Geldbetrag MIN_VALUE = new Geldbetrag(BigDecimal.valueOf(Long.MIN_VALUE));

    /** Der maximale Betrag, den wir unterstuetzen. */
    public static final Geldbetrag MAX_VALUE = new Geldbetrag(BigDecimal.valueOf(Long.MAX_VALUE));

    private final BigDecimal betrag;
    private final CurrencyUnit currency;
    private final MonetaryContext context;

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1
     */
    public Geldbetrag(long betrag) {
        this(BigDecimal.valueOf(betrag));
    }

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1.00
     */
    public Geldbetrag(double betrag) {
        this(BigDecimal.valueOf(betrag));
    }

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. "1"
     */
    public Geldbetrag(String betrag) {
        this(Geldbetrag.valueOf(betrag));
    }

    /**
     * Dies ist zum einen der CopyConstructor als Ersatz fuer eine
     * clone-Methode, zum anderen wandelt es einen {@link MonetaryAmount}
     * in ein GeldBetrag-Objekt.
     * 
     * @param other der andere Geldbetrag
     */
    public Geldbetrag(MonetaryAmount other) {
        this(other.getNumber(), Currency.getInstance(other.getCurrency().getCurrencyCode()));
    }

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1.00
     */
    public Geldbetrag(Number betrag) {
        this(betrag, Waehrung.DEFAULT_CURRENCY);
    }

    /**
     * Erzeugt einen Geldbetrag in der angegebenen Waehrung.
     *
     * @param betrag   Geldbetrag, z.B. 1.00
     * @param currency Waehrung, z.B. Euro
     */
    public Geldbetrag(Number betrag, Currency currency) {
        this(betrag, Waehrung.of(currency));
    }

    /**
     * Erzeugt einen Geldbetrag in der angegebenen Waehrung.
     *
     * @param betrag   Geldbetrag, z.B. 1.00
     * @param currency Waehrung, z.B. Euro
     */
    public Geldbetrag(Number betrag, CurrencyUnit currency) {
        this(betrag, currency, FACTORY.getMonetaryContextOf(betrag));
    }

    /**
     * Erzeugt einen Geldbetrag in der angegebenen Waehrung.
     *
     * @param betrag   Geldbetrag, z.B. 1.00
     * @param currency Waehrung, z.B. Euro
     * @param context  den Kontext mit Rundungs- und anderen Informationen
     */
    public Geldbetrag(Number betrag, CurrencyUnit currency, MonetaryContext context) {
        this.betrag = validate(toBigDecimal(betrag, context), currency);
        this.currency = currency;
        this.context = context;
    }

    /**
     * Hierueber kann eine Geldbetrag ueber die Anzahl an Cents angelegt
     * werden.
     *
     * @param cents Cent-Betrag, z.B. 42
     * @return Geldbetrag, z.B. 0.42$
     */
    public static Geldbetrag fromCent(long cents) {
        return Geldbetrag.valueOf(cents).divide(100);
    }

    /**
     * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
     * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
     * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
     * <p>
     * In Anlehnung an {@link BigDecimal} heisst die Methode "valueOf" und
     * nicht "of".
     * </p>
     *
     * @param other the other
     * @return ein Geldbetrag
     */
    public static Geldbetrag valueOf(String other) {
        String trimmed = new NullValidator<String>().validate(other).trim();
        String[] parts = StringUtils.splitByCharacterType(StringUtils.upperCase(trimmed));
        if (parts.length == 0) {
            throw new InvalidValueException(other, "money amount");
        }
        Currency cry = Waehrung.DEFAULT_CURRENCY;
        String waehrung = parts[parts.length - 1];
        if (!StringUtils.isNumericSpace(waehrung)) {
            cry = Waehrung.toCurrency(waehrung);
            trimmed = trimmed.substring(0, trimmed.length() - waehrung.length()).trim();
        }
        BigDecimal n = new BigDecimal(new NumberValidator().validate(trimmed));
        return valueOf(n, cry);
    }

    /**
     * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
     * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
     * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
     * <p>
     * In Anlehnung an {@link BigDecimal} heisst die Methode "valueOf" und
     * nicht "of".
     * </p>
     *
     * @param value Wert des andere Geldbetrags
     * @return ein Geldbetrag
     */
    public static Geldbetrag valueOf(long value) {
        return valueOf(new Geldbetrag(value));
    }

    /**
     * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
     * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
     * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
     * <p>
     * In Anlehnung an {@link BigDecimal} heisst die Methode "valueOf" und
     * nicht "of".
     * </p>
     *
     * @param value Wert des andere Geldbetrags
     * @return ein Geldbetrag
     */
    public static Geldbetrag valueOf(double value) {
        return valueOf(new Geldbetrag(value));
    }

    /**
     * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
     * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
     * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
     * <p>
     * In Anlehnung an {@link BigDecimal} heisst die Methode "valueOf" und
     * nicht "of".
     * </p>
     *
     * @param value Wert des andere Geldbetrags
     * @param currency Waehrung des anderen Geldbetrags
     * @return ein Geldbetrag
     */
    public static Geldbetrag valueOf(Number value, Currency currency) {
        return valueOf(new Geldbetrag(value, currency));
    }

    /**
     * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
     * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
     * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
     * <p>
     * In Anlehnung an {@link BigDecimal} heisst die Methode "valueOf" und
     * nicht "of".
     * </p>
     *
     * @param value Wert des andere Geldbetrags
     * @param currency Waehrung des anderen Geldbetrags
     * @return ein Geldbetrag
     */
    public static Geldbetrag valueOf(Number value, CurrencyUnit currency) {
        return valueOf(new Geldbetrag(value, currency));
    }

    /**
     * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
     * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
     * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
     * <p>
     * In Anlehnung an {@link BigDecimal} heisst die Methode "valueOf" und
     * nicht "of".
     * </p>
     *
     * @param value Wert des andere Geldbetrags
     * @param currency Waehrung des anderen Geldbetrags
     * @param monetaryContext Kontext des anderen Geldbetrags
     * @return ein Geldbetrag
     */
    public static Geldbetrag valueOf(Number value, CurrencyUnit currency, MonetaryContext monetaryContext) {
        return valueOf(new Geldbetrag(value, currency, monetaryContext));
    }

    /**
     * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
     * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
     * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
     * <p>
     * In Anlehnung an {@link BigDecimal} heisst die Methode "valueOf" und
     * nicht "of".
     * </p>
     *
     * @param other the other
     * @return ein Geldbetrag
     */
    public static Geldbetrag valueOf(MonetaryAmount other) {
        if (other instanceof Geldbetrag) {
            return (Geldbetrag) other;
        }
        BigDecimal value = other.getNumber().numberValue(BigDecimal.class);
        if (value.equals(BigDecimal.ZERO)) {
            return Geldbetrag.ZERO;
        }
        return new Geldbetrag(value).withCurrency(other.getCurrency());
    }

    /**
     * Validiert die uebergebene Zahl, ob sie sich als Geldbetrag eignet.
     *
     * @param zahl als String
     * @return die Zahl zur Weitervarabeitung
     */
    public static String validate(String zahl) {
        try {
            return Geldbetrag.valueOf(zahl).toString();
        } catch (IllegalArgumentException ex) {
            throw new InvalidValueException(zahl, "money_amount", ex);
        }
    }

    /**
     * Validiert die uebergebene Zahl.
     *
     * @param zahl als String
     * @return die Zahl zur Weitervarabeitung
     */
    public static BigDecimal validate(BigDecimal zahl, CurrencyUnit currency) {
        if (zahl.scale() == 0) {
            return zahl.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        }
        return zahl;
    }

    /**
     * Liefert einen Geldbetrag mit der neuen gewuenschten Waehrung zurueck.
     * Dabei findet <b>keine</b> Umrechnung statt.
     * <p>
     * Anmerkung: Der Prefix "with" kommt von der Namenskonvention in Scala
     * fuer immutable Objekte.
     * </p>
     *
     * @param unit die Waehrungseinheit
     * @return Geldbetrag mit neuer Waehrung
     */
    public Geldbetrag withCurrency(CurrencyUnit unit) {
        return withCurrency(unit.getCurrencyCode());
    }

    /**
     * Liefert einen Geldbetrag mit der neuen gewuenschten Waehrung zurueck.
     * Dabei findet <b>keine</b> Umrechnung statt.
     * <p>
     * Anmerkung: Der Prefix "with" kommt von der Namenskonvention in Scala
     * fuer immutable Objekte.
     * </p>
     *
     * @param waehrung Waehrung
     * @return Geldbetrag mit neuer Waehrung
     */
    public Geldbetrag withCurrency(String waehrung) {
        String normalized = waehrung.toUpperCase().trim();
        if ("DM".equalsIgnoreCase(normalized)) {
            normalized = "DEM";
        }
        return withCurrency(Currency.getInstance(normalized));
    }

    /**
     * Liefert einen Geldbetrag mit der neuen gewuenschten Waehrung zurueck.
     * Dabei findet <b>keine</b> Umrechnung statt.
     * <p>
     * Anmerkung: Der Prefix "with" kommt von der Namenskonvention in Scala
     * fuer immutable Objekte.
     * </p>
     *
     * @param currency Waehrung
     * @return Geldbetrag mit neuer Waehrung
     */
    public Geldbetrag withCurrency(Currency currency) {
        return new Geldbetrag(this.getNumber(), currency);
    }

    /**
     * Gibt den {@link MonetaryContext} des Geldbetrags zurueck. Der
     * {@link MonetaryContext} enthaelt Informationen ueber numerische
     * Eigenschaften wie Anzahl Nachkommastellen oder Rundungsinformation.
     *
     * @return den {@link MonetaryContext} zum Geldbetrag
     */
    @Override
    public MonetaryContext getContext() {
        return context;
    }

    /**
     * Erzeugt eine neue @code GeldbetragFactory}, die @link CurrencyUnit}, den
     * numerischen Werte und den aktuellen {@link MonetaryContext} verwendet.
     *
     * @return eine {@code GeldbetragFactory}, mit dem ein neuer (gleicher)
     *         Geldbetrag erzeugt werden kann.
     */
    @Override
    public GeldbetragFactory getFactory() {
        return new GeldbetragFactory().setCurrency(currency).setNumber(betrag).setContext(context);
    }

    /**
     * Vergleicht zwei Instanzen von {@link MonetaryAmount}. Nicht signifikante
     * Nachkommastellen werden dabei ignoriert.
     *
     * @param amount Betrag eines {@code MonetaryAmount}, mit dem verglichen werid
     * @return {@code true} falls {@code amount > this}.
     * @throws MonetaryException bei unterschiedlichen Waehrungen.
     */
    @Override
    public boolean isGreaterThan(MonetaryAmount amount) {
        return this.compareTo(amount) > 0;
    }

    /**
     * Vergleicht zwei Instanzen von {@link MonetaryAmount}. Nicht signifikante
     * Nachkommastellen werden dabei ignoriert.
     *
     * @param amount Betrag eines {@code MonetaryAmount}, mit dem verglichen werid
     * @return {@code true} falls {@code amount >= this}.
     * @throws MonetaryException bei unterschiedlichen Waehrungen.
     */
    @Override
    public boolean isGreaterThanOrEqualTo(MonetaryAmount amount) {
        return this.compareTo(amount) >= 0;
    }

    /**
     * Vergleicht zwei Instanzen von {@link MonetaryAmount}. Nicht signifikante
     * Nachkommastellen werden dabei ignoriert.
     *
     * @param amount Betrag eines {@code MonetaryAmount}, mit dem verglichen werid
     * @return {@code true} falls {@code amount < this}.
     * @throws MonetaryException bei unterschiedlichen Waehrungen.
     */
    @Override
    public boolean isLessThan(MonetaryAmount amount) {
        return this.compareTo(amount) < 0;
    }

    /**
     * Vergleicht zwei Instanzen von {@link MonetaryAmount}. Nicht signifikante
     * Nachkommastellen werden dabei ignoriert.
     *
     * @param amount Betrag eines {@code MonetaryAmount}, mit dem verglichen werid
     * @return {@code true} falls {@code amount <= this}.
     * @throws MonetaryException bei unterschiedlichen Waehrungen.
     */
    @Override
    public boolean isLessThanOrEqualTo(MonetaryAmount amount) {
        return this.compareTo(amount) <= 0;
    }

    /**
     * Zwei Geldbetraege sind nur dann gleich, wenn sie die gleiche Waehrung
     * und den gleichen Betrag haben.
     *
     * @param other der andere Geldbetrag oder MonetaryAmount
     * @return true, falls Waehrung und Betrag gleich ist
     * @throws MonetaryException wenn die Waehrungen nicht uebereinstimmen
     */
    @Override
    public boolean isEqualTo(MonetaryAmount other) {
        checkCurrency(other);
        return isNumberEqualTo(other.getNumber());
    }

    private boolean isNumberEqualTo(NumberValue value) {
        BigDecimal otherValue = toBigDecimal(value, context);
        return betrag.compareTo(otherValue) == 0;
    }

    private static BigDecimal toBigDecimal(NumberValue value) {
        return value.numberValue(BigDecimal.class);
    }

    private static BigDecimal toBigDecimal(NumberValue value, MonetaryContext mc) {
        Number n = toBigDecimal(value);
        return toBigDecimal(n, mc);
    }

    private static BigDecimal toBigDecimal(double value) {
        NUMBER_VALIDATOR.verifyNumber(value);
        return BigDecimal.valueOf(value);
    }

    /**
     * Returns the signum function of this {@code MonetaryAmount}.
     *
     * @return -1, 0, or 1 as the value of this {@code MonetaryAmount} is negative, zero, or
     * positive.
     */
    @Override
    public int signum() {
        return toBigDecimal(getNumber()).signum();
    }

    /**
     * Liefert die Summe mit dem anderen Gelbetrag zurueck. Vorausgesetzt,
     * beide Betraege haben die gleichen Waehrungen. Einzige Ausnahem davon
     * ist die Addition von 0, da hier die Waehrung egal ist (neutrale
     * Operation).
     *
     * @param other value to be added to this {@code MonetaryAmount}.
     * @return {@code this + amount}
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     *                             the {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public Geldbetrag add(MonetaryAmount other) {
        if (betrag.compareTo(BigDecimal.ZERO) == 0) {
            return Geldbetrag.valueOf(other);
        }
        BigDecimal n = toBigDecimal(other.getNumber(), context);
        if (n.compareTo(BigDecimal.ZERO) == 0) {
            return this;
        }
        checkCurrency(other);
        return Geldbetrag.valueOf(betrag.add(n), currency);
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>this -
     * amount</code>, and whose scale is <code>max(this.scale(),
     * subtrahend.scale()</code>.
     *
     * @param amount value to be subtracted from this {@code MonetaryAmount}.
     * @return {@code this - amount}
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     *                             the {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public Geldbetrag subtract(MonetaryAmount amount) {
        return add(amount.negate());
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <tt>(this &times;
     * multiplicand)</tt>, and whose scale is <code>this.scale() +
     * multiplicand.scale()</code>.
     *
     * @param multiplicand value to be multiplied by this {@code MonetaryAmount}.
     * @return {@code this * multiplicand}
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     *                             the {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public MonetaryAmount multiply(long multiplicand) {
        return multiply(BigDecimal.valueOf(multiplicand));
    }

    /**
     * Liefert einen GeldBetrag, desseen Wert <tt>(this &times; 
     * multiplicand)</tt> und desse Genauigkeit (scale) 
     * <code>this.scale() + multiplicand.scale()</code> entspricht.
     *
     * @param multiplicand Multiplikant (wird evtl. gerundet, wenn die
     *                     Genauigkeit zu hoch ist
     * @return {@code this * multiplicand}
     * @throws ArithmeticException bei "unendlich" oder "NaN" als Mulitiplikant
     */
    @Override
    public MonetaryAmount multiply(double multiplicand) {
        return multiply(toBigDecimal(multiplicand));
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <tt>(this &times;
     * multiplicand)</tt>, and whose scale is <code>this.scale() +
     * multiplicand.scale()</code>.
     *
     * @param multiplicand value to be multiplied by this {@code MonetaryAmount}. If the multiplicand's scale exceeds
     *                     the
     *                     capabilities of the implementation, it may be rounded implicitly.
     * @return {@code this * multiplicand}
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     *                             the {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public MonetaryAmount multiply(Number multiplicand) {
        BigDecimal d = toBigDecimal(multiplicand, context);
        if (BigDecimal.ONE.compareTo(d) == 0) {
            return this;
        }
        BigDecimal multiplied = betrag.multiply(d);
        return Geldbetrag.valueOf(multiplied, currency);
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>this /
     * divisor</code>, and whose preferred scale is <code>this.scale() -
     * divisor.scale()</code>; if the exact quotient cannot be represented an {@code ArithmeticException}
     * is thrown.
     *
     * @param divisor value by which this {@code MonetaryAmount} is to be divided.
     * @return {@code this / divisor}
     * @throws ArithmeticException if the exact quotient does not have a terminating decimal expansion, or if the
     *                             result exceeds the numeric capabilities of this implementation class, i.e. the
     *                             {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public Geldbetrag divide(long divisor) {
        return divide(BigDecimal.valueOf(divisor));
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>this /
     * divisor</code>, and whose preferred scale is <code>this.scale() -
     * divisor.scale()</code>; if the exact quotient cannot be represented an {@code ArithmeticException}
     * is thrown.
     *
     * @param divisor value by which this {@code MonetaryAmount} is to be divided.
     * @return {@code this / divisor}
     * @throws ArithmeticException if the exact quotient does not have a terminating decimal expansion, or if the
     *                             result exceeds the numeric capabilities of this implementation class, i.e. the
     *                             {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public MonetaryAmount divide(double divisor) {
        if (isInfinite(divisor)) {
            return Geldbetrag.valueOf(BigDecimal.ZERO, currency);
        }
        return divide(BigDecimal.valueOf(divisor));
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>this /
     * divisor</code>, and whose preferred scale is <code>this.scale() -
     * divisor.scale()</code>; if the exact quotient cannot be represented an {@code ArithmeticException}
     * is thrown.
     *
     * @param divisor value by which this {@code MonetaryAmount} is to be divided.
     * @return {@code this / divisor}
     * @throws ArithmeticException if the exact quotient does not have a terminating decimal expansion, or if the
     *                             result exceeds the numeric capabilities of this implementation class, i.e. the
     *                             {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public Geldbetrag divide(Number divisor) {
        BigDecimal d = toBigDecimal(divisor, context);
        if (BigDecimal.ONE.compareTo(d) == 0) {
            return this;
        }
        return Geldbetrag
                .valueOf(betrag.setScale(4, RoundingMode.HALF_UP).divide(d, RoundingMode.HALF_UP), currency);
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>this % divisor</code>.
     * <p>
     * <p>
     * The remainder is given by
     * <code>this.subtract(this.divideToIntegralValue(divisor).multiply(divisor)</code> . Note that this
     * is not the modulo operation (the result can be negative).
     *
     * @param divisor value by which this {@code MonetaryAmount} is to be divided.
     * @return {@code this % divisor}.
     * @throws ArithmeticException if {@code divisor==0}, or if the result exceeds the numeric capabilities of this
     *                             implementation class, i.e. the {@link MonetaryContext} cannot be adapted as
     *                             required.
     */
    @Override
    public Geldbetrag remainder(long divisor) {
        return remainder(BigDecimal.valueOf(divisor));
    }

    /**
     * Liefert eine @code Geldbetrag} zurueck, dessen Wert 
     * <code>this % divisor</code> entspricht. Der Betrag kann auch
     * negativ sein (im Gegensatz zur Modulo-Operation).
     *
     * @param divisor Wert, durch den der {@code Geldbetrag} geteilt wird.
     * @return {@code this % divisor}.
     */
    @Override
    public Geldbetrag remainder(double divisor) {
        if (isInfinite(divisor)) {
            return Geldbetrag.valueOf(0, currency);
        }
        return remainder(toBigDecimal(divisor));
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>this % divisor</code>.
     * <p>
     * <p>
     * The remainder is given by
     * <code>this.subtract(this.divideToIntegralValue(divisor).multiply(divisor)</code> . Note that this
     * is not the modulo operation (the result can be negative).
     *
     * @param divisor value by which this {@code MonetaryAmount} is to be divided.
     * @return {@code this % divisor}.
     * @throws ArithmeticException if {@code divisor==0}, or if the result exceeds the numeric capabilities of this
     *                             implementation class, i.e. the {@link MonetaryContext} cannot be adapted as
     *                             required.
     */
    @Override
    public Geldbetrag remainder(Number divisor) {
        return Geldbetrag.valueOf(betrag.remainder(toBigDecimal(divisor, context)), currency);
    }

    /**
     * Liefert ein zwei-elementiges {@code Geldbatrag}-Array mit dem Ergebnis 
     * {@code divideToIntegralValue} und{@code remainder}.
     *
     * @param divisor Teiler
     * @return ein zwei-elementiges {@code Geldbatrag}-Array
     * @throws ArithmeticException bei {@code divisor==0}
     * @see #divideToIntegralValue(long)
     * @see #remainder(long)
     */
    @Override
    public Geldbetrag[] divideAndRemainder(long divisor) {
        return divideAndRemainder(BigDecimal.valueOf(divisor));
    }

    /**
     * Liefert ein zwei-elementiges {@code Geldbatrag}-Array mit dem Ergebnis 
     * {@code divideToIntegralValue} und{@code remainder}.
     *
     * @param divisor Teiler
     * @return ein zwei-elementiges {@code Geldbatrag}-Array
     * @throws ArithmeticException bei {@code divisor==0}
     * @see #divideToIntegralValue(double)
     * @see #remainder(double)
     */
    @Override
    public Geldbetrag[] divideAndRemainder(double divisor) {
        if (isInfinite(divisor)) {
            return toGeldbetragArray(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return divideAndRemainder(BigDecimal.valueOf(divisor));
    }

    private static boolean isInfinite(double divisor) {
        if ((divisor == Double.POSITIVE_INFINITY) || (divisor == Double.NEGATIVE_INFINITY)) {
            return true;
        }
        if (Double.isNaN(divisor)) {
            throw new ArithmeticException("invalid number: NaN");
        }
        return false;
    }

    /**
     * Liefert ein zwei-elementiges {@code Geldbatrag}-Array mit dem Ergebnis 
     * {@code divideToIntegralValue} und{@code remainder}.
     *
     * @param divisor Teiler
     * @return ein zwei-elementiges {@code Geldbatrag}-Array
     * @throws ArithmeticException bei {@code divisor==0}
     * @see #divideToIntegralValue(Number)
     * @see #remainder(Number)
     */
    @Override
    public Geldbetrag[] divideAndRemainder(Number divisor) {
        BigDecimal[] numbers = betrag.divideAndRemainder(toBigDecimal(divisor, context));
        return toGeldbetragArray(numbers);
    }

    private Geldbetrag[] toGeldbetragArray(BigDecimal... numbers) {
        Geldbetrag[] betraege = new Geldbetrag[numbers.length];
        for (int i = 0; i < betraege.length; i++) {
            betraege[i] = Geldbetrag.valueOf(numbers[i], currency);
        }
        return betraege;
    }

    /**
     * Liefert den Integer-Teil des Quotienten <code>this / divisor</code>
     * (abgerundet).
     *
     * @param divisor Teiler
     * @return Integer-Teil von {@code this / divisor}.
     * @throws ArithmeticException falls {@code divisor==0}
     * @see BigDecimal#divideToIntegralValue(BigDecimal)
     */
    @Override
    public Geldbetrag divideToIntegralValue(long divisor) {
        return divideToIntegralValue(BigDecimal.valueOf(divisor));
    }

    /**
     * Liefert den Integer-Teil des Quotienten <code>this / divisor</code>
     * (abgerundet).
     *
     * @param divisor Teiler
     * @return Integer-Teil von {@code this / divisor}.
     * @throws ArithmeticException falls {@code divisor==0}
     * @see BigDecimal#divideToIntegralValue(BigDecimal)
     */
    @Override
    public Geldbetrag divideToIntegralValue(double divisor) {
        return divideToIntegralValue(BigDecimal.valueOf(divisor));
    }

    /**
     * Liefert den Integer-Teil des Quotienten <code>this / divisor</code>
     * (abgerundet).
     *
     * @param divisor Teiler
     * @return Integer-Teil von {@code this / divisor}.
     * @throws ArithmeticException falls {@code divisor==0}
     * @see BigDecimal#divideToIntegralValue(BigDecimal)
     */
    @Override
    public Geldbetrag divideToIntegralValue(Number divisor) {
        return Geldbetrag.valueOf(betrag.divideToIntegralValue(toBigDecimal(divisor, context)), currency);
    }

    /**
     * Liefert eine {@code Geldbetrag}, dessen Wert ({@code this} * 10<sup>n</sup>)
     * entspricht.
     *
     * @param power 10er-Potenz (z.B. 3 fuer 1000)
     * @return berechneter Geldbetrag
     */
    @Override
    public Geldbetrag scaleByPowerOfTen(int power) {
        BigDecimal scaled =
                betrag.scaleByPowerOfTen(power).setScale(context.getMaxScale(), context.get(RoundingMode.class));
        return Geldbetrag.valueOf(scaled, getCurrency(), context);
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is the absolute value of this
     * {@code MonetaryAmount}, and whose scale is {@code this.scale()}.
     *
     * @return <code>abs(this</code>
     */
    @Override
    public Geldbetrag abs() {
        if (betrag.compareTo(BigDecimal.ZERO) < 0) {
            return negate();
        } else {
            return this;
        }
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>-this</code>, and whose scale is
     * {@code this.scale()}.
     *
     * @return {@code -this}.
     */
    @Override
    public Geldbetrag negate() {
        return valueOf(betrag.negate(), currency);
    }

    /**
     * Liefert immer eine positiven Geldbetrag.
     *
     * @return positiver Geldbetrag
     * @see BigDecimal#plus()
     */
    @Override
    public Geldbetrag plus() {
        if (betrag.compareTo(BigDecimal.ZERO) < 0) {
            return negate();
        } else {
            return this;
        }
    }

    /**
     * Liefert einen {@code Geldbetrag}, der numerisch dem gleichen Wert
     * entspricht, aber ohne Nullen in den Nachkommastellen.
     *
     * @return im Priip der gleiche {@code Geldbetrag}, nur wird die Zahl
     *         intern anders repraesentiert.
     */
    @Override
    public Geldbetrag stripTrailingZeros() {
        if (isZero()) {
            return valueOf(BigDecimal.ZERO, getCurrency());
        }
        return valueOf(betrag.stripTrailingZeros(), getCurrency(), context);
    }

    /**
     * Vergleicht die Zahlenwerter der beiden Geldbetraege. Aber nur, wenn es
     * sich um die gleiche Waehrung handelt. Sonst wird eine 
     * {@link MonetaryException} ausgeloest.
     * Compares this object with the specified object for order.  Returns a
     * 
     * @param other der andere Geldbetrag
     * @return 0 bei Gleicheit; negative Zahl, wenn dieser Geldbetrag kleiner
     * als der andere ist; sonst positive Zahl.
     */
    @Override
    public int compareTo(MonetaryAmount other) {
        BigDecimal n = toBigDecimal(other.getNumber());
        if ((this.betrag.compareTo(BigDecimal.ZERO) != 0) && (n.compareTo(BigDecimal.ZERO) != 0)) {
            checkCurrency(other);
        }
        return betrag.compareTo(n);
    }

    /**
     * Vergleicht nur den Zahlenwert und ignoriert die Waehrung. Diese Methode
     * ist aus Kompatibiltaetsgruenden zur BigDecimal-Klasse enthalten.
     *
     * @param other der andere Betrag
     * @return 0 bei Gleicheit; negative Zahl, wenn die Zahle kleiner als die
     * andere ist, sonst positive Zahl.
     */
    public int compareTo(Number other) {
        return this.compareTo(Geldbetrag.valueOf(other, currency));
    }

    /**
     * Liefert die entsprechende Waehrungseinheit ({@link CurrencyUnit}).
     *
     * @return die entsprechende {@link CurrencyUnit}, not null.
     */
    @Override
    public CurrencyUnit getCurrency() {
        return currency;
    }

    /**
     * Liefert den entsprechenden {@link NumberValue}.
     *
     * @return der entsprechende {@link NumberValue}, not null.
     */
    @Override
    public NumberValue getNumber() {
        return new DefaultNumberValue(betrag);
    }

    /**
     * Liefert nur die Zahl als 'double' zurueck. Sie entspricht der
     * gleichnamigen Methode aus {@link BigDecimal}.
     *
     * @return Zahl als 'double'
     * @see BigDecimal#doubleValue()
     */
    public double doubleValue() {
        return betrag.doubleValue();
    }

    private static BigDecimal toBigDecimal(Number value, MonetaryContext monetaryContext) {
        BigDecimal n = BigDecimal.valueOf(value.doubleValue());
        if (value instanceof BigDecimal) {
            n = (BigDecimal) value;
        } else if (value instanceof DefaultNumberValue) {
            n = ((DefaultNumberValue) value).numberValue(BigDecimal.class);
        }
        RoundingMode roundingMode = monetaryContext.get(RoundingMode.class);
        if (roundingMode == null) {
            roundingMode = RoundingMode.HALF_UP;
        }
        int scale = monetaryContext.getMaxScale();
        if (scale <= 0) {
            return n;
        } else {
            BigDecimal scaled = n.setScale(scale, roundingMode);
            if (scaled.compareTo(n) != 0) {
                throw new LocalizedArithmeticException(value, "lost_precision");
            }
            return scaled;
        }
    }
    
    /**
     * Hash-Code.
     * 
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        return betrag.hashCode();
    }

    /**
     * Zwei Betraege sind gleich, wenn Betrag und Waehrung gleich sind.
     *
     * @param obj the obj
     * @return true, falls gleich
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Geldbetrag)) {
            return false;
        }
        Geldbetrag other = (Geldbetrag) obj;
        if (!hasSameCurrency(other)) return false;
        return this.isEqualTo(other);
    }

    private boolean hasSameCurrency(MonetaryAmount other) {
        return this.getCurrency().equals(other.getCurrency());
    }
    
    private void checkCurrency(MonetaryAmount other) {
        if (!hasSameCurrency(other)) throw new LocalizedMonetaryException("different currencies", this, other);
    }

    /**
     * Liefert das Ergebnis des Operator <b>vom selben Typ</b>.
     *
     * @param operator Operator (nicht null)
     * @return ein Objekt desselben Typs (nicht null)
     * @see javax.money.MonetaryAmount#with(javax.money.MonetaryOperator)
     */
    @Override
    public Geldbetrag with(MonetaryOperator operator) {
        Objects.requireNonNull(operator);
        try {
            return (Geldbetrag) operator.apply(this);
        } catch (MonetaryException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new LocalizedMonetaryException("operator failed", operator, ex);
        }
    }

    /**
     * Fraegt einen Wert an.
     *
     * @param query Anrfage (nicht null)
     * @return Ergebnis der Anfrage (kann null sein)
     * @see javax.money.MonetaryAmount#query(javax.money.MonetaryQuery)
     */
    @Override
    public <R> R query(MonetaryQuery<R> query) {
        Objects.requireNonNull(query);
        try {
            return query.queryFrom(this);
        } catch (MonetaryException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new LocalizedMonetaryException("query failed", query, ex);
        }
    }

    /**
     * Gibt den Betrag in Kurz-Format aus: ohne Nachkommastellen und mit dem
     * Waehrungssymbol.
     * 
     * @return z.B. "19 $"
     */
    public String toShortString() {
        return betrag.setScale(0, RoundingMode.HALF_UP) + " " + Waehrung.getSymbol(currency);
    }

    /**
     * Um anzuzeigen, dass es ein Geldbtrag ist, wird zusaetzlich noch das
     * Waehrungszeichen (abhaengig von der eingestellten Locale) ausgegeben.
     *
     * @return z.B. "19.00 USD"
     * @see java.math.BigDecimal#toString()
     */
    @Override
    public String toString() {
        return StringUtils.substringBefore(NumberFormat.getCurrencyInstance().format(this.betrag), " ") + " " +
                currency;
    }

    /**
     * Hier wird der Geldbetrag mit voller Genauigkeit ausgegeben.
     * 
     * @return z.B. "19.0012 USD"
     */
    public String toLongString() {
        NumberFormat formatter = DecimalFormat.getInstance();
        formatter.setMinimumFractionDigits(context.getMaxScale());
        formatter.setMinimumFractionDigits(context.getMaxScale());
        return formatter.format(betrag) + " " + currency;
    }

}
