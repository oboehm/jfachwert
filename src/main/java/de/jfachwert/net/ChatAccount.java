/*
 * Copyright (c) 2017 by Oliver Boehm
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
package de.jfachwert.net;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;
import de.jfachwert.util.ToFachwertSerializer;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Die Klasse ChatAccount steht fuer einen Account bei einem der uebleichen
 * Chat-Dienst wie ICQ, Skype oder Jabber.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.4 (08.08.2017)
 */
@JsonSerialize(using = ToFachwertSerializer.class)
public class ChatAccount implements Fachwert {

    private final ChatDienst chatDienst;
    private final String dienstName;
    private final String account;

    /**
     * Zerlegt den uebergebenen String in seine Einzelteile, um damit den
     * ChatAccount zu instanziieren. Bei der Zerlegung wird folgeden Heuristik
     * angwendet:
     * <ul>
     *     <li>zuserst kommt der Dienst, gefolgt von einem Doppelpunkt,</li>
     *     <li>danach kommt der Name bzw. Account.</li>
     * </ul>
     * 
     * @param chatAccount z.B. "Twitter: oboehm"
     */
    public ChatAccount(String chatAccount) {
        this(split(chatAccount));
    }
    
    private ChatAccount(String[] values) {
        this(ChatDienst.toChatDienst(values[0]), values[0], values[1]);
    }

    /**
     * Erzeugt einen neuen ChatAccount aus der uebergebenen Map.
     *
     * @param map mit den einzelnen Elementen fuer "chatDienst", "dienstName"
     *            und "account".
     */
    @JsonCreator
    public ChatAccount(Map<String, String> map) {
        this(ChatDienst.toChatDienst(map.get("chatDienst")), map.get("dienstName"), map.get("account"));
    }

    /**
     * Instanziiert einen Chat-Account.
     *
     * @param dienst z.B. "ICQ"
     * @param account z.B. 211349835 fuer ICQ
     */
    public ChatAccount(String dienst, String account) {
        this(ChatDienst.toChatDienst(dienst), dienst, account);
    }

    /**
     * Instanziiert eine Chat-Account.
     *
     * @param dienst z.B. "ICQ"
     * @param account z.B. 211349835 fuer ICQ
     */
    public ChatAccount(ChatDienst dienst, String account) {
        this(dienst, null, account);
    }

    /**
     * Instanziiert eine Chat-Account.
     *
     * @param chatDienst z.B. SONSTIGER
     * @param dienstName z.B. "WhatsApp"
     * @param account z.B. 211349835 fuer ICQ
     */
    public ChatAccount(ChatDienst chatDienst, String dienstName, String account) {
        this.chatDienst = chatDienst;
        this.dienstName = dienstName;
        this.account = (String) chatDienst.getValidator().verify(account);
    }
    
    private static String[] split(String value) {
        String[] splitted = StringUtils.trimToEmpty(value).split(":\\s+");
        if (splitted.length != 2) {
            throw new LocalizedIllegalArgumentException(value, "chat_service");
        }
        return splitted;
    }

    /**
     * Liefert den Dienst zum Account zurueck.
     *
     * @return z.B. JABBER
     */
    public ChatDienst getChatDienst() {
        return chatDienst;
    }

    /**
     * Liefert den Dienst zum Account zurueck.
     *
     * @return z.B. "Jabber"
     */
    public String getDienstName() {
        if (this.chatDienst == ChatDienst.SONSTIGER) {
            return dienstName;
        } else {
            return this.chatDienst.toString();
        }
    }

    /**
     * Liefert den Account-Namen zurueck.
     *
     * @return z.B. "+4917234567890@aspsms.swissjabber.ch"
     */
    public String getAccount() {
        return account;
    }

    /**
     * Beim Vergleich ignorieren wir Gross- und Kleinschreibung, weil das
     * vermutlich keine Rolle spielen duerfte. Zumindest ist mir kein
     * Chat-Dienst bekannt, wo zwischen Gross- und Kleinschreibung
     * unterschieden wird.
     *
     * @param obj der andere Chat-Account
     * @return true oder false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChatAccount)) {
            return false;
        }
        ChatAccount other = (ChatAccount) obj;
        return this.dienstName.equalsIgnoreCase(other.dienstName) && this.account.equalsIgnoreCase(other.account);
    }

    /**
     * Die Hashcode-Implementierung stuetzt sich nur auf den Accout ab.
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return this.account.hashCode();
    }

    /**
     * Ausgabe des Chat-Accounts zusammen mit der Dienstbezeichnung.
     *
     * @return z.B. "Jabber: bob@example.com"
     */
    @Override
    public String toString() {
        return this.getDienstName() + ": " + this.getAccount();
    }

    /**
     * Liefert die einzelnen Attribute eines ChatAccounts als Map.
     *
     * @return Attribute als Map
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("chatDienst", getChatDienst());
        map.put("dienstName", getDienstName());
        map.put("account", getAccount());
        return map;
    }
    
}
