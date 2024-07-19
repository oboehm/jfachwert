/*
 * Copyright (c) 2024 by Oliver Boehm
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
 * (c)reated 13.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-Tests fuer {@link FachwertFactory}-Klasse. Die urspruengliche
 * Test-Klasse wurde ins jfachwert-Modul verschoben, da dort die
 * benoetigten Abhaengigkeiten zur Verfuegung stehen.
 *
 * @author oboehm
 */
public class FachwertFactoryTest {
    
    private static final FachwertFactory FACTORY = FachwertFactory.getInstance();

    @Test
    public void testRegister() {
        Class<NullFachwert> testClass = NullFachwert.class;
        FACTORY.register(testClass.getName());
        Map<String, Class<? extends KFachwert>> registeredClasses = FACTORY.getRegisteredClasses();
        assertTrue(registeredClasses.containsValue(testClass));
    }



    public static class NullFachwert implements KFachwert {
        @Override
        public Map<String, Object> toMap() {
            return new HashMap<>();
        }
        @Override
        public boolean isValid() {
            return true;
        }
    }

}
