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

import de.jfachwert.AbstractFachwertTest;

/**
 * Unit-Teests fuer de.jfachwert.post.Anschrift.
 *
 * @author oboehm
 * @since 0.2 (12.05.2017)
 */
public final class AnschriftTest extends AbstractFachwertTest {

    /**
     * Zum Testen genererien wir eine einfache Anschrift.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Anschrift createFachwert() {
        Adresse adresse = new AdresseTest().createFachwert();
        return new Anschrift("Daisy", adresse);
    }

}
