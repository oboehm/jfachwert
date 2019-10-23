/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 11.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.jfachwert.Fachwert
import de.jfachwert.math.Prozent
import java.math.BigDecimal
import java.util.*
import java.util.function.Function
import javax.money.MonetaryAmount

/**
 * Die Mehrwertsteuer wird auf den normalen (Netto-)Preis aufgeschlagen.
 * In Deutschland gibt es 2 Mehrwersteuer-Sätze: den Standardsatz von 19% und
 * den ermaessigten Steuersatz von 7%.
 */
@JsonSerialize(using = ToStringSerializer::class)
open class Mehrwertsteuer (val prozent: Prozent) : Fachwert, Comparable<Mehrwertsteuer> {

    constructor(satz: String) : this(Prozent.of(satz))

    companion object {

        private val WEAK_CACHE = WeakHashMap<Prozent, Mehrwertsteuer>()

        /** Standard-Mehrwertsteuersatz in Deutschland. */
        @JvmField
        val DE_NORMAL = of("19%")

        /** Ermaessigter Mehrwertsteuersatz in Deutschland. */
        @JvmField
        val DE_REDUZIERT = of("7%")

        /** Standard-Mehrwertsteuersatz in der Schweiz. */
        @JvmField
        val CH_NORMAL = of("7.7%")

        /** Ermaessigter Mehrwertsteuersatz in der Schweiz. */
        @JvmField
        val CH_REDUZIERT = of("2.5%")

        /** Sonder-Mehrwertsteuersatz in der Schweiz (fuer Beherbergungsdienstleistungen). */
        @JvmField
        val CH_SONDER = of("3.7%")

        /**
         * Die of-Methode liefert fuer denselben Prozentwert immer dasselbe
         * Objekt zurueck. Bevorzugt sollte man diese Methode verwenden, um
         * die Anzahl der Objekte gering zu halten.
         *
         * @param satz z.B. "19%"
         * @return "19%" als Mehrwertsteuer-Objekt
         */
        @JvmStatic
        fun of(satz: String): Mehrwertsteuer {
            return of(Prozent.of(satz))
        }

        /**
         * Die of-Methode liefert fuer denselben Prozentwert immer dasselbe
         * Objekt zurueck. Bevorzugt sollte man diese Methode verwenden, um
         * die Anzahl der Objekte gering zu halten.
         *
         * @param satz als Prozentwert, z.B. "19%"
         * @return "19%" als Mehrwertsteuer-Objekt
         */
        @JvmStatic
        fun of(satz: Prozent): Mehrwertsteuer {
            return WEAK_CACHE.computeIfAbsent(satz, Function(::Mehrwertsteuer))
        }

    }

    /**
     * Hier wird die Mehrwertsteuer auf den Netto-Betrag aufgeschlagen.
     *
     * @return Brutto-Betrag
     */
    fun nettoZuBrutto(netto: MonetaryAmount): MonetaryAmount {
        return netto.add(betragVonNetto(netto))
    }

    /**
     * Hier wird die Mehrwertsteuer vom Brutto-Betrag abgezogen.
     *
     * @return Netto-Betrag
     */
    fun bruttoZuNetto(brutto: MonetaryAmount): MonetaryAmount {
        return brutto.divide(BigDecimal.ONE.add(prozent.toBigDecimal()))
    }

    /**
     * Errechnet aus dem Brutto-Betrag den entsprechenden
     * Mehrwertsteuer-Betrag.
     *
     * @return Mehrwertsteuer-Betrag
     */
    fun betragVonBrutto(brutto: MonetaryAmount): MonetaryAmount {
        return brutto.subtract(bruttoZuNetto(brutto))
    }

    /**
     * Errechnet aus dem Netto-Betrag den entsprechenden
     * Mehrwertsteuer-Betrag.
     *
     * @return Mehrwertsteuer-Betrag
     */
    fun betragVonNetto(netto: MonetaryAmount): MonetaryAmount {
         return netto.multiply(prozent)
    }

    /**
     * Vergleicht zwei Mehrwertsteuersaetze. Wenn [other] ein kleinerer
     * Mehrwertsteuersatz ist, wird eine positive Zahl zurueckgegeben.
     * Bei Gleichheit wird 0 zurueckgegeben, ansonsten eine negative Zahl.
     */
    override fun compareTo(other: Mehrwertsteuer): Int {
        return prozent.compareTo(other.prozent)
    }

    override fun toString(): String {
        return prozent.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Mehrwertsteuer) return false
        return prozent == other.prozent
    }

    override fun hashCode(): Int {
        return prozent.hashCode()
    }


}