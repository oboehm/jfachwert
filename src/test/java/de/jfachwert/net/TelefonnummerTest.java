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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 04.09.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.*;

/**
 * Unit-Tests fuer {@link Telefonnummer}-Klasse.
 *
 * @author oboehm
 */
public final class TelefonnummerTest extends AbstractFachwertTest {

    /**
     * Zum Testen nehmen wir eine fiktive Telefonnummer (aus Wikipedia).
     *
     * @return "+49 30 12345-67"
     */
    @Override
    protected Telefonnummer createFachwert() {
        return new Telefonnummer("+49 30 12345-67");
    }

}
