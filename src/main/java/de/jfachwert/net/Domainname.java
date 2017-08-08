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
 * (c)reated 08.08.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.*;
import org.apache.commons.lang3.*;

/**
 * Ueber den Domain-Namen wird ein Rechner im Internet adressiert. Man kann
 * ihn zwar auch ueber seine IP-Adresse ansprechen, aber kann man sich als
 * Normalsterblicher schwer merken.
 * <p>
 * Ein Domainname besteht aus mindestens aus Teilen: einem Hostnamen (z. B.
 * ein Firmenname), einem Punkt und einer Top-Level-Domain.
 * </p>
 *
 * @author oboehm
 * @since 0.4 (08.08.2017)
 */
public class Domainname extends AbstractFachwert<String> {

    /**
     * Legt eine Instanz an.
     *
     * @param name gueltiger Domain-Name
     */
    public Domainname(String name) {
        super(name);
    }

    /**
     * Liefert die Top-Level-Domain (TLD) zurueck.
     *
     * @return z.B. "de"
     */
    public String getTLD() {
        return StringUtils.substringAfterLast(this.getCode(), ".");
    }

}
