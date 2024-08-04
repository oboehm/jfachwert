/*
 * Copyright (c) 2024 by Oli B.
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
 * (c)reated 30.07.24 by oboehm
 */
package de.jfachwert.bank;

import de.jfachwert.FachwertTest;

/**
 * Unit-Tests fuer {@link Waehrung}-Klasse.
 */
class WaehrungTest extends FachwertTest {

    @Override
    protected de.jfachwert.money.Waehrung createFachwert() {
        return Waehrung.of("EUR");
    }

}
