/*
 * Copyright (c) 2018-2020 by Oliver Boehm
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
package de.jfachwert.bank

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.KFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.bank.Waehrung.Companion.getSymbol
import de.jfachwert.bank.Waehrung.Companion.toCurrency
import de.jfachwert.bank.internal.GeldbetragFormatter
import de.jfachwert.bank.internal.Zahlenwert
import de.jfachwert.pruefung.NumberValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import de.jfachwert.pruefung.exception.LocalizedArithmeticException
import de.jfachwert.pruefung.exception.LocalizedMonetaryException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import javax.money.*
import javax.money.format.MonetaryAmountFormat
import javax.money.format.MonetaryParseException

/**
 * Diese Klasse unterstuetzt den JSR 354 und das [MonetaryAmount]
 * Interface, das Bestandteil von Java 9 ist. Da in alten Anwendungen
 * oftmals ein [BigDecimal] verwendet wurde, wird auch diese
 * Schnittstelle weitgehende unterstützt. Einzige Unterschied ist
 * die [MonetaryAmount.stripTrailingZeros]-Methode, die einen anderen
 * Rueckgabewert hat. Deswegen ist diese Klasse auch nicht von
 * [BigDecimal] abgeleitet...
 *
 * Im Gegensatz zur [org.javamoney.moneta.Money]- und
 * [org.javamoney.moneta.FastMoney]-Klasse kann diese Klasse
 * ueberschrieben werden, falls anderes Rundungsverhalten oder
 * eine angepasste Implementierung benoetigt wird.
 *
 * @author oboehm
 * @since 1.0 (18.07.2018)
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Geldbetrag @JvmOverloads constructor(betrag: Number, currency: CurrencyUnit, context: MonetaryContext = FACTORY.getMonetaryContextOf(betrag)) : MonetaryAmount, Comparable<MonetaryAmount>, KFachwert {

    private val betrag: BigDecimal
    private val context: MonetaryContext

    // Eine Umstellung auf 'Waehrung' oder 'Currency' fuehrt leider dazu, dass
    // dann das TCK zu JSR-354 fehlschlaegt, da Waehrung nicht final und damit
    // potentiell nicht immutable ist. Daher unterdruecken wir jetzt die
    // Sonar-Warnung "Make "currency" transient or serializable".
    @SuppressWarnings("squid:S1948")
    private val currency: CurrencyUnit

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1
     */
    constructor(betrag: Long) : this(BigDecimal.valueOf(betrag))

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. 1.00
     */
    constructor(betrag: Double) : this(BigDecimal.valueOf(betrag))

    /**
     * Erzeugt einen Geldbetrag in der aktuellen Landeswaehrung.
     *
     * @param betrag Geldbetrag, z.B. "1"
     */
    constructor(betrag: String) : this(valueOf(betrag))

    /**
     * Dies ist zum einen der CopyConstructor als Ersatz fuer eine
     * clone-Methode, zum anderen wandelt es einen [MonetaryAmount]
     * in ein GeldBetrag-Objekt.
     *
     * @param other der andere Geldbetrag
     */
    constructor(other: MonetaryAmount) : this(other.number, Currency.getInstance(other.currency.currencyCode))

    /**
     * Erzeugt einen Geldbetrag in der angegebenen Waehrung.
     *
     * @param betrag   Geldbetrag, z.B. 1.00
     * @param currency Waehrung, z.B. Euro
     */
    @JvmOverloads
    constructor(betrag: Number, currency: Currency? = Waehrung.DEFAULT_CURRENCY) : this(betrag, Waehrung.of(currency!!))

    /**
     * Liefert einen Geldbetrag mit der neuen gewuenschten Waehrung zurueck.
     * Dabei findet **keine** Umrechnung statt.
     *
     * Anmerkung: Der Prefix "with" kommt von der Namenskonvention in Scala
     * fuer immutable Objekte.
     *
     * @param unit die Waehrungseinheit
     * @return Geldbetrag mit neuer Waehrung
     */
    fun withCurrency(unit: CurrencyUnit): Geldbetrag {
        return withCurrency(unit.currencyCode)
    }

    /**
     * Liefert einen Geldbetrag mit der neuen gewuenschten Waehrung zurueck.
     * Dabei findet **keine** Umrechnung statt.
     *
     * Anmerkung: Der Prefix "with" kommt von der Namenskonvention in Scala
     * fuer immutable Objekte.
     *
     * @param waehrung Waehrung
     * @return Geldbetrag mit neuer Waehrung
     */
    fun withCurrency(waehrung: String): Geldbetrag {
        var normalized = waehrung.uppercase().trim { it <= ' ' }
        if ("DM".equals(normalized, ignoreCase = true)) {
            normalized = "DEM"
        }
        return withCurrency(Currency.getInstance(normalized))
    }

    /**
     * Liefert einen Geldbetrag mit der neuen gewuenschten Waehrung zurueck.
     * Dabei findet **keine** Umrechnung statt.
     *
     * Anmerkung: Der Prefix "with" kommt von der Namenskonvention in Scala
     * fuer immutable Objekte.
     *
     * @param currency Waehrung
     * @return Geldbetrag mit neuer Waehrung
     */
    fun withCurrency(currency: Currency?): Geldbetrag {
        return Geldbetrag(this.number, currency)
    }

    /**
     * Gibt den [MonetaryContext] des Geldbetrags zurueck. Der
     * [MonetaryContext] enthaelt Informationen ueber numerische
     * Eigenschaften wie Anzahl Nachkommastellen oder Rundungsinformation.
     *
     * @return den [MonetaryContext] zum Geldbetrag
     */
    override fun getContext(): MonetaryContext {
        return context
    }

    /**
     * Erzeugt eine neue @code GeldbetragFactory}, die @link CurrencyUnit}, den
     * numerischen Werte und den aktuellen [MonetaryContext] verwendet.
     *
     * @return eine `GeldbetragFactory`, mit dem ein neuer (gleicher)
     * Geldbetrag erzeugt werden kann.
     */
    override fun getFactory(): GeldbetragFactory {
        return GeldbetragFactory().setCurrency(currency).setNumber(betrag).setContext(context)
    }

    /**
     * Vergleicht zwei Instanzen von [MonetaryAmount]. Nicht signifikante
     * Nachkommastellen werden dabei ignoriert.
     *
     * @param amount Betrag eines `MonetaryAmount`, mit dem verglichen werid
     * @return `true` falls `amount > this`.
     * @throws MonetaryException bei unterschiedlichen Waehrungen.
     */
    override fun isGreaterThan(amount: MonetaryAmount): Boolean {
        return this.compareTo(amount) > 0
    }

    /**
     * Vergleicht zwei Instanzen von [MonetaryAmount]. Nicht signifikante
     * Nachkommastellen werden dabei ignoriert.
     *
     * @param amount Betrag eines `MonetaryAmount`, mit dem verglichen werid
     * @return `true` falls `amount >= this`.
     * @throws MonetaryException bei unterschiedlichen Waehrungen.
     */
    override fun isGreaterThanOrEqualTo(amount: MonetaryAmount): Boolean {
        return this.compareTo(amount) >= 0
    }

    /**
     * Vergleicht zwei Instanzen von [MonetaryAmount]. Nicht signifikante
     * Nachkommastellen werden dabei ignoriert.
     *
     * @param amount Betrag eines `MonetaryAmount`, mit dem verglichen werid
     * @return `true` falls `amount < this`.
     * @throws MonetaryException bei unterschiedlichen Waehrungen.
     */
    override fun isLessThan(amount: MonetaryAmount): Boolean {
        return this.compareTo(amount) < 0
    }

    /**
     * Vergleicht zwei Instanzen von [MonetaryAmount]. Nicht signifikante
     * Nachkommastellen werden dabei ignoriert.
     *
     * @param amount Betrag eines `MonetaryAmount`, mit dem verglichen werid
     * @return `true` falls `amount <= this`.
     * @throws MonetaryException bei unterschiedlichen Waehrungen.
     */
    override fun isLessThanOrEqualTo(amount: MonetaryAmount): Boolean {
        return this.compareTo(amount) <= 0
    }

    /**
     * Zwei Geldbetraege sind nur dann gleich, wenn sie die gleiche Waehrung
     * und den gleichen Betrag haben. Im Unterschied zu [.equals]
     * muessen die Betraege exakt gleich sein.
     *
     * @param other der andere Geldbetrag oder MonetaryAmount
     * @return true, falls Waehrung und Betrag gleich ist
     * @throws MonetaryException wenn die Waehrungen nicht uebereinstimmen
     */
    override fun isEqualTo(other: MonetaryAmount): Boolean {
        checkCurrency(other)
        return isNumberEqualTo(other.number)
    }

    private fun isNumberEqualTo(value: NumberValue): Boolean {
        val otherValue = toBigDecimal(value, context)
        return betrag.compareTo(otherValue) == 0
    }

    /**
     * Testet, ob der Betrag negativ ist.
     *
     * @return true bei negativen Betraegen
     */
    override fun isNegative(): Boolean {
        return betrag.compareTo(BigDecimal.ZERO) < 0
    }

    /**
     * Testet, ob der Betrag negativ oder Null ist.
     *
     * @return false bei positiven Betraegen
     */
    override fun isNegativeOrZero(): Boolean {
        return betrag.compareTo(BigDecimal.ZERO) <= 0
    }

    /**
     * Testet, ob der Betrag positiv ist.
     *
     * @return true bei positiven Betraegen
     */
    override fun isPositive(): Boolean {
        return betrag.compareTo(BigDecimal.ZERO) > 0
    }

    /**
     * Testet, ob der Betrag positiv oder Null ist.
     *
     * @return false bei negativen Betraegen
     */
    override fun isPositiveOrZero(): Boolean {
        return betrag.compareTo(BigDecimal.ZERO) >= 0
    }

    /**
     * Tested, ob der Betrag null ist.
     *
     * @return true, falls Betrag == 0
     */
    override fun isZero(): Boolean {
        return betrag.compareTo(BigDecimal.ZERO) == 0
    }

    /**
     * Returns the signum function of this `MonetaryAmount`.
     *
     * @return -1, 0, or 1 as the value of this `MonetaryAmount` is negative, zero, or
     * positive.
     */
    override fun signum(): Int {
        return toBigDecimal(number).signum()
    }

    /**
     * Liefert die Summe mit dem anderen Gelbetrag zurueck. Vorausgesetzt,
     * beide Betraege haben die gleichen Waehrungen. Einzige Ausnahem davon
     * ist die Addition von 0, da hier die Waehrung egal ist (neutrale
     * Operation).
     *
     * @param other value to be added to this `MonetaryAmount`.
     * @return `this + amount`
     * @throws ArithmeticException if the result exceeds the numeric capabilities
     * of this implementation class, i.e. the [MonetaryContext] cannot be adapted
     * as required.
     */
    override fun add(other: MonetaryAmount?): Geldbetrag {
        if (betrag.compareTo(BigDecimal.ZERO) == 0) {
            return valueOf(other!!)
        }
        val n = toBigDecimal(other!!.number, context)
        if (n.compareTo(BigDecimal.ZERO) == 0) {
            return this
        }
        checkCurrency(other)
        return valueOf(betrag.add(n), currency)
    }

    /**
     * Returns a `MonetaryAmount` whose value is `this -
     * amount`, and whose scale is `max(this.scale(),
     * subtrahend.scale()`.
     *
     * @param amount value to be subtracted from this `MonetaryAmount`.
     * @return `this - amount`
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     * the [MonetaryContext] cannot be adapted as required.
     */
    override fun subtract(amount: MonetaryAmount?): Geldbetrag {
        return add(amount!!.negate())
    }

    /**
     * Returns a `MonetaryAmount` whose value is <tt>(this
     * multiplicand)</tt>, and whose scale is `this.scale() +
     * multiplicand.scale()`.
     *
     * @param multiplicand value to be multiplied by this `MonetaryAmount`.
     * @return `this * multiplicand`
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     * the [MonetaryContext] cannot be adapted as required.
     */
    override fun multiply(multiplicand: Long): MonetaryAmount {
        return multiply(BigDecimal.valueOf(multiplicand))
    }

    /**
     * Liefert einen GeldBetrag, desseen Wert <tt>(this
     * multiplicand)</tt> und desse Genauigkeit (scale)
     * `this.scale() + multiplicand.scale()` entspricht.
     *
     * @param multiplicand Multiplikant (wird evtl. gerundet, wenn die
     * Genauigkeit zu hoch ist
     * @return `this * multiplicand`
     * @throws ArithmeticException bei "unendlich" oder "NaN" als Mulitiplikant
     */
    override fun multiply(multiplicand: Double): MonetaryAmount {
        return multiply(toBigDecimal(multiplicand))
    }

    /**
     * Returns a `MonetaryAmount` whose value is <tt>(this
     * multiplicand)</tt>, and whose scale is `this.scale() +
     * multiplicand.scale()`.
     *
     * @param multiplicand value to be multiplied by this `MonetaryAmount`. If the multiplicand's scale exceeds
     * the
     * capabilities of the implementation, it may be rounded implicitly.
     * @return `this * multiplicand`
     * @throws ArithmeticException if the result exceeds the numeric capabilities of this implementation class, i.e.
     * the [MonetaryContext] cannot be adapted as required.
     */
    override fun multiply(multiplicand: Number?): MonetaryAmount {
        val d = toBigDecimal(multiplicand!!, context)
        if (BigDecimal.ONE.compareTo(d) == 0) {
            return this
        }
        val multiplied = betrag.multiply(d)
        return valueOf(multiplied, currency)
    }

    /**
     * Returns a `MonetaryAmount` whose value is `this /
     * divisor`, and whose preferred scale is `this.scale() -
     * divisor.scale()`; if the exact quotient cannot be represented an `ArithmeticException`
     * is thrown.
     *
     * @param divisor value by which this `MonetaryAmount` is to be divided.
     * @return `this / divisor`
     * @throws ArithmeticException if the exact quotient does not have a terminating decimal expansion, or if the
     * result exceeds the numeric capabilities of this implementation class, i.e. the
     * [MonetaryContext] cannot be adapted as required.
     */
    override fun divide(divisor: Long): Geldbetrag {
        return divide(BigDecimal.valueOf(divisor))
    }

    /**
     * Returns a `MonetaryAmount` whose value is `this /
     * divisor`, and whose preferred scale is `this.scale() -
     * divisor.scale()`; if the exact quotient cannot be represented an `ArithmeticException`
     * is thrown.
     *
     * @param divisor value by which this `MonetaryAmount` is to be divided.
     * @return `this / divisor`
     * @throws ArithmeticException if the exact quotient does not have a terminating decimal expansion, or if the
     * result exceeds the numeric capabilities of this implementation class, i.e. the
     * [MonetaryContext] cannot be adapted as required.
     */
    override fun divide(divisor: Double): MonetaryAmount {
        return if (isInfinite(divisor)) {
            valueOf(BigDecimal.ZERO, currency)
        } else divide(BigDecimal.valueOf(divisor))
    }

    /**
     * Returns a `MonetaryAmount` whose value is `this /
     * divisor`, and whose preferred scale is `this.scale() -
     * divisor.scale()`; if the exact quotient cannot be represented an `ArithmeticException`
     * is thrown.
     *
     * @param divisor value by which this `MonetaryAmount` is to be divided.
     * @return `this / divisor`
     * @throws ArithmeticException if the exact quotient does not have a terminating decimal expansion, or if the
     * result exceeds the numeric capabilities of this implementation class, i.e. the
     * [MonetaryContext] cannot be adapted as required.
     */
    override fun divide(divisor: Number?): Geldbetrag {
        val d = toBigDecimal(divisor!!, context)
        return if (BigDecimal.ONE.compareTo(d) == 0) {
            this
        } else valueOf(betrag.setScale(4, RoundingMode.HALF_UP).divide(d, RoundingMode.HALF_UP), currency)
    }

    /**
     * Returns a `MonetaryAmount` whose value is `this % divisor`.
     *
     * The remainder is given by
     * `this.subtract(this.divideToIntegralValue(divisor).multiply(divisor)` . Note that this
     * is not the modulo operation (the result can be negative).
     *
     * @param divisor value by which this `MonetaryAmount` is to be divided.
     * @return `this % divisor`.
     * @throws ArithmeticException if `divisor==0`, or if the result exceeds the numeric capabilities of this
     * implementation class, i.e. the [MonetaryContext] cannot be adapted as
     * required.
     */
    override fun remainder(divisor: Long): Geldbetrag {
        return remainder(BigDecimal.valueOf(divisor))
    }

    /**
     * Liefert eine @code Geldbetrag} zurueck, dessen Wert
     * `this % divisor` entspricht. Der Betrag kann auch
     * negativ sein (im Gegensatz zur Modulo-Operation).
     *
     * @param divisor Wert, durch den der `Geldbetrag` geteilt wird.
     * @return `this % divisor`.
     */
    override fun remainder(divisor: Double): Geldbetrag {
        return if (isInfinite(divisor)) {
            valueOf(0, currency)
        } else remainder(toBigDecimal(divisor))
    }

    /**
     * Returns a `MonetaryAmount` whose value is `this % divisor`.
     *
     * The remainder is given by
     * `this.subtract(this.divideToIntegralValue(divisor).multiply(divisor)` . Note that this
     * is not the modulo operation (the result can be negative).
     *
     * @param divisor value by which this `MonetaryAmount` is to be divided.
     * @return `this % divisor`.
     * @throws ArithmeticException if `divisor==0`, or if the result exceeds the numeric capabilities of this
     * implementation class, i.e. the [MonetaryContext] cannot be adapted as
     * required.
     */
    override fun remainder(divisor: Number?): Geldbetrag {
        return valueOf(betrag.remainder(toBigDecimal(divisor!!, context)), currency)
    }

    /**
     * Liefert ein zwei-elementiges `Geldbatrag`-Array mit dem Ergebnis
     * `divideToIntegralValue` und`remainder`.
     *
     * @param divisor Teiler
     * @return ein zwei-elementiges `Geldbatrag`-Array
     * @throws ArithmeticException bei `divisor==0`
     * @see .divideToIntegralValue
     * @see .remainder
     */
    override fun divideAndRemainder(divisor: Long): Array<Geldbetrag> {
        return divideAndRemainder(BigDecimal.valueOf(divisor))
    }

    /**
     * Liefert ein zwei-elementiges `Geldbatrag`-Array mit dem Ergebnis
     * `divideToIntegralValue` und`remainder`.
     *
     * @param divisor Teiler
     * @return ein zwei-elementiges `Geldbatrag`-Array
     * @throws ArithmeticException bei `divisor==0`
     * @see .divideToIntegralValue
     * @see .remainder
     */
    override fun divideAndRemainder(divisor: Double): Array<Geldbetrag> {
        return if (isInfinite(divisor)) {
            toGeldbetragArray(BigDecimal.ZERO, BigDecimal.ZERO)
        } else divideAndRemainder(BigDecimal.valueOf(divisor))
    }

    /**
     * Liefert ein zwei-elementiges `Geldbetrag`-Array mit dem Ergebnis
     * `divideToIntegralValue` und`remainder`.
     *
     * @param divisor Teiler
     * @return ein zwei-elementiges `Geldbatrag`-Array
     * @throws ArithmeticException bei `divisor==0`
     * @see .divideToIntegralValue
     * @see .remainder
     */
    override fun divideAndRemainder(divisor: Number?): Array<Geldbetrag> {
        val numbers = betrag.divideAndRemainder(toBigDecimal(divisor!!, context))
        return toGeldbetragArray(*numbers)
    }

    private fun toGeldbetragArray(vararg numbers: BigDecimal): Array<Geldbetrag> {
        val betraege = Array<Geldbetrag>(numbers.size) { i -> valueOf(numbers[i], currency) }
        return betraege
    }

    /**
     * Liefert den Integer-Teil des Quotienten `this / divisor`
     * (abgerundet).
     *
     * @param divisor Teiler
     * @return Integer-Teil von `this / divisor`.
     * @throws ArithmeticException falls `divisor==0`
     * @see BigDecimal.divideToIntegralValue
     */
    override fun divideToIntegralValue(divisor: Long): Geldbetrag {
        return divideToIntegralValue(BigDecimal.valueOf(divisor))
    }

    /**
     * Liefert den Integer-Teil des Quotienten `this / divisor`
     * (abgerundet).
     *
     * @param divisor Teiler
     * @return Integer-Teil von `this / divisor`.
     * @throws ArithmeticException falls `divisor==0`
     * @see BigDecimal.divideToIntegralValue
     */
    override fun divideToIntegralValue(divisor: Double): Geldbetrag {
        return divideToIntegralValue(BigDecimal.valueOf(divisor))
    }

    /**
     * Liefert den Integer-Teil des Quotienten `this / divisor`
     * (abgerundet).
     *
     * @param divisor Teiler
     * @return Integer-Teil von `this / divisor`.
     * @throws ArithmeticException falls `divisor==0`
     * @see BigDecimal.divideToIntegralValue
     */
    override fun divideToIntegralValue(divisor: Number): Geldbetrag {
        return valueOf(betrag.divideToIntegralValue(toBigDecimal(divisor, context)), currency)
    }

    /**
     * Liefert eine `Geldbetrag`, dessen Wert (`this` * 10<sup>n</sup>)
     * entspricht.
     *
     * @param power 10er-Potenz (z.B. 3 fuer 1000)
     * @return berechneter Geldbetrag
     */
    override fun scaleByPowerOfTen(power: Int): Geldbetrag {
        //val scaled = betrag.scaleByPowerOfTen(power).setScale(context.maxScale, context.get(RoundingMode::class.java))
        val scaled = betrag.scaleByPowerOfTen(power)
        return roundedValueOf(scaled, getCurrency(), context)
    }

    /**
     * Returns a `MonetaryAmount` whose value is the absolute value of this
     * `MonetaryAmount`, and whose scale is `this.scale()`.
     *
     * @return `abs(this)`
     */
    override fun abs(): Geldbetrag {
        return if (betrag.compareTo(BigDecimal.ZERO) < 0) {
            negate()
        } else {
            this
        }
    }

    /**
     * Returns a `MonetaryAmount` whose value is `-this`, and whose scale is
     * `this.scale()`.
     *
     * @return `-this`.
     */
    override fun negate(): Geldbetrag {
        return valueOf(betrag.negate(), currency)
    }

    /**
     * Liefert immer eine positiven Geldbetrag.
     *
     * @return positiver Geldbetrag
     * @see BigDecimal.plus
     */
    override fun plus(): Geldbetrag {
        return if (betrag.compareTo(BigDecimal.ZERO) < 0) {
            negate()
        } else {
            this
        }
    }

    /**
     * Liefert einen `Geldbetrag`, der numerisch dem gleichen Wert
     * entspricht, aber ohne Nullen in den Nachkommastellen.
     *
     * @return im Priip der gleiche `Geldbetrag`, nur wird die Zahl
     * intern anders repraesentiert.
     */
    override fun stripTrailingZeros(): Geldbetrag {
        return if (isZero) {
            valueOf(BigDecimal.ZERO, getCurrency())
        } else valueOf(betrag.stripTrailingZeros(), getCurrency(), context)
    }

    /**
     * Vergleicht die Zahlenwerter der beiden Geldbetraege. Aber nur, wenn es
     * sich um die gleiche Waehrung handelt. Ansonsten wird die Waehrung als
     * Vergleichswert herangezogen. Dies fuehrt dazu, dass "CHF 1 < GBP 0" ist.
     * Dies ist leider durch das TCK so vorgegeben :-(
     *
     * @param other der andere Geldbetrag
     * @return 0 bei Gleicheit; negative Zahl, wenn dieser Geldbetrag kleiner
     * als der andere ist; sonst positive Zahl.
     */
    override fun compareTo(other: MonetaryAmount): Int {
        val compare = getCurrency().currencyCode.compareTo(other.currency.currencyCode)
        if (compare == 0) {
            val n = toBigDecimal(other.number)
            return betrag.compareTo(n)
        }
        return compare
    }

    /**
     * Vergleicht nur den Zahlenwert und ignoriert die Waehrung. Diese Methode
     * ist aus Kompatibiltaetsgruenden zur BigDecimal-Klasse enthalten.
     *
     * @param other der andere Betrag
     * @return 0 bei Gleicheit; negative Zahl, wenn die Zahle kleiner als die
     * andere ist, sonst positive Zahl.
     */
    operator fun compareTo(other: Number): Int {
        return this.compareTo(valueOf(other, currency))
    }

    /**
     * Liefert die entsprechende Waehrungseinheit ([CurrencyUnit]).
     *
     * @return die entsprechende [CurrencyUnit], not null.
     */
    override fun getCurrency(): CurrencyUnit {
        return currency
    }

    /**
     * Liefert den entsprechenden [NumberValue].
     *
     * @return der entsprechende [NumberValue], not null.
     */
    override fun getNumber(): NumberValue {
        return Zahlenwert(betrag)
    }

    /**
     * Liefert nur die Zahl als 'double' zurueck. Sie entspricht der
     * gleichnamigen Methode aus [BigDecimal].
     *
     * @return Zahl als 'double'
     * @see BigDecimal.toDouble
     */
    fun doubleValue(): Double {
        return betrag.toDouble()
    }

    /**
     * Hash-Code.
     *
     * @return a hash code value for this object.
     * @see Object.equals
     * @see System.identityHashCode
     */
    override fun hashCode(): Int {
        return betrag.hashCode()
    }

    /**
     * Zwei Betraege sind gleich, wenn Betrag und Waehrung gleich sind. Im
     * Unterschied zu [.isEqualTo] wird hier nur der
     * sichtbare Teil fuer den Vergleich herangezogen, d.h. Rundungsdifferenzen
     * spielen beim Vergleich keine Rolle.
     *
     * @param other der Geldbetrag, mit dem verglichen wird
     * @return true, falls (optisch) gleich
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Geldbetrag) {
            return false
        }
        return if (!hasSameCurrency(other)) {
            false
        } else this.toString() == other.toString()
    }

    private fun hasSameCurrency(other: MonetaryAmount): Boolean {
        return getCurrency() == other.currency
    }

    private fun checkCurrency(other: MonetaryAmount) {
        if (!hasSameCurrency(other)) throw LocalizedMonetaryException("different currencies", this, other)
    }

    /**
     * Liefert das Ergebnis des Operator **vom selben Typ**.
     *
     * @param operator Operator (nicht null)
     * @return ein Objekt desselben Typs (nicht null)
     * @see javax.money.MonetaryAmount.with
     */
    override fun with(operator: MonetaryOperator?): Geldbetrag {
        Objects.requireNonNull(operator)
        return try {
            operator!!.apply(this) as Geldbetrag
        } catch (ex: MonetaryException) {
            throw ex
        } catch (ex: RuntimeException) {
            throw LocalizedMonetaryException("operator failed", operator, ex)
        }
    }

    /**
     * Fraegt einen Wert an.
     *
     * @param query Anrfage (nicht null)
     * @return Ergebnis der Anfrage (kann null sein)
     * @see javax.money.MonetaryAmount.query
     */
    override fun <R> query(query: MonetaryQuery<R>?): R {
        Objects.requireNonNull(query)
        return try {
            query!!.queryFrom(this)
        } catch (ex: MonetaryException) {
            throw ex
        } catch (ex: RuntimeException) {
            throw LocalizedMonetaryException("query failed", query, ex)
        }
    }

    /**
     * Gibt den Betrag in Kurz-Format aus: ohne Nachkommastellen und mit dem
     * Waehrungssymbol.
     *
     * @return z.B. "$19"
     */
    fun toShortString(): String {
        return getSymbol(currency) + betrag.setScale(0, RoundingMode.HALF_UP)
    }

    /**
     * Um anzuzeigen, dass es ein Geldbtrag ist, wird zusaetzlich noch das
     * Waehrungszeichen (abhaengig von der eingestellten Locale) ausgegeben.
     *
     * @return z.B. "19.00 USD"
     * @see java.math.BigDecimal.toString
     */
    override fun toString(): String {
        return DEFAULT_FORMATTER.format(this)
    }

    /**
     * Hier wird der Geldbetrag mit voller Genauigkeit ausgegeben.
     *
     * @return z.B. "19.0012 USD"
     */
    fun toLongString(): String {
        val formatter = DecimalFormat.getInstance()
        formatter.minimumFractionDigits = context.maxScale
        formatter.minimumFractionDigits = context.maxScale
        return formatter.format(betrag) + " " + currency
    }



    /**
     * Dieser Validator ist fuer die Ueberpruefung von Geldbetraegen vorgesehen.
     *
     * @since 3.0
     */
    class Validator : KSimpleValidator<String> {
        /**
         * Validiert die uebergebene Zahl, ob sie sich als Geldbetrag eignet.
         *
         * @param value als String
         * @return die Zahl zur Weitervarabeitung
         */
        override fun validate(value: String): String {
            return try {
                valueOf(value).toString()
            } catch (ex: IllegalArgumentException) {
                throw InvalidValueException(value, "money_amount", ex)
            }
        }
    }



    companion object {

        private val FACTORY = GeldbetragFactory()
        private val DEFAULT_FORMATTER = GeldbetragFormatter()
        private val NUMBER_VALIDATOR = NumberValidator()
        private val VALIDATOR: KSimpleValidator<String> = Validator()

        /** Da 0-Betraege relativ haeufig vorkommen, spendieren wir dafuer eine eigene Konstante.  */
        @JvmField
        val ZERO = Geldbetrag(BigDecimal.ZERO)

        /** Der minimale Betrag, den wir unterstuetzen.  */
        @JvmField
        val MIN_VALUE = Geldbetrag(BigDecimal.valueOf(Long.MIN_VALUE))

        /** Der maximale Betrag, den wir unterstuetzen.  */
        @JvmField
        val MAX_VALUE = Geldbetrag(BigDecimal.valueOf(Long.MAX_VALUE))

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = ZERO

        /**
         * Hierueber kann eine Geldbetrag ueber die Anzahl an Cents angelegt
         * werden.
         *
         * @param cents Cent-Betrag, z.B. 42
         * @return Geldbetrag, z.B. 0.42$
         */
        @JvmStatic
        fun fromCent(cents: Long): Geldbetrag {
            return ofMinor(Waehrung.of("EUR"), cents)
        }

        /**
         * Legt einen Geldbetrag unter Angabe der Unter-Einheit an. So liefert
         * `ofMinor(EUR, 12345)` die Instanz fuer '123,45 EUR' zurueck.
         *
         * Die Methode wurde aus Kompatibitaetsgrunden zur Money-Klasse
         * hinzugefuegt.
         *
         * @param currency Waehrung
         * @param amountMinor Betrag der Unter-Einzeit (z.B. 12345 Cents)
         * @param fractionDigits Anzahl der Nachkommastellen
         * @return Geldbetrag
         * @since 1.0.1
         */
        @JvmOverloads
        @JvmStatic
        fun ofMinor(currency: CurrencyUnit, amountMinor: Long, fractionDigits: Int = currency.defaultFractionDigits): Geldbetrag {
            return of(BigDecimal.valueOf(amountMinor, fractionDigits), currency)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(other: String): Geldbetrag {
            return valueOf(other)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(other: String): Geldbetrag {
            return try {
                DEFAULT_FORMATTER.parse(other) as Geldbetrag
            } catch (ex: MonetaryParseException) {
                throw IllegalArgumentException(other, ex)
            }
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Long): Geldbetrag {
            return valueOf(Geldbetrag(value))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Long): Geldbetrag {
            return valueOf(Geldbetrag(value))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Double): Geldbetrag {
            return valueOf(Geldbetrag(value))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Double): Geldbetrag {
            return valueOf(Geldbetrag(value))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: String): Geldbetrag {
            return valueOf(value, currency)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: String): Geldbetrag {
            return valueOf(value, toCurrency(currency))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: Currency): Geldbetrag {
            return valueOf(value, currency)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: Currency): Geldbetrag {
            return valueOf(Geldbetrag(value, currency))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: CurrencyUnit): Geldbetrag {
            return valueOf(value, currency)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: CurrencyUnit): Geldbetrag {
            return valueOf(Geldbetrag(value, currency))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: String, monetaryContext: MonetaryContext): Geldbetrag {
            return valueOf(value, currency, monetaryContext)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: String, monetaryContext: MonetaryContext): Geldbetrag {
            return valueOf(value, Waehrung.of(currency), monetaryContext)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(value: Number, currency: CurrencyUnit, monetaryContext: MonetaryContext): Geldbetrag {
            return valueOf(value, currency, monetaryContext)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf".
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(value: Number, currency: CurrencyUnit, monetaryContext: MonetaryContext): Geldbetrag {
            return valueOf(Geldbetrag(value, currency, monetaryContext))
        }

        /**
         * Im Gegensatz zu valueOf wird hier keine [ArithmeticException]
         * geworfen, wenn Genauigkeit verloren geht. Stattdessen wird der
         * Wert gerundet.
         *
         * @param value Wert des andere Geldbetrags
         * @param currency Waehrung des anderen Geldbetrags
         * @param monetaryContext Kontext des anderen Geldbetrags
         * @return ein Geldbetrag
         * @since 4.0
         */
        @JvmStatic
        fun roundedValueOf(value: Number, currency: CurrencyUnit, monetaryContext: MonetaryContext): Geldbetrag {
            val roundedValue = toBigDecimalRounded(value, monetaryContext)
            return valueOf(Geldbetrag(roundedValue, currency, monetaryContext))
        }

        /**
         * Erzeugt einen Geldbetrag anhand des uebergebenen Textes und mittels
         * des uebergebenen Formatters.
         *
         * @param text z.B. "12,25 EUR"
         * @param formatter Formatter
         * @return Geldbetrag
         */
        @JvmOverloads
        @JvmStatic
        fun parse(text: CharSequence?, formatter: MonetaryAmountFormat = DEFAULT_FORMATTER): Geldbetrag {
            return from(formatter.parse(text))
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden of(..)-Methode und
         * wurde eingefuehrt, um mit der Money-Klasse aus "org.javamoney.moneta"
         * kompatibel zu sein.
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun from(other: MonetaryAmount): Geldbetrag {
            return of(other)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * Diese Methode ist identisch mit der entsprechenden valueOf(..)-Methode.
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun of(other: MonetaryAmount): Geldbetrag {
            return valueOf(other)
        }

        /**
         * Wandelt den angegebenen MonetaryAmount in einen Geldbetrag um. Um die
         * Anzahl von Objekten gering zu halten, wird nur dann tatsaechlich eine
         * neues Objekt erzeugt, wenn es sich nicht vermeiden laesst.
         *
         * In Anlehnung an [BigDecimal] heisst die Methode "valueOf" .
         *
         * @param other the other
         * @return ein Geldbetrag
         */
        @JvmStatic
        fun valueOf(other: MonetaryAmount): Geldbetrag {
            if (other is Geldbetrag) {
                return other
            }
            val value = other.number.numberValue(BigDecimal::class.java)
            return if (value == BigDecimal.ZERO) {
                ZERO
            } else Geldbetrag(value).withCurrency(other.currency)
        }

        /**
         * Validiert die uebergebene Zahl, ob sie sich als Geldbetrag eignet.
         *
         * @param zahl als String
         * @return die Zahl zur Weitervarabeitung
         */
        @JvmStatic
        fun validate(zahl: String): String {
            return VALIDATOR.validate(zahl)
        }

        private fun toBigDecimal(value: NumberValue): BigDecimal {
            return value.numberValue(BigDecimal::class.java)
        }

        private fun toBigDecimal(value: NumberValue, mc: MonetaryContext): BigDecimal {
            val n: Number = toBigDecimal(value)
            return toBigDecimal(n, mc)
        }

        private fun toBigDecimal(value: Double): BigDecimal {
            NUMBER_VALIDATOR.verifyNumber(value)
            return BigDecimal.valueOf(value)
        }

        private fun isInfinite(divisor: Double): Boolean {
            if (divisor == Double.POSITIVE_INFINITY || divisor == Double.NEGATIVE_INFINITY) {
                return true
            }
            if (java.lang.Double.isNaN(divisor)) {
                throw ArithmeticException("invalid number: NaN")
            }
            return false
        }

        private fun toBigDecimal(value: Number, monetaryContext: MonetaryContext): BigDecimal {
            val n: BigDecimal = toBigDecimal(value)
            val rounded: BigDecimal = toBigDecimalRounded(value, monetaryContext)
            if (n.compareTo(rounded) != 0) {
                throw LocalizedArithmeticException(value, "lost_precision")
            }
            return n
        }

        private fun toBigDecimalRounded(value: Number, monetaryContext: MonetaryContext): BigDecimal {
            val n: BigDecimal = toBigDecimal(value)
            var roundingMode = monetaryContext.get(RoundingMode::class.java)
            if (roundingMode == null) {
                roundingMode = RoundingMode.HALF_UP
            }
            val scale = monetaryContext.maxScale
            return if (scale <= 0) {
                n
            } else {
                val scaled = n.setScale(scale, roundingMode)
                scaled
            }
        }

        private fun toBigDecimal(value: Number): BigDecimal {
            if (value is BigDecimal) {
                return value
            } else if (value is Zahlenwert) {
                return value.numberValue(BigDecimal::class.java)
            }
            return BigDecimal.valueOf(value.toDouble())
        }

    }

    /**
     * Erzeugt einen Geldbetrag in der angegebenen Waehrung.
     */
    init {
        this.betrag = toBigDecimal(betrag, context)
        this.currency = currency
        this.context = context
    }

}