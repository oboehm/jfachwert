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
package de.jfachwert;

import de.jfachwert.bank.*;
import de.jfachwert.steuer.*;
import org.junit.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Hier sind einige Tests versammelt, um allgemeine Dinge fuer die
 * Fachwert-Klassen zu pruefen.
 *
 * @author oboehm
 */
public final class FachwertTest {

    /**
     * Auch wenn zwei unterschiedliche Typen zufaellig den gleichen Wert haben,
     * sind sind sie nicht gleich.
     */
    @Test
    public void testEqualWithDifferentTypes() {
        Fachwert bic = new BIC("12345678900");
        Fachwert stnr = new Steuernummer("12345678903");
        assertThat(bic, not(stnr));
    }

}
