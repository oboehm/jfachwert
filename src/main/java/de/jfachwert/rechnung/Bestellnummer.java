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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 10.07.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.rechnung;

import de.jfachwert.*;

/**
 * Oftmals findet sich auf Rechnungen auch eine Bestellnummer, die man bei
 * der Bezahlung mit angegeben muss.
 *
 * @author oboehm
 * @since 0.3 (10.07.2017)
 */
public class Bestellnummer extends AbstractFachwert<String> {

    /**
     * Erzeugt eine Bestellnummer.
     *
     * @param nummer z.B. "000002835042"
     */
    public Bestellnummer(String nummer) {
        super(nummer);
    }

}
