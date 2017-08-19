package de.jfachwert.net;/*
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

import de.jfachwert.AbstractFachwertTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Tests fuer {@link ChatAccount}-Klasse.
 *
 * @author oliver (ob@jfachwert.de)
 */
public final class ChatAccountTest extends AbstractFachwertTest {

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
        ChatAccount threema = new ChatAccount(ChatDienst.JABBER, "bob@example.com");
        assertEquals("Jabber: bob@example.com", threema.toString());
    }

    /**
     * Test-Methode fuer {@link ChatAccount#toString()}.
     */
    @Test
    public void testToStringSonstiger() {
        ChatAccount threema = new ChatAccount(ChatDienst.SONSTIGER, "Threema", "AB3DE6GH");
        assertEquals("Threema: AB3DE6GH", threema.toString());
    }

}
