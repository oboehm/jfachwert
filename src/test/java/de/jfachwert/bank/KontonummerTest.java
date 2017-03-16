package de.jfachwert.bank;

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
 * (c)reated 16.03.2017 by oboehm (ob@jfachwert.de)
 */

import de.jfachwert.AbstractFachwertTest;

/**
 * Unit-Tests fuer {@link Kontonummer}-Klasse.
 *
 * @author oboehm
 */
public final class KontonummerTest extends AbstractFachwertTest {

    /**
     * Die Kontonummer stammt aus dem IBANTest.
     *
     * @return "0006605605" als Kontonummer
     */
    @Override
    protected Kontonummer createFachwert() {
        return new Kontonummer("0006605605");
    }

}