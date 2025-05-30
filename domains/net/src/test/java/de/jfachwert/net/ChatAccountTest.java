package de.jfachwert.net;/*
 * Copyright (c) 2017-2025 by Oliver Boehm
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

import de.jfachwert.FachwertTest;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Unit-Tests fuer {@link ChatAccount}-Klasse.
 *
 * @author oliver (ob@jfachwert.de)
 */
public final class ChatAccountTest extends FachwertTest {

    private static final Logger LOG = Logger.getLogger(ChatAccountTest.class.getName());

    /**
     * Zum Testen brauchen wir ein Test-Objekt. Dies muss hierueber von den
     * abgeleiteten Unit-Tests bereitgestellt werden. Und zwar muss jedesmal
     * der gleiche Fachwert erzeugt werden, weil sonst der equals-Test nicht
     * funktioniert.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected ChatAccount createFachwert() {
        return new ChatAccount("Jabber", "alice@example.com");
    }

    /**
     * Null-Werte werden bereits von Kotlin zurueckgewiesen, waehrend wir in
     * der Java-Version hier eine IllegalArgumentException erwartet haben.
     * Jetzt akzeptieren wir beides.
     */
    @Test
    public void testChatAccountInvalid() {
        assertThrows(RuntimeException.class, () -> new ChatAccount(ChatDienst.SONSTIGER, null));
    }
    
    /**
     * Unit-Test fuer {@link ChatAccount#getChatDienst()}.
     */
    @Test
    public void testGetChatDienst() {
        ChatAccount jabberAccount = createFachwert();
        assertEquals(ChatDienst.JABBER, jabberAccount.getChatDienst());
    }

    /**
     * Test-Methode fuer {@link ChatAccount#toString()}.
     */
    @Test
    public void testToStringJabber() {
        ChatAccount jabber = new ChatAccount(ChatDienst.JABBER, "bob@example.com");
        assertEquals("Jabber: bob@example.com", jabber.toString());
        assertEquals(jabber, ChatAccount.of(jabber.toString()));
    }

    @Test
    public void testOf() {
        ChatAccount jabber = ChatAccount.of("Jabber: bob@example.com");
        assertEquals(ChatDienst.JABBER, jabber.getChatDienst());
    }

    @Test
    public void testOfCaching() {
        String s = "bsky: oboehm";
        ChatAccount a1 = ChatAccount.of(s);
        ChatAccount a2 = ChatAccount.of(s);
        assertSame(a1, a2);
        if (forceGC()) {
            ChatAccount a3 = ChatAccount.of(s);
            assertNotSame(a1, a3);
            assertEquals(a1, a3);
        } else {
            LOG.info("GC wurde nicht durchgefuehrt.");
        }
    }

    /**
     * Test-Methode fuer {@link ChatAccount#toString()}.
     */
    @Test
    public void testToStringSonstiger() {
        ChatAccount threema = new ChatAccount(ChatDienst.SONSTIGER, "Threema", "AB3DE6GH");
        assertEquals("Threema: AB3DE6GH", threema.toString());
    }

    /**
     * Test eines gueltigen Jabber-Accounts.
     */
    @Test
    public void testChatAccountJabber() {
        ChatAccount jabber = new ChatAccount(ChatDienst.JABBER, "+4917234567890@aspsms.swissjabber.ch");
        assertEquals(ChatDienst.JABBER, jabber.getChatDienst());
    }

    /**
     * Test eines ungueltigen Jabber-Accounts.
     */
    @Test
    public void testChatAccountJabberInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new ChatAccount(ChatDienst.JABBER, "a@b@c"));
    }

    /**
     * Test eines gueltigen ICQ-Accounts.
     */
    @Test
    public void testChatAccountIcq() {
        ChatAccount icq = new ChatAccount(ChatDienst.ICQ, "123456");
        assertEquals(ChatDienst.ICQ, icq.getChatDienst());
    }

    /**
     * Test eines ungueltigen ICQ-Accounts (ICQ-Nummber sind mindestens
     * 5-stellig).
     */
    @Test
    public void testChatAccountIcqInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new ChatAccount(ChatDienst.ICQ, "9999"));
    }

    /**
     * Test eines ungueltigen ICQ-Accounts (ICQ-Nummber sind mindestens
     * 5-stelling).
     */
    @Test
    public void testChatAccountNoNumber() {
        assertThrows(IllegalArgumentException.class, () -> new ChatAccount(ChatDienst.ICQ, "0x12345"));
    }

    /**
     * Hier wird der String-Ctor geprueft, ob der Input-String auch richtig
     * interpretiert wird.
     */
    @Test
    public void testChatAccountString() {
        ChatAccount account = new ChatAccount("Jabber: duke@sun.com");
        assertEquals(ChatDienst.JABBER, account.getChatDienst());
        assertEquals("duke@sun.com", account.getAccount());
    }

}
