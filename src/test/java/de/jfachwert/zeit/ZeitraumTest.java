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
 * (c)reated 19.01.24 by oboehm
 */
package de.jfachwert.zeit;

import de.jfachwert.FachwertTest;

/**
 * Unit-Tests fuer die {@link Zeitraum}-Klasse.
 *
 * @author oboehm
 * @since 5.2 (19.01.24)
 */
public final class ZeitraumTest extends FachwertTest {

    @Override
    protected Zeitraum createFachwert() {
        return Zeitraum.of("2018-01-24 - 2024-01-19");
    }

}
