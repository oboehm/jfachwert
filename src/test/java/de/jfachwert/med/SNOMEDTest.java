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

}
