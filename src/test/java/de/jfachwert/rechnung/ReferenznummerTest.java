package de.jfachwert.rechnung;/*
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
 * (c)reated 10.07.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.*;

/**
 * Unit-Tests fuer {@link Referenznummer}-Klasse.
 *
 * @author oboehm
 */
public class ReferenznummerTest extends AbstractFachwertTest {

    /**
     * Die Test-Referenznummer ist fiktiv und dient nur zum Testen.
     *
     * @return Test-Referenznummer
     */
    @Override
    protected Fachwert createFachwert() {
        return new Referenznummer("42");
    }

}