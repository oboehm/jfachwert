package de.jfachwert

import de.jfachwert.pruefung.NullValidator

/**
 * Zwischenklasse fuer [AbstractFachwert]e, deren interner Code ein
 * [Number] ist. Damit wird der Typ-Parameter <T> auf Zahlen eingeschraenkt.
 * Diese abstrakte Oberklasse dient dazu, um Kurzformen für [Number]-Methoden
 * wie [Number#intValue] zur Verfuegung zu stellen.
 *
 * @param T konkreter Number-Typ (z.B. Int, Long, BigDecimal)
 * @param S Selbst-Typ (CRTP) fuer Comparable
 * @param code der numerische Wert
 * @param validator Validator zur Pruefung
 */
abstract class AbstractNumericFachwert<T : Number, S : AbstractNumericFachwert<T, S>>
    protected constructor(code: T, validator: KSimpleValidator<T> = NullValidator())
    : AbstractFachwert<T, S>(code, validator) {

    /**
     * Liefert den internen numerischen Code als int-Wert zurueck.
     *
     * @return int-Wert des Codes
     */
    fun intValue(): Int {
        return code.toInt()
    }

    /**
     * Liefert den internen numerischen Code als long-Wert zurueck.
     *
     * @return long-Wert des Codes
     */
    fun longValue(): Long {
        return code.toLong()
    }

    /**
     * Liefert den internen numerischen Code als float-Wert zurueck.
     *
     * @return float-Wert des Codes
     */
    fun floatValue(): Float {
        return code.toFloat()
    }

    /**
     * Liefert den internen numerischen Code als double-Wert zurueck.
     *
     * @return double-Wert des Codes
     */
    fun doubleValue(): Double {
        return code.toDouble()
    }

    /**
     * Liefert den internen numerischen Code als byte-Wert zurueck.
     *
     * @return byte-Wert des Codes
     */
    fun byteValue(): Byte {
        return code.toByte()
    }

    /**
     * Liefert den internen numerischen Code als short-Wert zurueck.
     *
     * @return short-Wert des Codes
     */
    fun shortValue(): Short {
        return code.toShort()
    }

}