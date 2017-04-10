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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.AbstractFachwert;

/**
 * Klasse PLZ.
 *
 * @author oboehm
 * @since x.x (10.04.2017)
 */
public class PLZ extends AbstractFachwert<String> {

    /**
     * Hierueber wird eine Postleitzahl angelegt.
     *
     * @param plz z.B. "70839" oder "D-70839"
     */
    public PLZ(String plz) {
        super(plz);
    }

}
