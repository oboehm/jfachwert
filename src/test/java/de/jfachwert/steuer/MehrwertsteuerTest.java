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
 * (c)reated 10.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer;

import de.jfachwert.FachwertTest;
import de.jfachwert.math.Prozent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-Test fuer [Zins]-Klasse.
 *
 * @author oboehm
 */
public final class MehrwertsteuerTest extends FachwertTest {

    /**
     * Zum Testen nehmen wir den deutschen Mehrwertsteuersatz von 19%.
     *
     * @return Test-Objekt zum Testen
     */
    @Override
    protected Mehrwertsteuer createFachwert() {
        return new Mehrwertsteuer(Prozent.of("19%"));
    }

    @Test
    public void testGetProzent() {
        Mehrwertsteuer mwst = new Mehrwertsteuer(Prozent.TEN);
        assertEquals(Prozent.TEN, mwst.getProzent());
    }

    @Test
    @Override
    public void testToString() {
        Prozent p = Prozent.of("7%");
        assertEquals(p.toString(), new Mehrwertsteuer(p).toString());
    }

}