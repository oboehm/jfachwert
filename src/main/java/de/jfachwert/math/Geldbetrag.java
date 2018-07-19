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
package de.jfachwert.math;

import de.jfachwert.Fachwert;
import org.javamoney.moneta.spi.DefaultNumberValue;

import javax.money.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse unterstuetzt sie JSR 354 und das{@link MonetaryAmount} 
 * Interface, das Bestandteil von Java 9 ist. Da in alten Anwendungen
 * oftmals ein {@link BigDecimal} verwendet wurde, wird auch diese
 * Schnittstelle weitgehende unterstützt. Einzige Unterschied ist
 * die {@link MonetaryAmount#stripTrailingZeros()}-Methode, die einen anderen
 * Rueckgabewert hat. Deswegen ist dies Klasse auch nicht von
 * {@link BigDecimal} abgeleitet..
 *
 * @author oboehm
 * @since 0.8 (18.07.2018)
 */
public class Geldbetrag implements MonetaryAmount, Fachwert {
    
    private static final Logger LOG = Logger.getLogger(Geldbetrag.class.getName());
    private static final Currency DEFAULT_CURRENCY = getDefaultCurrency();
    private final BigDecimal betrag;
    private final Currency currency;

    /**
     * Erzeugt eine Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1
     */
    public Geldbetrag(long betrag) {
        this(BigDecimal.valueOf(betrag));
    }

    /**
     * Erzeugt eine Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1.00
     */
    public Geldbetrag(double betrag) {
        this(BigDecimal.valueOf(betrag));
    }

    /**
     * Erzeugt eine Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. "1"
     */
    public Geldbetrag(String betrag) {
        this(new BigDecimal(betrag));
    }

    /**
     * Erzeugt eine Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1.00
     */
    public Geldbetrag(Number betrag) {
        this(betrag, DEFAULT_CURRENCY);
    }

    /**
     * Erzeugt eine Geldbetrag in der angegebenen Waehrung.
     *
     * @param betrag   Geldbetrag, z.B. 1.00
     * @param currency Waehrung, z.B. Euro
     */
    public Geldbetrag(Number betrag, Currency currency) {
        this.betrag = BigDecimal.valueOf(betrag.doubleValue());
        this.currency = currency;
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
    public Geldbetrag withWaehrung(String waehrung) {
        String normalized = waehrung.toUpperCase().trim();
        if ("DM".equalsIgnoreCase(normalized)) {
            normalized = "DEM";
        }
        return withWaehrung(Currency.getInstance(normalized));
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
    public Geldbetrag withWaehrung(Currency currency) {
        return new Geldbetrag(this.getNumber(), currency);
    }

    /**
     * Returns the {@link MonetaryContext} of this {@code MonetaryAmount}. The
     * {@link MonetaryContext} provides additional information about the numeric representation and
     * the numeric capabilities. This information can be used by code to determine situations where
     * {@code MonetaryAmount} instances must be converted to avoid implicit truncation, which can
     * lead to invalid results.
     *
     * @return the {@link MonetaryContext} of this {@code MonetaryAmount}, never {@code null} .
     */
    @Override
    public MonetaryContext getContext() {
        return null;
    }

    /**
     * Creates a new {@code MonetaryAmountFactory}, returning the same implementation type Hereby
     * this given amount is used as a template, so reusing the {@link CurrencyUnit}, its numeric
     * value, the algorithmic implementation as well as the current {@link MonetaryContext}.
     * <p>
     * This method is used for creating a new amount result after having done calculations that are
     * not directly mappable to the default monetary arithmetics, e.g. currency conversion.
     *
     * @return the new {@code MonetaryAmountFactory} with the given {@link MonetaryAmount} as its
     * default values.
     */
    @Override
    public MonetaryAmountFactory<? extends MonetaryAmount> getFactory() {
        return null;
    }

    /**
     * Compares two instances of {@link MonetaryAmount}, hereby ignoring non significant trailing
     * zeroes and different numeric capabilities.
     *
     * @param amount the {@code MonetaryAmount} to be compared with this instance.
     * @return {@code true} if {@code amount > this}.
     * @throws MonetaryException if the amount's currency is not equals to the currency of this instance.
     */
    @Override
    public boolean isGreaterThan(MonetaryAmount amount) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Compares two instances of {@link MonetaryAmount}, hereby ignoring non significant trailing
     * zeroes and different numeric capabilities.
     *
     * @param amount the {@link MonetaryAmount} to be compared with this instance.
     * @return {@code true} if {@code amount >= this}.
     * @throws MonetaryException if the amount's currency is not equals to the currency of this instance.
     */
    @Override
    public boolean isGreaterThanOrEqualTo(MonetaryAmount amount) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Compares two instances of {@link MonetaryAmount}, hereby ignoring non significant trailing
     * zeroes and different numeric capabilities.
     *
     * @param amount the {@link MonetaryAmount} to be compared with this instance.
     * @return {@code true} if {@code amount < this}.
     * @throws MonetaryException if the amount's currency is not equals to the currency of this instance.
     */
    @Override
    public boolean isLessThan(MonetaryAmount amount) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Compares two instances of {@link MonetaryAmount}, hereby ignoring non significant trailing
     * zeroes and different numeric capabilities.
     *
     * @param amt the {@link MonetaryAmount} to be compared with this instance.
     * @return {@code true} if {@code amount <= this}.
     * @throws MonetaryException if the amount's currency is not equals to the currency of this instance.
     */
    @Override
    public boolean isLessThanOrEqualTo(MonetaryAmount amt) {
        throw new UnsupportedOperationException("not yet implemented");
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
        if (this.getCurrency().equals(other.getCurrency())) {
            return isNumberEqualTo(other.getNumber());
        } else {
            throw new MonetaryException("different currencies: cannot compare " + this + " with " + other);
        }
    }

    private boolean isNumberEqualTo(NumberValue value) {
        BigDecimal otherValue = toBigDecimal(value);
        BigDecimal thisValue = toBigDecimal(this.getNumber());
        return thisValue.equals(otherValue);
    }

    private static BigDecimal toBigDecimal(NumberValue value) {
        return value.numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the signum function of this {@code MonetaryAmount}.
     *
     * @return -1, 0, or 1 as the value of this {@code MonetaryAmount} is negative, zero, or
     * positive.
     */
    @Override
    public int signum() {
        return 0;
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>this + amount</code>, and whose scale is
     * <code>max(this.scale(),
     * amount.scale()</code>.
     *
     * @param amount value to be added to this {@code MonetaryAmount}.
     * @return {@code this + amount}
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     *                             the {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public MonetaryAmount add(MonetaryAmount amount) {
        return null;
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
    public MonetaryAmount subtract(MonetaryAmount amount) {
        return null;
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
        return null;
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <tt>(this &times;
     * multiplicand)</tt>, and whose scale is <code>this.scale() +
     * multiplicand.scale()</code>.
     * By default the input value's scale will be rounded to
     * accommodate the format capabilities, and no {@link ArithmeticException}
     * is thrown if the input number's scale exceeds the capabilities.
     *
     * @param multiplicand value to be multiplied by this {@code MonetaryAmount}. If the multiplicand's scale exceeds
     *                     the
     *                     capabilities of the implementation, it may be rounded implicitly.
     * @return {@code this * multiplicand}
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     *                             the {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public MonetaryAmount multiply(double multiplicand) {
        return null;
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
        return null;
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
    public MonetaryAmount divide(long divisor) {
        return null;
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
        return null;
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
    public MonetaryAmount divide(Number divisor) {
        return null;
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
    public MonetaryAmount remainder(long divisor) {
        return null;
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
    public MonetaryAmount remainder(double divisor) {
        return null;
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
    public MonetaryAmount remainder(Number divisor) {
        return null;
    }

    /**
     * Returns a two-element {@code MonetaryAmount} array containing the result of
     * {@code divideToIntegralValue} followed by the result of {@code remainder} on the two
     * operands.
     * <p>
     * <p>
     * Note that if both the integer quotient and remainder are needed, this method is faster than
     * using the {@code divideToIntegralValue} and {@code remainder} methods separately because the
     * division need only be carried out once.
     *
     * @param divisor value by which this {@code MonetaryAmount} is to be divided, and the remainder
     *                computed.
     * @return a two element {@code MonetaryAmount} array: the quotient (the result of
     * {@code divideToIntegralValue}) is the initial element and the remainder is the final
     * element.
     * @throws ArithmeticException if {@code divisor==0}, or if the result exceeds the numeric capabilities of this
     *                             implementation class, i.e. the {@link MonetaryContext} cannot be adapted as
     *                             required.
     * @see #divideToIntegralValue(long)
     * @see #remainder(long)
     */
    @Override
    public MonetaryAmount[] divideAndRemainder(long divisor) {
        return new MonetaryAmount[0];
    }

    /**
     * Returns a two-element {@code MonetaryAmount} array containing the result of
     * {@code divideToIntegralValue} followed by the result of {@code remainder} on the two
     * operands.
     * <p>
     * <p>
     * Note that if both the integer quotient and remainder are needed, this method is faster than
     * using the {@code divideToIntegralValue} and {@code remainder} methods separately because the
     * division need only be carried out once.
     *
     * @param divisor value by which this {@code MonetaryAmount} is to be divided, and the remainder
     *                computed.
     * @return a two element {@code MonetaryAmount} array: the quotient (the result of
     * {@code divideToIntegralValue}) is the initial element and the remainder is the final
     * element.
     * @throws ArithmeticException if {@code divisor==0}, or if the result exceeds the numeric capabilities of this
     *                             implementation class, i.e. the {@link MonetaryContext} cannot be adapted as
     *                             required.
     * @see #divideToIntegralValue(double)
     * @see #remainder(double)
     */
    @Override
    public MonetaryAmount[] divideAndRemainder(double divisor) {
        return new MonetaryAmount[0];
    }

    /**
     * Returns a two-element {@code MonetaryAmount} array containing the result of
     * {@code divideToIntegralValue} followed by the result of {@code remainder} on the two
     * operands.
     * <p>
     * <p>
     * Note that if both the integer quotient and remainder are needed, this method is faster than
     * using the {@code divideToIntegralValue} and {@code remainder} methods separately because the
     * division need only be carried out once.
     *
     * @param divisor value by which this {@code MonetaryAmount} is to be divided, and the remainder
     *                computed.
     * @return a two element {@code MonetaryAmount} array: the quotient (the result of
     * {@code divideToIntegralValue}) is the initial element and the remainder is the final
     * element.
     * @throws ArithmeticException if {@code divisor==0}, or if the result exceeds the numeric capabilities of this
     *                             implementation class, i.e. the {@link MonetaryContext} cannot be adapted as
     *                             required.
     * @see #divideToIntegralValue(Number)
     * @see #remainder(Number)
     */
    @Override
    public MonetaryAmount[] divideAndRemainder(Number divisor) {
        return new MonetaryAmount[0];
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is the integer part of the quotient
     * <code>this / divisor</code> rounded down. The preferred scale of the result is
     * <code>this.scale() -
     * divisor.scale()</code>.
     *
     * @param divisor value by which this {@code BigDecimal} is to be divided.
     * @return The integer part of {@code this / divisor}.
     * @throws ArithmeticException if {@code divisor==0}
     * @see BigDecimal#divideToIntegralValue(BigDecimal)
     */
    @Override
    public MonetaryAmount divideToIntegralValue(long divisor) {
        return null;
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is the integer part of the quotient
     * <code>this / divisor</code> rounded down. The preferred scale of the result is
     * <code>this.scale() - divisor.scale()</code>.
     *
     * @param divisor value by which this {@code BigDecimal} is to be divided.
     * @return The integer part of {@code this / divisor}.
     * @throws ArithmeticException if {@code divisor==0}
     * @see BigDecimal#divideToIntegralValue(BigDecimal)
     */
    @Override
    public MonetaryAmount divideToIntegralValue(double divisor) {
        return null;
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is the integer part of the quotient
     * <code>this / divisor</code> rounded down. The preferred scale of the result is
     * <code>this.scale() -
     * divisor.scale()</code>.
     *
     * @param divisor value by which this {@code BigDecimal} is to be divided.
     * @return The integer part of {@code this / divisor}.
     * @throws ArithmeticException if {@code divisor==0}
     * @see BigDecimal#divideToIntegralValue(BigDecimal)
     */
    @Override
    public MonetaryAmount divideToIntegralValue(Number divisor) {
        return null;
    }

    /**
     * Returns a {@code MonetaryAmount} whose numerical value is equal to ( {@code this} *
     * 10<sup>n</sup>). The scale of the result is <code>this.scale() - n</code>.
     *
     * @param power the power.
     * @return the calculated amount value.
     * @throws ArithmeticException if the scale would be outside the range of a 32-bit integer, or if the result
     *                             exceeds the numeric capabilities of this implementation class, i.e. the
     *                             {@link MonetaryContext} cannot be adapted as required.
     */
    @Override
    public MonetaryAmount scaleByPowerOfTen(int power) {
        return null;
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is the absolute value of this
     * {@code MonetaryAmount}, and whose scale is {@code this.scale()}.
     *
     * @return <code>abs(this</code>
     */
    @Override
    public MonetaryAmount abs() {
        return null;
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>-this</code>, and whose scale is
     * {@code this.scale()}.
     *
     * @return {@code -this}.
     */
    @Override
    public MonetaryAmount negate() {
        return null;
    }

    /**
     * Returns a {@code MonetaryAmount} whose value is <code>+this</code>, with rounding according to
     * the context settings.
     *
     * @return {@code this}, rounded as necessary. A zero result will have a scale of 0.
     * @throws ArithmeticException if rounding fails.
     * @see BigDecimal#plus()
     */
    @Override
    public MonetaryAmount plus() {
        return null;
    }

    /**
     * Returns a {@code MonetaryAmount} which is numerically equal to this one but with any trailing
     * zeros removed from the representation. For example, stripping the trailing zeros from the
     * {@code MonetaryAmount} value {@code CHF 600.0}, which has [{@code BigInteger}, {@code scale}]
     * components equals to [6000, 1], yields {@code 6E2} with [ {@code BigInteger}, {@code scale}]
     * components equals to [6, -2]
     *
     * @return a numerically equal {@code MonetaryAmount} with any trailing zeros removed.
     */
    @Override
    public MonetaryAmount stripTrailingZeros() {
        return null;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(MonetaryAmount o) {
        return 0;
    }

    /**
     * Liefert die entsprechende Waehrungseinheit ({@link CurrencyUnit}).
     *
     * @return die entsprechende {@link CurrencyUnit}, not null.
     */
    @Override
    public CurrencyUnit getCurrency() {
        return Monetary.getCurrency(currency.getCurrencyCode());
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
        if (!this.getCurrency().equals(other.getCurrency())) {
            return false;
        }
        return this.isEqualTo(other);
    }

    /**
     * Um anzuzeigen, dass es ein Geldbtrag ist, wird zusaetzlich noch das
     * Waehrungszeichen (abhaengig von der eingestellten Locale) ausgegeben.
     *
     * @return z.B. "19.00$"
     * @see java.math.BigDecimal#toString()
     */
    @Override
    public String toString() {
        return this.getNumber() + currency.getSymbol();
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
     *     at de.jfachwert.math.Geldbetrag.&lt;clinit&gt;
     *     ...
     * </pre>
     *
     * @return normalerweise die deutsche Currency
     */
    public static Currency getDefaultCurrency() {
        Locale[] locales = { Locale.getDefault(), Locale.GERMANY, Locale.GERMAN };
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

}
