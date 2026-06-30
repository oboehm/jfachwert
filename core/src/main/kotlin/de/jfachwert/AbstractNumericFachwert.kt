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

}