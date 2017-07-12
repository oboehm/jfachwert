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
 * (c)reated 12.07.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.*;

/**
 * Unit-Tests fuer {@link Rechnungsmonat}-Klasse.
 *
 * @author oboehm
 */
public final class RechnungsmonatTest extends AbstractFachwertTest {

    /**
     * Zum Testen nehmen wir den Juli 2017.
     *
     * @return Juli 2017
     */
    @Override
    protected Fachwert createFachwert() {
        return new Rechnungsmonat(7, 2017);
    }

}
