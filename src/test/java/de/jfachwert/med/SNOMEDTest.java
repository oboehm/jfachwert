/*
 * Copyright (c) 2023 by Oli B.
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
 * (c)reated 27.12.23 by oboehm
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwertTest;
import org.junit.jupiter.api.Test;
import patterntesting.runtime.junit.ObjectTester;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit-Tests fuer {@link SNOMED}.
 *
 * @author oboehm
 * @since 5.1 (27.12.23)
 */
public final class SNOMEDTest extends AbstractFachwertTest<String, SNOMED> {

    @Override
    protected SNOMED createFachwert(String code) {
        return SNOMED.of(code);
    }

    /**
     * Liefert einen gueltigen SNOMED-Code.
     *
     * @return "763158003"
     */
    @Override
    protected String getCode() {
        return "763158003";
    }

    @Test
    void testWithSameCode() {
        SNOMED codeOnly = new SNOMED("373873005:860781008=362943005");
        SNOMED withDisplay = new SNOMED("373873005:860781008=362943005",
                "Pharmaceutical / biologic product (product) : Has product characteristic (attribute) = Manual method (qualifier value)");
        ObjectTester.assertEquals(codeOnly, withDisplay);
    }

    @Test
    void getDisplay() {
        SNOMED s = SNOMED.of("763158003");
        assertThat(s.getDisplay(), not(emptyString()));
    }

    @Test
    void ofWithDisplay() {
        SNOMED s1 = SNOMED.of("373873005", "Pharmaceutical / biologic product");
        SNOMED s2 = SNOMED.of("373873005");
        ObjectTester.assertEquals(s1, s2);
        assertSame(s1, s2);
    }

}
