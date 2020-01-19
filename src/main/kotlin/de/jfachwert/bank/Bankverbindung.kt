/*
 * Copyright (c) 2017-2020 by Oliver Boehm
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
 * (c)reated 06.07.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.jfachwert.Fachwert
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import de.jfachwert.util.ToFachwertSerializer
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Eine Bankverbindung besteht aus dem Zahlungsempfaenger oder Kontoinhaber,
 * einer IBAN und einer BIC. Bei Inlandsverbindungen kann die BIC entfallen,
 * weswegen sie hier auch optional ist.
 *
 * @author oliver (ob@aosd.de)
 * @since 0.3.0
 */
@JsonSerialize(using = ToFachwertSerializer::class)
open class Bankverbindung

@JvmOverloads constructor(val kontoinhaber: String, iban: IBAN, bic: BIC? = null) : Fachwert {

    val iban: IBAN
    private val bic: BIC?

    /**
     * Zerlegt den uebergebenen String in Name, IBAN und (optional) BIC.
     * Folgende Heuristiken werden fuer die Zerlegung angewendet:
     *  * Reihenfolge ist Name, IBAN und BIC, evtl. durch Kommata
     *    getrennt
     *  * IBAN wird durch "IBAN" (grossgeschrieben) eingeleitet
     *  * BIC wird durch "BIC" (grossgeschrieben) eingeleitet,
     *    ist aber optional.
     *
     * @param bankverbindung z.B. "Max Muster, IBAN DE41300606010006605605"
     */
    constructor(bankverbindung: String) : this(split(bankverbindung)) {}

    private constructor(bankverbindung: Array<Any?>) :
            this(bankverbindung[0].toString(), bankverbindung[1] as IBAN, bankverbindung[2] as BIC?) {}

    /**
     * Erzeugt eine neue Bankverbindung aus der uebergebenen Map.
     *
     * @param map mit den einzelnen Elementen fuer "kontoinhaber", "iban" und
     * "bic".
     */
    @JsonCreator
    constructor(map: Map<String, String>) :
            this(Objects.toString(map["kontoinhaber"], ""), IBAN(map["iban"]), BIC(map["bic"]!!)) {
    }

    /**
     * Da die BIC bei Inlands-Ueberweisungen optional ist, wird sie hier als
     * [Optional] zurueckgegeben.
     *
     * @return BIC
     */
    fun getBic(): Optional<BIC> {
        return if (bic == null) Optional.empty() else Optional.of(bic)
    }

    override fun hashCode(): Int {
        return iban.hashCode()
    }

    /**
     * Zwei Bankverbindungen sind gleich, wenn IBAN und BIC uebereinstimmen.
     *
     * @param other die andere Bankverbindung
     * @return true bei Gleichheit
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Bankverbindung) {
            return false
        }
        return iban == other.iban && bic == other.bic
    }

    override fun toString(): String {
        val buf = StringBuilder(kontoinhaber)
        buf.append(", IBAN ")
        buf.append(iban)
        if (getBic().isPresent) {
            buf.append(", BIC ")
            buf.append(getBic().get())
        }
        return buf.toString()
    }

    /**
     * Liefert die einzelnen Attribute einer Bankverbindung als Map.
     *
     * @return Attribute als Map
     */
    override fun toMap(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["kontoinhaber"] = kontoinhaber
        map["iban"] = iban
        getBic().ifPresent { b: BIC -> map["bic"] = b }
        return map
    }

    companion object {

        /** Null-Konstante fuer Initialisierungen.  */
        val NULL = Bankverbindung("", IBAN.NULL, BIC.NULL)

        private fun split(bankverbindung: String): Array<Any?> {
            val splitted = arrayOfNulls<String>(3)
            splitted[0] = stripSeparator(StringUtils.substringBefore(bankverbindung, "IBAN"))
            splitted[1] = stripSeparator(StringUtils.substringAfter(bankverbindung, "IBAN"))
            if (StringUtils.isBlank(splitted[1])) {
                throw LocalizedIllegalArgumentException(bankverbindung, "bank_account")
            }
            if (splitted[1]!!.contains("BIC")) {
                splitted[2] = stripSeparator(StringUtils.substringAfter(splitted[1], "BIC"))
                splitted[1] = stripSeparator(StringUtils.substringBefore(splitted[1], "BIC"))
            } else {
                splitted[2] = ""
            }
            val values = arrayOfNulls<Any>(3)
            values[0] = splitted[0]
            values[1] = IBAN(splitted[1])
            values[2] = if (splitted[2]!!.isEmpty()) null else BIC(splitted[2]!!)
            return values
        }

        private fun stripSeparator(raw: String): String {
            var value = raw.trim { it <= ' ' }
            if (value.endsWith(",")) {
                value = value.substring(0, value.length - 1)
            }
            return value
        }
    }

    init {
        this.iban = iban
        this.bic = bic
    }

}