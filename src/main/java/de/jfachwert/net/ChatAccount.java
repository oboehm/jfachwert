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

import de.jfachwert.Fachwert;

/**
 * Die Klasse ChatAccount steht fuer einen Account bei einem der uebleichen
 * Chat-Dienst wie ICQ, Skype oder Jabber.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.4 (08.08.2017)
 */
public class ChatAccount implements Fachwert {

    private final String dienst;
    private final String account;

    /**
     * Instanziiert eine Chat-Account.
     *
     * @param dienst z.B. "ICQ"
     * @param account z.B. 211349835 fuer ICQ
     */
    public ChatAccount(String dienst, String account) {
        this.dienst = dienst;
        this.account = account;
    }

    /**
     * Liefert den Dienst zum Account zurueck.
     *
     * @return z.B. "Jabber"
     */
    public String getDienst() {
        return dienst;
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
        return this.dienst.equalsIgnoreCase(other.dienst) && this.account.equalsIgnoreCase(other.account);
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
        return this.getDienst() + ": " + this.getAccount();
    }

}
