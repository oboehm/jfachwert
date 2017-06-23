package de.jfachwert.net;/*
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
 * (c)reated 23.06.2017 by oboehm (ob@oasd.de)
 */

import de.jfachwert.*;

/**
 * Unit-Teests fuer de.jfachwert.net.EMailAdresse.
 *
 * @author oboehm
 */
public final class EMailAdresseTest extends AbstractFachwertTest {

    /**
     * Zum Testen generieren wir eine gueltige Email-Adresse.
     *
     * @return eine Test-Email-Adresse
     */
    @Override
    protected Fachwert createFachwert() {
        return new EMailAdresse("test@fachwert.de");
    }

}
