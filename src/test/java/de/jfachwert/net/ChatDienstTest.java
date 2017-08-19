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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit-Tests fuer {@link ChatDienst}-Klasse.
 */
public class ChatDienstTest {

    /**
     * Unit-Test fuer {@link ChatDienst#toChatDienst(String)}
     */
    @Test
    public void toChatDienst() {
        assertEquals(ChatDienst.JABBER, ChatDienst.toChatDienst("Jabber"));
    }

    /**
     * Unit-Test fuer {@link ChatDienst#toChatDienst(String)}
     */
    @Test
    public void toChatDienstSonstiger() {
        assertEquals(ChatDienst.SONSTIGER, ChatDienst.toChatDienst("gibts net"));
    }

}