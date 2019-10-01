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
 * (c)reated 01.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.math;

import de.jfachwert.FachwertTest;

import java.math.BigDecimal;

/**
 * Unit-Test fuer {@link Prozent}-Klasse.
 *
 * @author oboehm
 */
public final class ProzentTest extends FachwertTest {

    /**
     * Zum Testen verwenden wir 10 Prozent.
     *
     * @return 10 %
     */
    @Override
    protected Prozent createFachwert() {
        return new Prozent(BigDecimal.TEN);
    }

}