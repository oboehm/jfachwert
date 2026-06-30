/*
 * Copyright (c) 2026 by Oliver Boehm
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
 * (c)reated 30.06.26 by oboehm (ob@oasd.de)
 */
package de.jfachwert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * In der Klasse AbstractNumericFachwertTest sind die Tests zusammengefasst,
 * die fuer alle numerischen Fachwert-Klassen gelten, die von
 * AbstractNumericFachwert abgeleitetet sind.
 *
 * @param <T> der numerische Typ (z.B. Integer, Long, BigDecimal)
 * @param <S> der Selbst-Typ (CRTP)
 */
public abstract class AbstractNumericFachwertTest<T extends Number, S extends AbstractNumericFachwert<T, S>>
        extends AbstractFachwertTest<T, S> {

    /**
     * Liefert einen numerischen Fachwert zum Testen.
     *
     * @return numerischer Fachwert
     */
    protected AbstractNumericFachwert<T, S> createNumericFachwert() {
        return (AbstractNumericFachwert<T, S>) createFachwert();
    }

    @Test
    public void testIntValue() {
        AbstractNumericFachwert<T, S> fachwert = createNumericFachwert();
        int expected = fachwert.getCode().intValue();
        assertEquals(expected, fachwert.intValue());
    }

}
