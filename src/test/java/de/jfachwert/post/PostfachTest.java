package de.jfachwert.post;/*
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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.*;

/**
 * Unit-Tests fuer die {@link Postfach}-Klasse.
 *
 * @author oboehm
 */
public final class PostfachTest extends AbstractFachwertTest {

    /**
     * Zum Testen generieren wir ein normales Postfach mit Nummer und PLZ.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Postfach createFachwert() {
        return new Postfach(815, new PLZ("09876"), new Ort("Nirwana"));
    }

}
