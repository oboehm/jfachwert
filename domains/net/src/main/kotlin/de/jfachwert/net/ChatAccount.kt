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
 * (c)reated 19.08.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.net

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.jfachwert.KFachwert
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException
import de.jfachwert.util.ToFachwertSerializer
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Die Klasse ChatAccount steht fuer einen Account bei einem der uebleichen
 * Chat-Dienst wie ICQ, Skype oder Jabber.
 *
 * @author oliver (ob@aosd.de)
 * @since 0.4 (08.08.2017)
 */
@JsonSerialize(using = ToFachwertSerializer::class)
open class ChatAccount(val chatDienst: ChatDienst, private val dienstName: String?, account: String) : KFachwert {

    /**
     * Liefert den Account-Namen zurueck.
     *
     * @return z.B. "+4917234567890@aspsms.swissjabber.ch"
     */
    val account: String

    /**
     * Zerlegt den uebergebenen String in seine Einzelteile, um damit den
     * ChatAccount zu instanziieren. Bei der Zerlegung wird folgeden Heuristik
     * angwendet:
     *
     *  * zuserst kommt der Dienst, gefolgt von einem Doppelpunkt,
     *  * danach kommt der Name bzw. Account.
     *
     * @param chatAccount z.B. "Twitter: oboehm"
     */
    constructor(chatAccount: String) : this(split(chatAccount)) {}

    private constructor(values: Array<String>) : this(ChatDienst.of(values[0]), values[0], values[1]) {}

    /**
     * Erzeugt einen neuen ChatAccount aus der uebergebenen Map.
     *
     * @param map mit den einzelnen Elementen fuer "chatDienst", "dienstName"
     * und "account".
     */
    @JsonCreator
    constructor(map: Map<String, String>) : this(ChatDienst.of(map["chatDienst"]), map["dienstName"], map["account"]!!) {
    }

    /**
     * Instanziiert einen Chat-Account.
     *
     * @param dienst z.B. "ICQ"
     * @param account z.B. 211349835 fuer ICQ
     */
    constructor(dienst: String, account: String) : this(ChatDienst.of(dienst), dienst, account) {}

    /**
     * Instanziiert eine Chat-Account.
     *
     * @param dienst z.B. "ICQ"
     * @param account z.B. 211349835 fuer ICQ
     */
    constructor(dienst: ChatDienst, account: String) : this(dienst, null, account) {}

    init {
        this.account = chatDienst.validator.verify(account)
    }

    /**
     * Liefert den Dienst zum Account zurueck.
     *
     * @return z.B. "Jabber"
     */
    fun getDienstName(): String {
        return if (chatDienst == ChatDienst.SONSTIGER) {
            dienstName!!
        } else {
            chatDienst.toString()
        }
    }

    /**
     * Beim Vergleich ignorieren wir Gross- und Kleinschreibung, weil das
     * vermutlich keine Rolle spielen duerfte. Zumindest ist mir kein
     * Chat-Dienst bekannt, wo zwischen Gross- und Kleinschreibung
     * unterschieden wird.
     *
     * @param other der andere Chat-Account
     * @return true oder false
     */
    override fun equals(other: Any?): Boolean {
        if (other !is ChatAccount) {
            return false
        }
        return getDienstName().equals(other.getDienstName(), ignoreCase = true) &&
                account.equals(other.account, ignoreCase = true)
    }

    /**
     * Die Hashcode-Implementierung stuetzt sich nur auf den Account ab.
     *
     * @return hashcode
     */
    override fun hashCode(): Int {
        return account.hashCode()
    }

    /**
     * Ausgabe des Chat-Accounts zusammen mit der Dienstbezeichnung.
     *
     * @return z.B. "Jabber: bob@example.com"
     */
    override fun toString(): String {
        return getDienstName() + ": " + account
    }

    /**
     * Liefert die einzelnen Attribute eines ChatAccounts als Map.
     *
     * @return Attribute als Map
     */
    override fun toMap(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["chatDienst"] = chatDienst
        map["dienstName"] = getDienstName()
        map["account"] = account
        return map
    }



    companion object {

        private val WEAK_CACHE = WeakHashMap<String, ChatAccount>()

        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = ChatAccount(ChatDienst.SONSTIGER, "", "")

        private fun split(value: String): Array<String> {
            val splitted = StringUtils.trimToEmpty(value).split(":\\s+".toPattern()).toTypedArray()
            if (splitted.size != 2) {
                throw LocalizedIllegalArgumentException(value, "chat_service")
            }
            return splitted
        }

        /**
         * Liefert einen Chat-Account.
         *
         * @param name z.B. "Twitter: oboehm"
         * @return Chat-Account
         */
        @JvmStatic
        fun of(name: String): ChatAccount {
            val copy = String(name.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy) { chatAccount: String -> ChatAccount(chatAccount) }
        }

    }

}